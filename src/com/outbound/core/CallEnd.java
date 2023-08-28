package com.outbound.core;

import com.airtel.core.ServionCallEnd;
/*
import com.audium.core.vfc.VException;
import com.audium.core.vfc.VPreference;
import com.audium.core.vfc.form.VBlock;
import com.audium.core.vfc.form.VForm;
import com.audium.core.vfc.util.VAction;
import com.audium.core.vfc.util.VMain;
 */
import com.audium.server.AudiumException;
import com.audium.server.proxy.EndCallInterface;
import com.audium.server.session.APIBase;
import com.audium.server.session.CallEndAPI;
import com.host.db.DBInterface;
import com.host.dbbean.DB_Bean;
import com.host.rest.SessionAPIHost;
import com.util.*;

public class CallEnd implements EndCallInterface 
{
	public void onEndCall(CallEndAPI callEndAPI) throws AudiumException 
	{
		ServionCallEnd servionCallEnd = new ServionCallEnd();
		servionCallEnd.onCallEnd(callEndAPI);		
		Utilities util = new Utilities(callEndAPI);
		String serveyQuestionFlag=(String)callEndAPI.getSessionData("S_SURVEY_QUESTION_FLAG");
		if(null!=serveyQuestionFlag&&"YES".equalsIgnoreCase(serveyQuestionFlag)) {
			String survey_report_DB=(String) callEndAPI.getSessionData("S_SURVEY_REPORT_DB");
			if(hostCall(callEndAPI, "Call End", survey_report_DB, util)) {
				callEndAPI.addToLog("Call End : ","Outbound IVR Survey Inserted Successful  .... ");
			}else {
				callEndAPI.addToLog("Call End : ","Outbound IVR Survey Failed  .... ");
			}
		}
	}

	public boolean hostCall(APIBase data,String strElementName,String dbName,Utilities util) {
		boolean flag=false;
		int logLevel=0;
		SessionAPIHost sessionApi=null;
		try {
			sessionApi=new SessionAPIHost(data, logLevel);
			Object objBean=data.getApplicationAPI().getGlobalData(dbName);
			DBInterface dbInterface=new DBInterface(dbName, sessionApi,(DB_Bean) objBean );
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
