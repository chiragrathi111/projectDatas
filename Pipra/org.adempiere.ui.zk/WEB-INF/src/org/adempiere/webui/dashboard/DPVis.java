package org.adempiere.webui.dashboard;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MOrg;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class DPVis extends DashboardPanel implements EventListener<Event> {

  private static final long serialVersionUID = 1L;

  public DPVis() {
    super();
    this.setSclass("activities-box");
  }

  @Override
  public void onEvent(Event event) throws Exception {
  }

  public Collection<JsonObject> getData() {
    JsonObject warehouseData = new JsonObject();
    Collection<JsonObject> list = new ArrayList<>();
    LinkedHashMap<String, JsonObject> map = new LinkedHashMap<String, JsonObject>();
    String locatorTypeName = "locatorTypeName";

    PreparedStatement pstm = null;
    ResultSet rs = null;
    try {
      Properties ctx = Env.getCtx();
      int ad_client_id = Env.getAD_Client_ID(ctx);
      
      String sql = "SELECT w.m_warehouse_id AS warehouseID,w.name AS warehouseName,ml.isdefault,(SELECT COUNT(*)FROM adempiere.M_Locator l\n"
        		+ "LEFT JOIN (SELECT M_Locator_ID, COALESCE(SUM(QtyOnHand), 0) AS TotalQty FROM adempiere.M_StorageOnHand GROUP BY M_Locator_ID) ms \n"
        		+ "ON l.M_Locator_ID = ms.M_Locator_ID WHERE l.m_warehouse_id = w.m_warehouse_id AND COALESCE(ms.TotalQty, 0) = 0) AS emptyCount,\n"
        		+ "(SELECT COUNT(*)FROM adempiere.m_locator WHERE m_warehouse_id = w.m_warehouse_id) AS total_count,lt.name AS locator_type,\n"
        		+ "ml.value AS location_values,CASE WHEN (SELECT COALESCE(SUM(QtyOnHand), 0) FROM adempiere.M_StorageOnHand\n"
        		+ "WHERE M_Locator_ID = ml.m_locator_id) = 0 THEN 'false' ELSE 'true' END AS booleanValue,COALESCE((SELECT SUM(QtyOnHand) \n"
        		+ "FROM adempiere.M_StorageOnHand WHERE M_Locator_ID = ml.m_locator_id), 0) AS TotalQty,((SELECT COUNT(*)FROM adempiere.m_locator\n"
        		+ "WHERE m_warehouse_id = w.m_warehouse_id) - (SELECT COUNT(*)FROM adempiere.M_Locator l LEFT JOIN (\n"
        		+ "SELECT M_Locator_ID, COALESCE(SUM(QtyOnHand), 0) AS TotalQty FROM adempiere.M_StorageOnHand GROUP BY M_Locator_ID) ms \n"
        		+ "ON l.M_Locator_ID = ms.M_Locator_ID WHERE l.m_warehouse_id = w.m_warehouse_id AND COALESCE(ms.TotalQty, 0) = 0)) * 100 / \n"
        		+ "(SELECT COUNT(*)FROM adempiere.m_locator WHERE m_warehouse_id = w.m_warehouse_id) AS occupancy_percentage\n"
        		+ "FROM adempiere.m_warehouse w JOIN adempiere.m_locator ml ON ml.m_warehouse_id = w.m_warehouse_id\n"
        		+ "JOIN adempiere.m_locatortype lt ON ml.m_locatortype_id = lt.m_locatortype_id WHERE ml.ad_client_id =  " + ad_client_id + " \n"
        		+ "GROUP BY w.m_warehouse_id, w.name, lt.name, ml.value,ml.m_locator_id, ml.isdefault;";
      
      pstm = DB.prepareStatement(sql.toString(), null);
      rs = pstm.executeQuery();

      
      while (rs.next()) {
        String warehouseId = rs.getString("warehouseID");
        String warehouseName = rs.getString("warehouseName");
        String occupancyPercents = rs.getString("occupancy_percentage");
        String locatorType = rs.getString("locator_type");
        String locatorName = rs.getString("location_values");
        Boolean status = rs.getBoolean("booleanValue");
        int totalQty = rs.getInt("TotalQty");

        String[] words = locatorType.split("\\s+");
        StringBuilder camelCase = new StringBuilder();

        for (String word : words) {
          if (!word.isEmpty()) {
            if (camelCase.length() == 0) {
              camelCase.append(word.toLowerCase());
            } else {
              camelCase.append(word.substring(0, 1).toUpperCase());
              camelCase.append(word.substring(1).toLowerCase());
            }
          }
        }

        if (warehouseData.size() == 0 || warehouseData.get(warehouseId) == null) {
          JsonObject warehouseInfo = new JsonObject();
          warehouseInfo.addProperty("warehouseId", warehouseId);
          warehouseInfo.addProperty("warehouseName", warehouseName);
          warehouseInfo.addProperty("occupancyPercents", occupancyPercents);
          warehouseInfo.addProperty("warehouseTotalQty", totalQty);

          JsonObject locations = new JsonObject();
          JsonObject locationArray = new JsonObject();
          locationArray.addProperty(locatorName, status);
          locations.addProperty(locatorTypeName, locatorType);
          locations.add("locators", locationArray);
          JsonArray array = new JsonArray();
          array.add(locations);
          warehouseInfo.add("locations", array);
          warehouseData.add(warehouseId, warehouseInfo);
          map.put(warehouseId, warehouseInfo);

        } else {
          JsonObject warehouseInfo = (JsonObject) warehouseData.get(warehouseId);
          JsonPrimitive warehouseTotalQty =   (JsonPrimitive) warehouseInfo.get("warehouseTotalQty");
          warehouseInfo.addProperty("warehouseTotalQty", warehouseTotalQty.getAsInt() + totalQty);
          JsonArray locations = (JsonArray) warehouseInfo.get("locations");
          boolean flag = false;
          for (int i = 0; i < locations.size(); i++) {
            JsonObject obj = (JsonObject) locations.get(i);
            String lName = obj.get(locatorTypeName).toString();
            lName = lName.replaceAll("\"", "");

            if (lName.equalsIgnoreCase(locatorType)) {
              JsonObject lType = (JsonObject) obj.get("locators");
              lType.addProperty(locatorName, status);
              obj.addProperty(locatorTypeName, locatorType);
              obj.add("locators", lType);
              locations.remove(i);
              locations.add(obj);
              warehouseInfo.add("locations", locations);
              flag = false;
              break;
            } else {
              flag = true;
            }

          }
          if (flag == true) {
            JsonObject locationArray = new JsonObject();
            JsonObject name = new JsonObject();
            name.addProperty(locatorName, status);
            locationArray.addProperty(locatorTypeName, locatorType);
            locationArray.add("locators", name);
            locations.add(locationArray);
            warehouseInfo.add("locations", locations);
          }
          warehouseData.add(warehouseId, warehouseInfo);
          map.put(warehouseId, warehouseInfo);
        }
      }
      pstm.close();
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();

    }
    list = map.values();
    for(JsonObject warehouseInfo : list) {
    	if(warehouseInfo.get("warehouseTotalQty").getAsInt() < 0) {
    		  warehouseInfo.addProperty("warehouseTotalQty", 0);
    	}
    	
    }
    return list;
  }
  
}
