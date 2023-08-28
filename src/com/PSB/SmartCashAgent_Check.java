package com.PSB;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class SmartCashAgent_Check extends DecisionElementBase {


	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		String lineType=null;
		String customerSegmentation=null;
		String preferLang=null;
		List<Map<String,String>> segList= null;
		Map<String,String> segMap= null;
		Map<String,String> segmentVarField=null;
		try  
		{
			if(util.IsValidDBSUResponse()) 
			{
				segList=(List<Map<String, String>>) data.getSessionData("S_IVR_SEG");
				segmentVarField= (Map<String, String>) data.getApplicationAPI().getApplicationData("SEG_DB_FIELD");
				util.addToLog(elementName+" IVR SEG DETAILS  : "+segList+", Seg DB Field Map : "+segmentVarField,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(segList)&&util.IsNotNUllorEmpty(segmentVarField)) 
				{
					segMap=segList.get(0);
					customerSegmentation=segMap.get(segmentVarField.get("CUST_SEG"));
					lineType=segMap.get(segmentVarField.get("CONN_TYPE"));
					preferLang=segMap.get(segmentVarField.get("PREF_LANG"));
					data.addToLog(elementName,"LINE TYPE    : "+lineType +" | CUST SEGMENT : "+customerSegmentation+" | PREF LANG : "+preferLang);
					data.setSessionData("S_PREF_LANG", preferLang);
					lineType=lineTypeCheck(lineType);
					if(util.IsNotNUllorEmpty(lineType))
					{
						data.setSessionData("S_CONN_TYPE", lineType);
					}

				}


				if(!util.IsNotNUllorEmpty(customerSegmentation)) 
				{
					customerSegmentation=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
					data.addToLog(" No Customer Segment Found From The Segmentation DB, So Default Value Setting To : ", customerSegmentation);
				}
				data.setSessionData("S_CUSTOMER_SEGMENT",customerSegmentation.toUpperCase());
				data.setSessionData("S_PREVIOUS_PAGE", "Start");

				if(customerSegmentation.equalsIgnoreCase("TRADE"))
				{
					util.addToLog("SMART CASH AGENT");
					exitState="SUCCESS_CASHAGENT";
				}
				else
				{
					exitState="SUCCESS_NO";
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
