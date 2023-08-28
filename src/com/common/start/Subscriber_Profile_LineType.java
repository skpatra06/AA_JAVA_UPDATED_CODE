package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

import com.util.Utilities;

public class Subscriber_Profile_LineType extends DecisionElementBase {

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try
		{
			if(util.IsValidRestAPIResponse())
			{
				String connType=(String) data.getSessionData("S_LINE_TYPE");
				String customerSegmentation=(String) data.getSessionData("S_CUSTOMER_SEGMENT");
				connType=lineTypeCheck(connType);
				data.addToLog("LINE TYPE CHECK : ", connType);
				data.setSessionData("S_CONN_TYPE",connType);
				if(!util.IsNotNUllorEmpty(customerSegmentation)) {
					customerSegmentation=(String) data.getApplicationAPI().getApplicationData("CUSTOMER_SEGMENTATION");
					data.addToLog(" No Customer Segment Found From The Segmentation DB, So Default Value Setting To : ", customerSegmentation);
					data.setSessionData("S_CUSTOMER_SEGMENT",customerSegmentation);
				}
				exitState = "SU";
			}
			else
			{
				String strNonAirtelFlag = (String) data.getSessionData("S_NON_AIRTEL_TRANSFER_FLAG");
				if(util.IsNotNUllorEmpty(strNonAirtelFlag) &&"True".equalsIgnoreCase(strNonAirtelFlag))
				{
                    util.setTransferData(data,"NON_AIRTEL_TRANSFER");
					exitState = "Non_Airtel";
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
