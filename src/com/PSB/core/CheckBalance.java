package com.PSB.core;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CheckBalance extends DecisionElementBase 
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		String getbalance=null;
		try 
		{
			if(util.IsValidRestAPIResponse())
			{
				getbalance=(String) data.getSessionData("S_AVAILABLE_BALANCE");
				data.addToLog(elementName, "User  balance "+getbalance);		
				if(util.IsNotNUllorEmpty(getbalance) &&Double.parseDouble(getbalance)!=0) 
				{
					data.addToLog("S_AVAILABLE_BALANCE is : ", getbalance);
					exitState="SU";
				}
				else
				{
					exitState="NO_BAL";	
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
}