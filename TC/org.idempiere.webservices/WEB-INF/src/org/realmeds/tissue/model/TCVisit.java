package org.realmeds.tissue.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Properties;

import javax.activation.DataSource;
//import javax.jms.Connection;
import javax.swing.JOptionPane;

import org.compiere.util.DB;
import org.compiere.util.Env;
import org.realmeds.tissue.moduller.X_TC_Visit;

public class TCVisit extends X_TC_Visit{
	
	private static final long serialVersionUID = 1L;
//	private DataSource dataSource;
	public TCVisit(Properties ctx, int TC_visit_ID, String trxName) {
		super(ctx, TC_visit_ID, trxName);
		// TODO Auto-generated constructor stub
	}
	
	public TCVisit(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	@Override
		protected boolean beforeDelete() {
			// TODO Auto-generated method stub
			return super.beforeDelete();
		}
	@Override
		protected boolean beforeSave(boolean newRecord) {	
//		if (getc_uuid() == null) {
//			setc_uuid(TCUtills.generateUUID());
//		}
// 		String message = validateDate(getdate());
// 		if (!message.isEmpty()) {
// // 	        JOptionPane.showMessageDialog(null, message, "Date Validation", JOptionPane.WARNING_MESSAGE);
// 			Calendar today = Calendar.getInstance();
//  	        today.set(Calendar.HOUR_OF_DAY, 0);
//  	        today.set(Calendar.MINUTE, 0);
//  	        today.set(Calendar.SECOND, 0);
//  	        today.set(Calendar.MILLISECOND, 0);
// // 	        date.setTime(today.getTimeInMillis());
//  	        setdate(new Timestamp(today.getTimeInMillis()));
// 		}
			return super.beforeSave(newRecord);
		}
	@Override
		protected boolean afterDelete(boolean success) {
			// TODO Auto-generated method stub
			return super.afterDelete(success);
		}
	
	public void setdate(Timestamp date) {

//		String message = validateDate(date);
//		if (!message.isEmpty()) {
// 	        JOptionPane.showMessageDialog(null, message, "Date Validation", JOptionPane.WARNING_MESSAGE);
// 	    // Replace the date with today's date
// 	        Calendar today = Calendar.getInstance();
// 	        today.set(Calendar.HOUR_OF_DAY, 0);
// 	        today.set(Calendar.MINUTE, 0);
// 	        today.set(Calendar.SECOND, 0);
// 	        today.set(Calendar.MILLISECOND, 0);
// 	        date.setTime(today.getTimeInMillis());
//		}
		super.setdate(date);
	}
	@Override
		protected boolean afterSave(boolean newRecord, boolean success) {
//		int recordId = get_ID();
//		TCVisit visit = new TCVisit(Env.getCtx(), recordId,get_TrxName());
//		Calendar today = Calendar.getInstance();
//	        today.set(Calendar.HOUR_OF_DAY, 0);
//	        today.set(Calendar.MINUTE, 0);
//	        today.set(Calendar.SECOND, 0);
//	        today.set(Calendar.MILLISECOND, 0);
//////	        
//	        visit.setdate(new Timestamp(today.getTimeInMillis()));
//	        visit.saveEx();
//	        setDate.setTime(today.getTimeInMillis());
//	        setdate(today.getTimeInMillis());
//		int recordId = get_ID();
//		TCVisit visit = new TCVisit(Env.getCtx(), recordId,get_TrxName());
//		Timestamp date = visit.getdate();
//		Timestamp selectedDate = validateDate(date);
//		String sql = "UPDATE tc_visit SET date = ? WHERE tc_visit_id = ?";
//		DB.executeUpdate(sql, new Object[] { selectedDate, visit.get_ID() }, false, null);
////		visit.setdate(selectedDate);
//		visit.saveEx();
		
//		if (!message.isEmpty()) {
////	        JOptionPane.showMessageDialog(null, message, "Date Validation", JOptionPane.WARNING_MESSAGE);
//	    }
//		Calendar today = Calendar.getInstance();
//        today.set(Calendar.HOUR_OF_DAY, 0);
//        today.set(Calendar.MINUTE, 0);
//        today.set(Calendar.SECOND, 0);
//        today.set(Calendar.MILLISECOND, 0);
//		String sql = "UPDATE tc_visit SET date = ? WHERE tc_visit_id = ?";
//        DB.executeUpdate(sql, new Object[] { today.getTimeInMillis(), visit.get_ID() }, false, null);
////        this.re
			return super.afterSave(newRecord, success);
		}
	
	public Timestamp validateDate(Timestamp selectedDate) {
        if (selectedDate != null) {
            // Get today's date without time
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // Get the selected date without time
            Calendar selected = Calendar.getInstance();
            selected.setTime(selectedDate);
            selected.set(Calendar.HOUR_OF_DAY, 0);
            selected.set(Calendar.MINUTE, 0);
            selected.set(Calendar.SECOND, 0);
            selected.set(Calendar.MILLISECOND, 0);

            // Compare the dates
            if (selected.before(today)) {
                selectedDate.setTime(today.getTimeInMillis());
//                visit.setdate(new Timestamp(today.getTimeInMillis()));
//                setdate(selectedDate);
//                visit.saveEx();
//                Connection connection = null;
//                try {
//                    String sql = "UPDATE tc_visit SET date = ? WHERE tc_visit_id = ?";
//                    DB.executeUpdate(sql, new Object[] { selectedDate, visit.get_ID() }, false, null);
//                    // Trigger UI update or pop-up here
//                } catch (Exception e) {
//                    return "Error updating the date: " + e.getMessage();
//                }
    			
                return selectedDate;
            }
            return selectedDate; // No error message
        } else {
            throw new IllegalArgumentException("This field is required. Please select a date.");
        }
    }

}
