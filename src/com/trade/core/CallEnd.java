package com.trade.core;

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
import com.util.smsAPIdataFormation;

public class CallEnd implements EndCallInterface {

	public void onEndCall(CallEndAPI callEndAPI) throws AudiumException {
		//SessionAPI sessionAPI = (SessionAPI)callEndAPI.getSessionData(VoiceElementsInterface.CUSTOM_DATA_OBJ);
		//doDisconnect(callEndAPI,sessionAPI);
		ServionCallEnd servionCallEnd = new ServionCallEnd();
		Utilities util = new Utilities(callEndAPI);
		try {
			servionCallEnd.onCallEnd(callEndAPI);
			String isAgentTransferFlag=(String) callEndAPI.getSessionData("IS_AGENT_TRANSFER_FLAG");
			String callEndType=callEndAPI.getHowCallEnded();
			if("HangUp".equalsIgnoreCase(callEndType)||"DISCONNECT".equalsIgnoreCase(callEndType)||!"Y".equalsIgnoreCase(isAgentTransferFlag)) {
				callEndAPI.setSessionData("S_HOST_FAILURE_TRANSFER_FLAG","N");
			}
			if("Hangup".equalsIgnoreCase(callEndType)){
				smsAPIdataFormation smsLoad = new smsAPIdataFormation();
				String smsID = (String) callEndAPI.getSessionData("S_DISC_SMS_ID");
				String smsFalg = smsLoad.loadSMS(callEndAPI,util, smsID, "CallEnd");
				if ("Y".equalsIgnoreCase(smsFalg)) {
					if(util.hostCall(callEndAPI, "Send_SMS_Notification"))
					{
						callEndAPI.addToLog("CallEnd", "Disconnect SMS sent successfully");
					}
					else
					{
						callEndAPI.addToLog("CallEnd", "Error while sending Disconnect SMS");
					}
				}
			}
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
		catch (Exception e) 
		{
			util.errorLog("CALL END ", e);
		}
		finally
		{
			util=null;
		}
	}
}