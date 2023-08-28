package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Request_Account_Unlock extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			String S_AM_STATUS=""+data.getSessionData("S_AM_STATUS");
			data.addToLog(elementName," Airtel Money status S_AM_STATUS : "+ S_AM_STATUS);
			
			if(!"null".equalsIgnoreCase(S_AM_STATUS) ){
				if("200".equalsIgnoreCase(S_AM_STATUS))
				{
					data.setSessionData("AM_ACC_UNLOCK", "TRUE");
					exitState="YES";
				}else{
					exitState="NO";
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