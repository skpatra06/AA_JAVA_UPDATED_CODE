package com.common.faf;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Product_GetFAF_EligibleCheck extends DecisionElementBase 
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		String exitState="ER";
		Utilities util=new Utilities(data);
		try 
		{
			if(util.IsValidRestAPIResponse()) 
			{
				exitState="SuccessFAF";
			}
			else
			{
				String responseCode=String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
				if("2500".equalsIgnoreCase(responseCode)) 
				{
					data.addToLog(strElementName,"Subscriber has no Friends and Family : "+responseCode);
					data.setSessionData("S_FAF_MAXALLOWED_NUMREACHED_FLAG","false");
					exitState="SuccessNoFAF";
				}
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util=null;
		}
		return exitState;
	}

}
