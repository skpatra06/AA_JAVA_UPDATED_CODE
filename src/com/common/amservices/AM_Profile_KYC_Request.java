package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Profile_KYC_Request extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			if(util.IsValidRestAPIResponse()) 
			{
				String activeAcct=""+data.getSessionData("S_AM_ACC_STATUS");
				String balance=String.valueOf(data.getSessionData("S_AM_BALANCE"));
				data.addToLog("caller Registered With Airtel Money status : ",""+ activeAcct+" AIRTEL MONEY ALANCE :"+balance);
				data.setSessionData("S_AM_BALANCE",balance.replaceAll(",",""));
				if("ACTIVE".equalsIgnoreCase(activeAcct))
				{
					data.setSessionData("S_IS_AM_CALLER","Y");
					exitState="RC";
				}
				else
				{
					data.setSessionData("S_IS_AM_CALLER","N");
					exitState="NRC";
				}
			}
			else
			{
				data.setSessionData("S_IS_AM_CALLER","N");
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
}
