package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_Airtel_Money_User_Info_Question_Balance extends DecisionElementBase{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			String amWallentBalanceLimit=String.valueOf(data.getSessionData("S_AM_UNLOCK_MIN_BALANCE"));
			String statuSCode = String.valueOf(data.getSessionData("S_STATUS_CODE"));
			String recovery_question_set = ""+data.getSessionData("S_AM_RECOVERY_QUESTION_SET");
			String BALANCE = ""+data.getSessionData("S_AM_PINRESET_BALANCE");
			String S_AM_Block_By_Invalid_Pin = ""+data.getSessionData("S_AM_Block_By_Invalid_Pin");
			data.addToLog(elementName, "S_AM_UNLOCK_MIN_BALANCE : "+amWallentBalanceLimit);
			data.addToLog(elementName, "S_AM_Block_By_Invalid_Pin : "+S_AM_Block_By_Invalid_Pin);
			data.addToLog(elementName,"Airtel Money status : "+ statuSCode+" S_AM_RECOVERY_QUESTION_SET :"+recovery_question_set +" :S_AM_PINRESET_BALANCE :"+BALANCE);
	
			if(util.IsValidRestAPIResponse()) 
			{
				if(Double.parseDouble(BALANCE) > Double.parseDouble(amWallentBalanceLimit)){
					if(recovery_question_set.equalsIgnoreCase("false")){
						exitState="QNO";
					}else{
						exitState="QYES";
					}
				}else{
					exitState="SU";
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
 


