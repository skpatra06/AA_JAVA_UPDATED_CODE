package com.enterprise.core;

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
import com.audium.server.session.CallEndAPI;
import com.util.Utilities;

public class CallEnd implements EndCallInterface
{
	public void onEndCall(CallEndAPI callEndAPI) throws AudiumException 
	{
		ServionCallEnd servionCallEnd = new ServionCallEnd();
		Utilities util = new Utilities(callEndAPI);
		try
		{
			servionCallEnd.onCallEnd(callEndAPI);
			String strWhitelist = (String) callEndAPI.getSessionData("S_WHITE_LIST_USER_CHECK");
			String strTktStatus = (String) callEndAPI.getSessionData("S_TKT_STATUS");
			String strAgentType = (String) callEndAPI.getSessionData("S_AGENT_TYPE");
			strWhitelist=util.IsNotNUllorEmpty(strWhitelist)?strWhitelist:"NA";
			strTktStatus=util.IsNotNUllorEmpty(strTktStatus)?strTktStatus:"NA";
			strAgentType=util.IsNotNUllorEmpty(strAgentType)?strAgentType:"NA";
			callEndAPI.addToLog("CallEnd", "User Whitelist: "+strWhitelist+"\t Ticket Status:"+strTktStatus+"\tAgent type:"+strAgentType);
			callEndAPI.setSessionData("S_ENTERPRISE_DATA", strWhitelist+"|"+strTktStatus+"|"+strAgentType);	
			
			String isAppTransfer = String.valueOf( callEndAPI.getSessionData("S_APP_TRANSFER"));
			callEndAPI.addToLog("Call End : ", "App Transfer Flag : "+isAppTransfer);
			if(util.IsNotNUllorEmpty(isAppTransfer)&&!"YES".equalsIgnoreCase(isAppTransfer)) 
			{
                callEndAPI.setSessionData("S_CALL_ENDDATE", util.getCurrDateInStr("yyyy-MM-dd HH:mm:ss.SSS"));
				if(util.hostCallDB(callEndAPI, "Call End ","SP_INSERT_REPEAT_CALLER"))
				{
					callEndAPI.addToLog("Call End", " Repeat Caller Data Inserted Successful ... ");
				}
				else 
				{
					callEndAPI.addToLog("Call End", " Repeat Caller Data Failed to Insert .... ");
				}
			}
		
		}
		catch(Exception e)
		{
			util.errorLog("Call End : ", e);
		}
		finally
		{
			util=null;	
		}	
	}
}
