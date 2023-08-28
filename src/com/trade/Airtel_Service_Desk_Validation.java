package com.trade;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Airtel_Service_Desk_Validation extends DecisionElementBase
{

	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="N";
		Utilities util = new Utilities(data);
		try 
		{
			String custSegment = (String) data.getSessionData("S_CUSTOMER_SEGMENT");
			data.addToLog("Customer Segmentation Details :", "" + custSegment);
			if(util.IsNotNUllorEmpty(custSegment)) 
			{
				exitState="Y";
			}
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		return exitState;
	}
}

