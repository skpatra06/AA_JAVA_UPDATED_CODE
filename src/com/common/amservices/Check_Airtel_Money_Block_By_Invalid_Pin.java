package com.common.amservices;

import java.time.Instant;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_Airtel_Money_Block_By_Invalid_Pin extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			String S_AM_Block_By_Invalid_Pin = ""+data.getSessionData("S_AM_Block_By_Invalid_Pin");
			String S_AM_USER_BARRED = ""+data.getSessionData("S_AM_USER_BARRED");
			data.addToLog(elementName," Airtel Money S_AM_Block_By_Invalid_Pin : "+ S_AM_Block_By_Invalid_Pin);
			data.addToLog(elementName," Airtel Money S_AM_USER_BARRED : "+ S_AM_USER_BARRED);
			if(util.IsNotNUllorEmpty(S_AM_USER_BARRED) && S_AM_USER_BARRED.equalsIgnoreCase("FALSE")){
				exitState = "USERBARREDNO";
			}else if(util.IsNotNUllorEmpty(S_AM_USER_BARRED) && S_AM_USER_BARRED.equalsIgnoreCase("TRUE")){
				
				if(util.IsNotNUllorEmpty(S_AM_Block_By_Invalid_Pin) && S_AM_Block_By_Invalid_Pin.equalsIgnoreCase("True")){
					exitState = "INVALIDPINYES";
				}else{
					exitState = "INVALIDPINNO";
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


