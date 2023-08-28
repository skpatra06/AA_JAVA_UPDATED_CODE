package com.common.start;

import java.util.List;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Segmentation_Check extends DecisionElementBase
{
	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		String lineType=null;
		String customerSegmentation=null;
		String preferLang=null;
		List<Map<String,String>> segList= null;
		Map<String,String> segMap= null;		
		Map<String,String> segmentVarField=null;
		Map<String, Object> languageSettings = null;
		List<String> avlLanguages =null;
		try {
			if(util.IsValidDBSUResponse()) 
			{
				segList=(List<Map<String, String>>) data.getSessionData("S_IVR_SEG");
				segmentVarField= (Map<String, String>) data.getApplicationAPI().getApplicationData("SEG_DB_FIELD");
				util.addToLog(elementName+" IVR SEG DETAILS  : "+segList+", Seg DB Field Map : "+segmentVarField,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(segList)&&util.IsNotNUllorEmpty(segmentVarField)) 
				{
					segMap=segList.get(0);
					if(util.IsNotNUllorEmpty(segMap))
					{
						lineType=segMap.get(segmentVarField.get("CONN_TYPE"));
						/** Enterprise Caller Check  */
						customerSegmentation=segmentVarField.containsKey("CUST_SEG_ENT")? segMap.get(segmentVarField.get("CUST_SEG_ENT")):null;
						data.addToLog(" Enterprise Segment Value : ",customerSegmentation);
						if(util.IsNotNUllorEmpty(customerSegmentation)) 
						{
							exitState="NON_White_List";
							customerSegmentation=customerSegmentation.trim();
							String whiteListSegment=(String) data.getApplicationAPI().getApplicationData("WHITELIST_SEG");
							data.addToLog(elementName,"Whitelisted Segmentations :"+whiteListSegment);
							if(util.IsNotNUllorEmpty(whiteListSegment))
							{

								if(whiteListSegment.equalsIgnoreCase("ALL"))
								{
									data.setSessionData("S_WHITE_LIST_USER_CHECK", "WHITELISTED");
									return "WHITE_LIST";
								}
								else if (whiteListSegment.contains(customerSegmentation.toUpperCase()))
								{
									data.setSessionData("S_WHITE_LIST_USER_CHECK", "WHITELISTED");
									return "WHITE_LIST";
								}
							}
						}
						else
						{
							customerSegmentation=segmentVarField.containsKey("CUST_SEG")? segMap.get(segmentVarField.get("CUST_SEG")):null;
						}
						data.setSessionData("S_CONN_TYPE", lineType);
					}

					/** Default Customer Segment Check */
					if(!util.IsNotNUllorEmpty(customerSegmentation)) {
						customerSegmentation=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
						data.addToLog(" No Customer Segment Found From The Segmentation DB, So Default Value Setting To : ", customerSegmentation);
					}					
					data.setSessionData("S_CUSTOMER_SEGMENT",customerSegmentation.toUpperCase());
					if("NON_White_List".equalsIgnoreCase(exitState)) {
						data.setSessionData("S_WHITE_LIST_USER_CHECK", exitState);
						return exitState;	
					}
					preferLang=segmentVarField.containsKey("PREF_LANG")?segMap.get(segmentVarField.get("PREF_LANG")):"";
					if(util.IsNotNUllorEmpty(preferLang)) {
						data.setSessionData("S_PREF_LANG", preferLang);
					}
					
					/** Outbound Caller Check  */
					try
					{
						/**  Prefer Language Check For Outbound Application */
						String appType = (String) data.getSessionData("S_APP_TYPE");
						if(util.IsNotNUllorEmpty(appType)&&"OUTBOUND".equalsIgnoreCase(appType)&& util.IsNotNUllorEmpty(preferLang)) 
						{
							util.addToLog("App type is : " +appType +" : Checking Prefer Language  ", util.INFOLEVEL);
							languageSettings=(Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");
							util.addToLog("Language Details :"+languageSettings, util.DEBUGLEVEL);
							if(util.IsNotNUllorEmpty(languageSettings)) 
							{                            
								avlLanguages= (List<String>) languageSettings.get("LANGUAGES");
								util.addToLog("Avaiakable Language Details :"+avlLanguages, util.DEBUGLEVEL);
								if(util.IsNotNUllorEmpty(avlLanguages)) 
								{
									for(int i=0;i<avlLanguages.size();i++)  {
										if(null!=avlLanguages.get(i)&&avlLanguages.get(i).equalsIgnoreCase(preferLang)) {
											data.setSessionData("S_ACTIVE_LANG", preferLang.toUpperCase());
											util.addToLog("set Active Language:  "+avlLanguages, util.DEBUGLEVEL);
											util.setDefaultAudioPath();
											break;
										}
									}
								}    
							}
						}
					}
					catch (Exception e)
					{
						util.errorLog(elementName, e);
					}
					data.addToLog(elementName,"LINE TYPE    : "+lineType +" | CUST SEGMENT : "+customerSegmentation+" | PREF LANG : "+preferLang);
					data.setSessionData("S_PREF_LANG", preferLang);
					lineType=lineTypeCheck(lineType);
					if(util.IsNotNUllorEmpty(lineType))
					{
						data.setSessionData("S_CONN_TYPE", lineType);
						exitState = "SU";
					}
					else
					{
						exitState = "NR";
					}
				}
				else
				{
					exitState = "NR";
				}
			}
			else if(util.IsValidDBNRResponse())
			{
				exitState = "NR";
			}		
			if(!util.IsNotNUllorEmpty(customerSegmentation)) 
            {
                customerSegmentation=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
                data.addToLog(" No Customer Segment Found From The Segmentation DB, So Default Value Setting To : ", customerSegmentation);
            }
			data.setSessionData("S_CUSTOMER_SEGMENT",customerSegmentation.toUpperCase().trim());
			data.setSessionData("S_PREVIOUS_PAGE", "Start");			
			if("NR".equalsIgnoreCase(exitState))
			{
				String strNonAirtelFlag = (String) data.getSessionData("S_NON_AIRTEL_TRANSFER_FLAG");
				if(util.IsNotNUllorEmpty(strNonAirtelFlag) &&"True".equalsIgnoreCase(strNonAirtelFlag))
				{
					util.setTransferData(data,"NON_AIRTEL_TRANSFER");
					return "Non_Airtel";
				}
			}
		}
		catch(Exception e) 
		{
			util.errorLog(elementName, e);
		}
		finally
		{
		    languageSettings = null;
			avlLanguages=null;
			util = null;
			segList = null;
			segMap = null;
		}
		return exitState;
	}

	public String lineTypeCheck(String connType)
	{
		String response=connType;
		if(null!=response)
		{
			if(response.toUpperCase().startsWith("PRE")&&7>response.length()){
				response="PREPAID";
			}else if(response.toUpperCase().startsWith("POS")&&8>response.length()){
				response="POSTPAID";
			}else if(response.toUpperCase().startsWith("HYB")&&5>response.length()){
				response="HYBRID";
			}
		}
		return response;
	}
}