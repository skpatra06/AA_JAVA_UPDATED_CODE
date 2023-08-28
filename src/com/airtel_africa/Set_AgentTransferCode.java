package com.airtel_africa;

import com.audium.server.session.APIBase;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.host.db.DBInterface;
import com.host.dbbean.DB_Bean;
import com.host.rest.SessionAPIHost;
import com.util.Utilities;
import com.util.smsAPIdataFormation;

public class Set_AgentTransferCode extends ActionElementBase{

	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception {
		Utilities util=new Utilities(data);
		try {
			util.setTransferData(data, "DIRECT_AGENT_TRANSFER");
			//RepeatCallerDB
			if(hostCall(data,strElementName,"SP_GET_REPEAT_CALLER",util)) {
				data.addToLog(strElementName,"Repeat Caller DB Status : Successfully Fetched Repeat Caller Count .....");
			}
			smsAPIdataFormation smsAPIdataFormation=new smsAPIdataFormation();
			String smsAirtelAppTemp=(String) data.getSessionData("S_AIRTEL_APP_SMSTEMP_ID");
			String smsFalg = smsAPIdataFormation.loadSMS(data,util, smsAirtelAppTemp, strElementName);
			if("Y".equalsIgnoreCase(smsFalg)) {
				if(util.hostCall(data, "Send_SMS_Notification")){
					data.addToLog(strElementName, " Successfully Download Airtel App Link Send to The Customer .... ");
				}else {
					data.addToLog(strElementName, "Failed To Send The Airtel App Link ....");
				}
			}		
		}catch (Exception e) {
			util.errorLog(strElementName, e);
		}
	}
	public boolean hostCall(APIBase data,String strElementName,String dbName,Utilities util) {
		boolean flag=false;
		int logLevel=0;
		SessionAPIHost sessionApi=null;
		try {
			sessionApi=new SessionAPIHost(data, logLevel);
			Object objBean=data.getApplicationAPI().getGlobalData(dbName);
			DBInterface dbInterface=new DBInterface(strElementName, sessionApi,(DB_Bean) objBean );
			dbInterface.executeSP();
			if(util.IsValidDBSUResponse()) 
			{
				flag=true;
			}
		}catch(Exception e) {
			util.errorLog("REST CLIENT :", e);
		}
		return flag;
	}

}
