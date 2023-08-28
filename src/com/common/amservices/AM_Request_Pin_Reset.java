package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Request_Pin_Reset extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			String S_PIN_AM_STATUS=""+data.getSessionData("S_PIN_AM_STATUS");
			String S_AM_API_MESSAGE=""+data.getSessionData("S_AM_API_MESSAGE");
			String AM_ACC_UNLOCK=""+data.getSessionData("AM_ACC_UNLOCK");
			data.addToLog(elementName," Airtel Money status S_AM_STATUS : "+ S_PIN_AM_STATUS +" Message : "+S_AM_API_MESSAGE);
			
			if(!"null".equalsIgnoreCase(S_PIN_AM_STATUS) ){
				if("200".equalsIgnoreCase(S_PIN_AM_STATUS))
				{
					if(!"null".equalsIgnoreCase(AM_ACC_UNLOCK) ){
						exitState="UNSU";
					}else{
						exitState="SU";
					}
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