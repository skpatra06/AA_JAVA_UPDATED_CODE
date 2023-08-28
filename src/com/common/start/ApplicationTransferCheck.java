package com.common.start;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ApplicationTransferCheck extends DecisionElementBase
{
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState = "NULL";
		String app_transfer_annc = "";
		Utilities util = new Utilities(data);
		Map<String,Map<String,String>> mapAtcData = null;
		Map<String,String> mapTransferDetails = null;
		Map<String,Object> short_code_annc_map=null;
		List<String> listWav=null;
		try 
		{
			mapAtcData = (Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("ATC_DATA");
			String strConnectionType = (String) data.getSessionData("S_CONN_TYPE");
			String strCustSegment = (String) data.getSessionData("S_CUSTOMER_SEGMENT");
			if(!util.IsNotNUllorEmpty(strConnectionType))
			{
				strConnectionType = (String) data.getSessionData("S_ATC_CONN");
				data.addToLog(elementName, "Connection Type not fetched, Hence setting "+strConnectionType+" from session");
			}
			else
			{
				strConnectionType= strConnectionType.toUpperCase();
				data.setSessionData("S_CONN_TYPE", strConnectionType);
			}

			if(!util.IsNotNUllorEmpty(strCustSegment))
			{
				strCustSegment=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
			}

			String strCurrentApp=data.getApplicationName();
			String strDNISType = (String) data.getSessionData("S_DNIS_TYPE");
			if(util.IsNotNUllorEmpty(strDNISType)) 
			{ 
				strCurrentApp = strDNISType; 
			}

			String strTransferApp = "";
			util.addToLog(elementName+ " Current App:"+strCurrentApp+"\nConnection Type:"+strConnectionType+"\nCustomer Segmentation:"+strCustSegment, util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(strCustSegment)&&util.IsNotNUllorEmpty(strConnectionType))
			{
				if(util.IsNotNUllorEmpty(mapAtcData))
				{
					mapTransferDetails = mapAtcData.get(strCurrentApp.replaceAll("AA_", ""));
					util.addToLog(elementName+" App transfer data Map:"+mapTransferDetails,util.DEBUGLEVEL);
					if(util.IsNotNUllorEmpty(mapTransferDetails))
					{
						strTransferApp = mapTransferDetails.get(strConnectionType+"|"+strCustSegment);
						data.addToLog(elementName, "App Transfer data:"+strTransferApp);
						if(!util.IsNotNUllorEmpty(strTransferApp))
						{
							strTransferApp = "AA_"+strConnectionType+"_IVR";
						}
						else if(strCurrentApp.equalsIgnoreCase("AIRTEL_MONEY")&&strTransferApp.contains("PREPAID_IVR"))
						{
							data.setSessionData("S_DNIS_TYPE", "PREPAID_IVR");
							return "YES";
						}
						exitState = !strTransferApp.contains(strCurrentApp)?"Transfer":"YES";
						data.setSessionData("S_TRANSFER_APP", strTransferApp);
					}
				}
				else
				{
					data.addToLog(elementName, "App transfer data not available "+mapAtcData);
				}

			}
			else
			{
				data.addToLog(elementName, "Customer segment values not fetched "+strCustSegment);
			}
			if("Transfer".equalsIgnoreCase(exitState)) {
				data.setSessionData("S_APP_TRANSFER","YES");
			}
			/** For Nigeria Based on Customer Segmentation,we have to play the App Transfer Annc */
			if("Transfer".equalsIgnoreCase(exitState))
			{
				short_code_annc_map=(Map<String, Object>) data.getApplicationAPI().getApplicationData("APP_TRANSFER_SHORT_CODE_ANNC");	
				util.addToLog(elementName+" SHORT CODE ANNOUCEMENT MAP : "+short_code_annc_map,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(short_code_annc_map))
				{
					String dnis=(String) data.getSessionData("S_DNIS");
					if(short_code_annc_map.containsKey(strConnectionType+"_"+strCustSegment+"|"+dnis)&&null!=short_code_annc_map.get(strConnectionType+"_"+strCustSegment+"|"+dnis))
					{
						Object obj=short_code_annc_map.get(strConnectionType+"_"+strCustSegment+"|"+dnis);
						if(obj instanceof List) 
						{
							listWav=(List<String>) obj;
							for(String srtWav:listWav) 
							{
								if(!srtWav.endsWith(".wav"))
								{
									if("".equalsIgnoreCase(app_transfer_annc)) 
										app_transfer_annc=srtWav+".wav";	
									else 
										app_transfer_annc=app_transfer_annc+","+srtWav+".wav";
								} 
								else
								{
									if("".equalsIgnoreCase(app_transfer_annc)) 
										app_transfer_annc=srtWav;	
									else 
										app_transfer_annc=app_transfer_annc+","+srtWav;
								}
							}
						} 
						else
						{
							app_transfer_annc=(String) obj;
						}
					} 
					else 
					{
						app_transfer_annc=(String) short_code_annc_map.get("DEFAULT");
					}
				}
				data.addToLog(elementName, "ANNC WAV : "+app_transfer_annc);
				data.setSessionData("S_APP_TRANSFER_ANNC",app_transfer_annc);
			}

		} 
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			short_code_annc_map=null;
			mapTransferDetails = null;
			mapAtcData = null;
			listWav=null;
			util=null;
		}
		return exitState;
	}
}