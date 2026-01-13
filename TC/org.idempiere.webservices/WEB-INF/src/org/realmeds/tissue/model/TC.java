package org.realmeds.tissue.model;

import java.math.BigDecimal;

public interface TC {
	BigDecimal getQuantity();
    Object getM_Product(); // You might need to adjust the return type if it's specific to TCIn or TCOut
    Object getM_Locator(); 

}
