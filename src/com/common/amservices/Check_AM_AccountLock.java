package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_AM_AccountLock extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {

		String exitState = "NO";
		Utilities util = new Utilities(data);
		try 
		{
			String S_AM_USER_BARRED = ""+data.getSessionData("S_AM_USER_BARRED");
			data.addToLog(elementName," Airtel Money S_AM_USER_BARRED : "+ S_AM_USER_BARRED);
			if(util.IsNotNUllorEmpty(S_AM_USER_BARRED) && S_AM_USER_BARRED.equalsIgnoreCase("TRUE")){
				exitState = "YES";
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
