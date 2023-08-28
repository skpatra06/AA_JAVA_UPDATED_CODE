package com.common;

import java.util.Map;

import com.airtel.framework.creator.voice.VoiceElementsInterface;
import com.audium.server.AudiumException;
import com.audium.server.session.DecisionElementData;
import com.audium.server.session.ElementAPI;
import com.audium.server.session.ReadOnlyList;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;


public class GetErrorMsg extends DecisionElementBase {
	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception {

		String exitState="API_DBFailure";
		Utilities util =new Utilities(data);
		String responseCode="";
		Map<String,Map<String,String>> errorCodeDetails= null;
		Map<String,String> errorCodeMap=null;
		try
		{
			ReadOnlyList listElements = (ReadOnlyList) data.getElementHistory();
			String strDBElementName = "";
			for(int i=listElements.size()-1;i>=0;i--)
			{
				if((listElements.get(i)).endsWith("_DB"))
				{
					strDBElementName= listElements.get(i);
					break;
				}
			}
			data.addToLog(elementName, "Last DB name: "+strDBElementName);
			setTransferData(data, strDBElementName,util);
			data.setSessionData("S_HOST_FAILURE_TRANSFER_FLAG","Y");
			errorCodeDetails=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("Errorcodes");
			util.addToLog(elementName+ "Error Code Details :"+errorCodeDetails,util.DEBUGLEVEL);
			responseCode=String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
			data.addToLog("****** RESPONSE CODE ******** :", responseCode);
			String HID=(String) data.getElementData(strDBElementName,"HID");
			data.setSessionData("S_HOST_FAILURE_ID",HID);
			data.addToLog(strDBElementName, "HID INFO  : "+responseCode+"|"+HID);
			if(util.IsNotNUllorEmpty(errorCodeDetails))
			{
				if(errorCodeDetails.containsKey((responseCode+"|"+HID)))
				{
					errorCodeMap=errorCodeDetails.get((responseCode+"|"+HID));
				}
				else if(errorCodeDetails.containsKey(responseCode))
				{
					errorCodeMap=errorCodeDetails.get(responseCode);
				}
				String strErrMsg =(errorCodeMap!=null)?String.valueOf(errorCodeMap.get("ERROR_MSG")):null;
				if(util.IsNotNUllorEmpty(strErrMsg))
				{
					strErrMsg=strErrMsg.trim();
					data.addToLog("******* ERROR CODE WAV ********* :",strErrMsg );
					if(!(strErrMsg.contains(".wav")))
					{
						strErrMsg+=".wav";
					}
					data.setSessionData("S_ERROR_AUD",strErrMsg);
					data.setSessionData("S_MR_CONFIG",(errorCodeMap!=null)?errorCodeMap.get("MENU_ROUTING"):"MR1");
					exitState="ErrorCode";
				}
				else
				{
					data.addToLog(elementName, "Error msg is : "+strErrMsg);
					exitState="API_DBFailure";
				}
			}
		} 
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			util=null;
			errorCodeDetails= null;
			errorCodeMap=null;
		}
		return exitState;
	}

	private void setTransferData(ElementAPI data, String elementName, Utilities util) throws AudiumException {
		Map<String, Map<String,String>> mapTransferInfo = null;
		Map<String,String> objTransferInfo = null;
		try
		{
			String appName =data.getApplicationName();
			mapTransferInfo =(Map<String, Map<String,String>>) data.getApplicationAPI().getGlobalData(appName+VoiceElementsInterface.TRANSFER_DETAIL);
			util.addToLog("TRANSFER NODE : "+" APP NAME : "+appName+" - MapInfo "+mapTransferInfo,util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(mapTransferInfo)){
				data.addToLog(elementName,"");
				objTransferInfo = mapTransferInfo.get(elementName);
				String activeLang = (String) data.getSessionData("S_ACTIVE_LANG");
				if(util.IsNotNUllorEmpty(objTransferInfo))
				{
					data.setSessionData("S_PRI_SKILL_NAME", objTransferInfo.get(activeLang+"_PRI_SKILL"));
					data.setSessionData("S_SEC_SKILL_NAME", objTransferInfo.get(activeLang+"_SEC_SKILL"));
					data.setSessionData(VoiceElementsInterface.S_TRANSFER_CODE, objTransferInfo.get("Transcode"));
					data.addToLog(elementName,"Primary Skill :"+objTransferInfo.get(activeLang+"_PRI_SKILL")+" and Secondary Skill"+objTransferInfo.get(activeLang+"_SEC_SKILL")
					+ " S_TRANSFER_CODE :"+objTransferInfo.get("Transcode"));
				}
			} 
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally 
		{
			objTransferInfo = null;
			mapTransferInfo = null;
		}
	}
}
