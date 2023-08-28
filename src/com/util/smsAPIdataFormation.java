package com.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.airtel.core.SessionAPI;
import com.airtel.framework.creator.voice.VoiceElementsInterface;
import com.audium.server.AudiumException;
import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;


/**
 * This class is called by the decision element
 * for setting the Menu Options in case of the Menu
 */
public class smsAPIdataFormation extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data)	throws AudiumException
	{
		String exitState="N";
		int logLevel=0;
		SessionAPI sessionAPI = new SessionAPI(data, logLevel);
		Utilities util=new Utilities(data);
		try 
		{
			if("Y".equalsIgnoreCase(String.valueOf(data.getSessionData("S_SMS_RESEND_FLAG"))))
			{
				data.setSessionData("S_SMS_RESEND_FLAG","N");
				String smsText=(String) data.getSessionData("S_SMS_FINAL_TEXT2");
				if(null!=smsText) 
				{
					data.addToLog(elementName, "Hitting SMS API To Send Remaining Text ... ");
					data.setSessionData("S_SMS_FINAL_TEXT",smsText);
					return "Y";
				}
			}
			else
			{
				data.setSessionData("S_SMS_RESEND_FLAG","N");
			}
			
			String SID=(String) data.getElementData("SMS_GATEWAY","SMS_ID");
			data.addToLog(elementName,"The SMS Host ID:"+SID);
			if(null!=SID) 
			{
				exitState = loadSMS(data,util, SID, elementName);
				if("Y".equalsIgnoreCase(exitState))
				{
					if("TRUE".equalsIgnoreCase(String.valueOf(data.getSessionData("S_SMS_RE_HIT_CHECK"))) ) 
					{
						String smsFinalText=(String) data.getSessionData("S_SMS_FINAL_TEXT");
						if(util.IsNotNUllorEmpty(smsFinalText))
						{
							int maxLength=0;
							try 
							{
								maxLength=Integer.parseInt(String.valueOf(data.getSessionData("S_SMS_MAX_LENGTH")));
							}
							catch (Exception e)
							{
								util.errorLog(elementName, e);
								maxLength=450;
								data.setSessionData("S_SMS_MAX_LENGTH", maxLength);
							}
							if(maxLength<smsFinalText.length()&&smsFinalText.contains(","))
							{
								String[] splitTextCont=smsFinalText.split("\\,");
								String smsFirstContText="";
								String smsSecContText="";
								for(String smsText:splitTextCont)
								{
									smsText=smsText.trim();
									if(smsFirstContText.length()<maxLength&&(((smsFirstContText.length()+smsText.length())<maxLength))&&"".equalsIgnoreCase(smsSecContText))
									{
										if("".equals(smsFirstContText)) 
										{
											smsFirstContText=smsText;
										}
										else
										{
											smsFirstContText=smsFirstContText+","+smsText;
										}  
									}
									else
									{
										if("".equalsIgnoreCase(smsSecContText)) 
										{
											smsSecContText=smsText;
										}
										else
										{
											smsSecContText=smsSecContText+","+smsText;
										}
									}
								}
								data.setSessionData("S_SMS_FINAL_TEXT", smsFirstContText);
								if(util.IsNotNUllorEmpty(smsSecContText)&&!smsSecContText.trim().isEmpty()) {
									data.setSessionData("S_SMS_FINAL_TEXT2",smsSecContText);
									data.setSessionData("S_SMS_RESEND_FLAG","Y");
								}
							}
							else
							{
								data.setSessionData("S_SMS_RESEND_FLAG","N");
							}
						}
						else
						{
							data.addToLog(elementName, "Null Value In Final Text : "+smsFinalText);
							exitState="N";
						}
					}
				}
				else
				{
					exitState="N";
				}
				data.setElementData("SMS_ID",null);
			}
			else
			{
				data.addToLog(elementName," SMS Template Not available :"+SID);
			}

		}
		catch(Exception e)
		{
			data.setSessionData("S_SMS_RESEND_FLAG","N");
			sessionAPI.ErrorLog(e);
		}
		finally
		{
			util=null;
		}
		return exitState;
	}

	public String loadSMS(APIBase data,Utilities util ,String SID, String elementName)
	{
		String exitState="N";
		Map<String, Map<String, String>> mapSMSObj=null;
		List<Map<String,String>> objDynamciValue = null;
		Map<String, String> smsDataMap = null;
		int logLevel=0;
		SessionAPI sessionAPI = new SessionAPI(data, logLevel);
		try
		{
			mapSMSObj=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("SMS_TEMPLATE");
			util.addToLog(elementName+" ------ SMS MAP -------"+mapSMSObj,util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(mapSMSObj) && mapSMSObj.containsKey(SID)) 
			{
				smsDataMap = mapSMSObj.get(SID);
				if(util.IsNotNUllorEmpty(smsDataMap))
				{
					String smsFlag = smsDataMap.get(VoiceElementsInterface.SMS_ENABLE_FLAG);
					if(smsFlag!=null && smsFlag.equalsIgnoreCase(VoiceElementsInterface.YES)) {
						String strActiveLang = (String) data.getSessionData("S_ACTIVE_LANG");
						String smsText = smsDataMap.get(VoiceElementsInterface.SMS_TEXT+"_"+strActiveLang);
						String dynValue = smsDataMap.get(VoiceElementsInterface.DYNAMIC_VALUE);
						String destinationNumber=smsDataMap.get("DESTINATION_NUMBER");
						data.addToLog(elementName," SMS TEXT : "+smsText);
						data.addToLog(elementName," dynamic Value : "+dynValue);
						if(util.IsNotNUllorEmpty(destinationNumber))
						{
							data.setSessionData("S_SMS_DEST", data.getSessionData(destinationNumber));
							data.addToLog(elementName," SMS Destination Number  : "+destinationNumber);
						}else {
							data.setSessionData("S_SMS_DEST", data.getSessionData("S_CLI"));
							data.addToLog(elementName," SMS Destination Number  : "+data.getSessionData("S_CLI"));
						}
						objDynamciValue = (List<Map<String, String>>) data.getSessionData(dynValue);
						if(!util.IsNotNUllorEmpty(objDynamciValue)) {
							objDynamciValue = new ArrayList<Map<String,String>>();
							objDynamciValue.add(new HashMap<String, String>());
						}
						String smsFinalText = util.formSMSTemplate(smsText, objDynamciValue);
						data.addToLog(elementName," SMS Final Text : "+smsFinalText);
						data.setSessionData(VoiceElementsInterface.SMS_FINAL_TEXT,String.valueOf( smsFinalText));
						exitState="Y";
					}else {
						data.addToLog(elementName," SMS is disabled for "+SID);
					}
				}
			}
			else 
			{ 
				data.addToLog(elementName," Null Value Occurs In Access Host :");

			}
		}
		catch(Exception e)
		{
			sessionAPI.ErrorLog(e);
		}
		finally
		{
			mapSMSObj=null;
			objDynamciValue = null;
			smsDataMap = null;
		}
		return exitState;
	}

}