package com.common;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class GsmBarring extends DecisionElementBase 
{		
	@Override	 
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		Utilities util = new Utilities(data);
		String exitState = "ER";	
		String transid=null;
		try 
		{
			transid=(String) data.getSessionData("S_GSM_BARRED_TRANSACTION_ID");
			data.addToLog(elementName, "userbarred transaction id"+transid);
			if(util.IsNotNUllorEmpty(transid))
			{
				exitState="Success";
			}
			else
			{
				exitState="Failure";
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









