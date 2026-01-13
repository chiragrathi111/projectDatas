package com.pipra.rwpl.model;

import java.lang.reflect.Method;
import org.compiere.model.MProcess;
import org.compiere.process.ProcessInfo;
import org.compiere.util.Trx;

public class ProcessExecuter {
	
	public static boolean executeProcess(MProcess process, ProcessInfo pi) {
        Trx trx = null;
        try {
            trx = Trx.get(Trx.createTrxName(), true);
            
            Method startProcess = MProcess.class.getDeclaredMethod(
                "startProcess", 
                String.class, 
                ProcessInfo.class, 
                Trx.class, 
                boolean.class
            );
            
            startProcess.setAccessible(true);
            
            boolean success = (boolean) startProcess.invoke(
                process,
                process.getProcedureName(),
                pi,
                trx,
                false
            );
            
            if (success) {
                trx.commit();
                return true;
            } else {
                trx.rollback();
                return false;
            }
        } catch (Exception e) {
            if (trx != null) trx.rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (trx != null) trx.close();
        }
    }

}
