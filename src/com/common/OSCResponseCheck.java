package com.common;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class OSCResponseCheck extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception 
	{
		String exitState ="ER";
		Utilities util = new Utilities(data);
		try
		{
			if(util.IsValidRestAPIResponse())  {
				String strNewBal = String.valueOf(data.getSessionData("S_OSC_NEW_BAL"));
				data.addToLog(strElementName, "New Balance after OSC Recharge: "+strNewBal);
				if(util.IsNotNUllorEmpty(strNewBal)){
					exitState ="SU";
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
		return exitState;
	}
}