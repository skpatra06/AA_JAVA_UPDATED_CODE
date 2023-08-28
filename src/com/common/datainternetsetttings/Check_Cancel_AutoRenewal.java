package com.common.datainternetsetttings;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_Cancel_AutoRenewal extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState="Failure";
		Utilities util = new Utilities(data);
		try {
			String statusCode=String.valueOf( data.getSessionData("S_AUTO_RENEWAL_STATUS_CODE"));
			if("200".equalsIgnoreCase(statusCode)) 
			{
				data.addToLog(elementName, "Auto Renewal Cancellation is successful ... ");
				exitState="Success";
			}

		}catch (Exception e) {
			util.errorLog(elementName, e);
		}
		finally {
			util=null;
		}
		return exitState;
	}
}
