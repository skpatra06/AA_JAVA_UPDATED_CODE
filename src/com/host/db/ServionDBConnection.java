/**
ServionDBConnectionManager_Singleton.java - The sole purpose of this class is to ensure that only 
one instance of the class is created for the entire application and that instance 
is shared across multiple clients / callers
Copyright (c) 2008 Servion Global Solutions All Rights Reserved.
Version : 2.1
Author : Mohammed Rafi
Created on : Janauary 25, 2010
Updated on : April 08, 2008 - Created different instances for Common, Postpaid and Prepaid
 **/
package com.host.db;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import com.host.rest.SessionAPIHost;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class ServionDBConnection {
	Connection con = null;
    ComboPooledDataSource comboPooledDataSource=null;
	Object GlobalData=null;
	public Connection getConnection(String pStrDSName, SessionAPIHost data) throws Exception
	{
		Long cStart =0L;
		Long cEnd =0L;
		int intDBDownDuration=2;
		long longDownTime=0;
		Boolean boolDownFlag=false;
		try{
			HashMap  hashMapDSObj = (HashMap) data.getGlobalLevelData("G_DATASOURCE");
			data.addToLog(pStrDSName+ " GLOBAL CONNECTIONPOOL  : "+hashMapDSObj);
			if((hashMapDSObj.containsKey(pStrDSName)))
			{
				cStart = System.currentTimeMillis();
				try{
					longDownTime=Long.parseLong((String)data.getGlobalLevelData("G_INT_"+pStrDSName+"_DBDOWN_TIME"));
					boolDownFlag=(Boolean)data.getGlobalLevelData("G_BOOL_"+pStrDSName+"_DBDOWN_FLAG");
					longDownTime = 2;
					boolDownFlag = false;
				}catch(Exception e){
					e.printStackTrace();
				}
				if(boolDownFlag==true){
					long longDbDownDiff=cStart-longDownTime;
					intDBDownDuration=Integer.parseInt((String) data.getGlobalLevelData("G_INT_"+pStrDSName+"_DBDOWN_DURATION"));
					long longDBDownDuration=intDBDownDuration*60000;
					if(longDbDownDiff>longDBDownDuration){
						data.addToLog("Resuming "+pStrDSName+" After : "+longDbDownDiff/1000+" Sec");
						data.getGlobalLevelData("G_BOOL_"+pStrDSName+"_DBDOWN_FLAG");
					}else{
						data.addToLog(pStrDSName+ " Database will be Down for :"+longDBDownDuration+" Sec");
					}
					data.setGlobalLevelData("S_STR_EXITSTATE"+pStrDSName, "ER");
					data.setGlobalLevelData("S_STR_ERR_REASON"+pStrDSName,pStrDSName+" Exception occurred while retrieving Connection Object. Connection is NULL");
					con=null;
				}else{
					
					comboPooledDataSource=(ComboPooledDataSource) hashMapDSObj.get(pStrDSName);
					if(null==comboPooledDataSource) {
						data.addToLog(pStrDSName+" Active Connection not available ");
						con=null;
					}else{
						con=comboPooledDataSource.getConnection();
						cEnd = System.currentTimeMillis();
						data.addToLog("Time to acquire the "+pStrDSName+" connection from the Database "+(cEnd-cStart)+" Ms "+"");
					}
				}
			}else {
				data.addToLog("Pool Datasource Not avalible in DS List, Not able to retreived Connection Object: "+ pStrDSName);
				con=null;
			}
		}catch(Exception e){
			cEnd = System.currentTimeMillis();
			long longExceptionTime=System.currentTimeMillis();
			if(null==con) {
				data.ErrorLog(pStrDSName+" Connection Object is Invalid / Null, invalidating the object : "+getDate("dd/MM/yyyy HH:mm:ss"),e);
				try{
					if(con !=null)
					{
						con.close();
						con =null;
					}
				}catch (Exception e1)
				{
					data.ErrorLog(pStrDSName+" DB Exception Occured While Invalidating Connection Object :"+getDate("dd/MM/yyyy HH:mm:ss"),e1);
				}
			}else{
				data.ErrorLog(pStrDSName+" DB Exception Occured While Retreiving Connection  : "+getDate("dd/MM/yyyy HH:mm:ss"),e);

			}
			data.setGlobalLevelData("G_INT_"+pStrDSName+"_DBDOWN_TIME",String.valueOf(longExceptionTime));
			data.setGlobalLevelData("G_BOOL_"+pStrDSName+"_DBDOWN_FLAG",true);
			data.addToLog(pStrDSName +" will be stopped for "+intDBDownDuration+" Sec"+" due to Database is not functioning properly");
			data.setGlobalLevelData("S_STR_RESULT"+pStrDSName, "1");
			data.setGlobalLevelData("S_STR_EXITSTATE"+pStrDSName, "ER");
			data.setGlobalLevelData("S_STR_ERR_REASON"+pStrDSName, pStrDSName +" will be stopped for "+intDBDownDuration+" Sec"+" due to Database is not functioning properly");
			data.addToLog(pStrDSName+ " Time to Invalidate the connection from Connection pool : "+(cEnd-cStart));
		}
		
		return con;
	}
	
	public String getDate(String format){
		java.util.Date date = Calendar.getInstance().getTime();  
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);  
		String strDate = dateFormat.format(date);
		return strDate;  
	}
}