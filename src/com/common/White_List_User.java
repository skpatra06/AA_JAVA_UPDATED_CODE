package com.common;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class White_List_User extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		String lineType=null;
		String customerSegmentation=null;
		List<Map<String,String>> segList= null;
		Map<String,String> segMap= null;
		try 
		{
			if(util.IsValidDBSUResponse()) 
			{
				segList=(List<Map<String, String>>) data.getSessionData("S_IVR_SEG");
				util.addToLog(strElementName+ " IVR SEG DETAILS  : "+segList,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(segList))
				{
					segMap=segList.get(0);
					if(util.IsNotNUllorEmpty(segMap))
					{
						lineType=segMap.get("p_connection_type_out");
						if("UG".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE")))) 
							customerSegmentation=segMap.get("p_customer_segment1_out");
						else if("CD".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "NE".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "GA".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "NG".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "ZM".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "MG".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "MW".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "TZ".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "CG".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))) || "RW".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE")))) 
							customerSegmentation=segMap.get("p_customer_segment2_out");
						else if( "TD".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))))
							customerSegmentation=segMap.get("p_cutomer_segment2_out");
						//else if("CD".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))))
						//	customerSegmentation=segMap.get("p_customer_segment3_out");
						else
							customerSegmentation=segMap.get("p_customer_segment_out");						
						data.addToLog(strElementName,"LINE TYPE    : "+lineType +" | CUST SEGMENT : "+customerSegmentation);						
						if(!util.IsNotNUllorEmpty(customerSegmentation)) 
						{
							customerSegmentation=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
							data.addToLog(" No Customer Segment Found From The Segmentation DB, So Default Value Setting To : ", customerSegmentation);
						}
						data.setSessionData("S_CUSTOMER_SEGMENT",customerSegmentation.toUpperCase());
						data.setSessionData("S_CONN_TYPE", lineType);
						if(util.IsNotNUllorEmpty(customerSegmentation)) 
						{
							customerSegmentation=customerSegmentation.trim();
							String whiteListSegment=(String) data.getApplicationAPI().getApplicationData("WHITELIST_SEG");
							data.addToLog(strElementName,"Whitelisted Segmentations :"+whiteListSegment);
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
					}
				}
			}
			if(!util.IsNotNUllorEmpty(customerSegmentation)) 
			{
				customerSegmentation=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
				data.addToLog(" No Customer Segment Found From The Segmentation DB, So Default Value Setting To : ", customerSegmentation);
			}
			data.setSessionData("S_CUSTOMER_SEGMENT",customerSegmentation.toUpperCase());
			data.setSessionData("S_WHITE_LIST_USER_CHECK", "NOT_WHITELISTED");
			exitState = "NON_White_List";
		}
		catch(Exception e) 
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
			segList = null;
			segMap = null;
		}
		return exitState;
	}
}