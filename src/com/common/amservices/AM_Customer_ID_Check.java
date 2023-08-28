package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Customer_ID_Check extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		String exitState = "INVALID_AM_ID";
		Utilities util = new Utilities(data);
		try
		{
			if(util.IsValidRestAPIResponse()) 
			{
				String customerAM_ID =String.valueOf(data.getSessionData("S_CUSTOMER_ID"));
				data.addToLog(strElementName, "S CUSTOMER ID :"+customerAM_ID);
				String userEnterAM_ID =String.valueOf(data.getSessionData("S_AM_USER_ENTER_ID"));
				data.addToLog(strElementName, "user  enter  ID for AM unlock:"+userEnterAM_ID);
				if(util.IsNotNUllorEmpty(customerAM_ID) && util.IsNotNUllorEmpty(userEnterAM_ID))
				{
					if(customerAM_ID.equals(userEnterAM_ID))
					{
						exitState="VALID_AM_ID";
					}
				}
			}
		}
		catch(Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util=null;
		}
		return exitState ;
	}
}