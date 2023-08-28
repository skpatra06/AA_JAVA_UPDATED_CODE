package com.common;


import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;


public class Am_Barring  extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		Utilities util=new Utilities(data);
		String exitState = "ER";
		String amtransid=null;
		try 
		{		
			amtransid=(String) data.getSessionData("S_AM_BARRED_TRANS_ID");
			data.addToLog(elementName, "am userbarred transaction id"+amtransid);
			if(util.IsNotNUllorEmpty(amtransid))
			{
				exitState="Success";
			}
			else
			{
				exitState="Failure";
			}
		}
		finally
		{
			util=null;
		}
		return exitState;
	}
}