package com.airtel_africa;

import java.util.List;
import java.util.Map;

import com.airtel.framework.creator.voice.VoiceElementsInterface;
import com.audium.server.session.APIBase;
import com.audium.server.session.ActionElementData;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.audium.server.voiceElement.DecisionElementBase;
import com.host.db.DBInterface;
import com.host.dbbean.DB_Bean;
import com.host.rest.SessionAPIHost;
import com.util.Utilities;

public class SegmentationDBCheck extends ActionElementBase
{
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception 
	{
		Utilities util = new Utilities(data);
		String lineType=null;
		String customerSegmentation=null;
		String preferLang=null;
		Map<String, Object> languageSettings=null;
		List<String> avlLanguages=null;
		try 
		{
			String ivrSegDB="SP_GET_IVR_SEGMENTATION";
			if(hostCall(data, "Call End", ivrSegDB,util))
			{
				List<Map<String,String>> segList=(List<Map<String, String>>) data.getSessionData("S_IVR_SEG");
				data.addToLog(strElementName," IVR SEG DETAILS  : "+segList);
				if(util.IsNotNUllorEmpty(segList))
				{
					Map<String,String> segMap=segList.get(0);
					lineType=segMap.get("p_connection_type_out");
					customerSegmentation=segMap.get("p_customer_segment_out");
					preferLang=segMap.get("p_preffered_language_out");
					data.addToLog(strElementName,"LINE TYPE    : "+lineType +" | CUST SEGMENT : "+customerSegmentation+" | PREF LANG : "+preferLang);
					if(!util.IsNotNUllorEmpty(lineType))
					{
						// Line Type Check
						data.addToLog(strElementName, "Null Value Occurs IN Line Type , So hitting the LineTypeAPI");
						if(util.hostCall(data, "Subscriber_Profile_LineType"))
						{
							lineType=(String) data.getSessionData("S_LINE_TYPE"); 
							data.addToLog("LINE TYPE API RESP : ",""+lineType);
						}
					}
					lineType=lineTypeCheck(lineType);
					data.setSessionData("S_CONN_TYPE",lineType);
				}
			}
			if(!util.IsNotNUllorEmpty(customerSegmentation)) 
			{
				customerSegmentation=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
				data.addToLog(" No Customer Segment Found From The Segmentation DB, So Default Value Setting To : ", customerSegmentation);
			}
			data.setSessionData("S_CUSTOMER_SEGMENT",customerSegmentation.toUpperCase());
		}catch (Exception e) {
			util.errorLog(strElementName, e); 
		}

		try {
			languageSettings = (Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");
			if(util.IsNotNUllorEmpty(languageSettings) && util.IsNotNUllorEmpty(preferLang))
			{
				data.addToLog(strElementName," LANGUAGE AVAILABLE "+languageSettings);
				avlLanguages = (List<String>) languageSettings.get("LANGUAGES");
				if(util.IsNotNUllorEmpty(avlLanguages))
				{
					for(int i=0;i<avlLanguages.size();i++)
					{
						if(null!=avlLanguages.get(i)&&avlLanguages.get(i).equalsIgnoreCase(preferLang)) 
						{
							preferLang=preferLang.toUpperCase();
							data.setSessionData("S_PREF_LANG",preferLang);
							data.setSessionData("S_ACTIVE_LANG", preferLang);
							break;
						}
					}
				}
			}
			// Assigning Audio Path
			String vxmlIP = "http://"+data.getSessionData(VoiceElementsInterface.S_SERVERIP);
			String strAudioPath = (String)data.getApplicationAPI().getApplicationData(VoiceElementsInterface.AUDIOPATH);
			strAudioPath = vxmlIP+strAudioPath;
			String strFullAudioPath="";
			String activeLang=(String) data.getSessionData("S_ACTIVE_LANG");
			if(util.IsNotNUllorEmpty(activeLang))
			{
				strFullAudioPath = strAudioPath + VoiceElementsInterface.SLASH	+ activeLang + VoiceElementsInterface.SLASH;
				data.addToLog(data.getCurrentElement(),"Setting AudioPath with active Language : " + strFullAudioPath);
			}
			else
			{
				strFullAudioPath = strAudioPath + VoiceElementsInterface.SLASH;
			}
			
			data.setDefaultAudioPath(strFullAudioPath);
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
			DBInterface dbInterface=new DBInterface(dbName, sessionApi,(DB_Bean) objBean );
			dbInterface.executeQuery();
			if(util.IsValidDBSUResponse()) 
			{
				flag=true;
			}
		}catch(Exception e) {
			util.errorLog("REST CLIENT :", e);
		}
		return flag;
	}
	public String lineTypeCheck(String connType)
	{
		String response=connType;
		if(null!=response){
			if(response.toUpperCase().startsWith("PRE")&&7>=response.length()){
				response="PREPAID";
			}else if(response.toUpperCase().startsWith("POS")&&8>=response.length()){
				response="POSTPAID";
			}else if(response.toUpperCase().startsWith("HYB")&&7>=response.length()){
				response="HYBRID";
			}
		}
		return response;
	}
}