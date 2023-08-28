package com.PSB;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Caller_DialCount_Check extends DecisionElementBase {
	
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		String callerCount=null;
		
		
		
		
		
		try  
		{
			if(util.IsValidDBSUResponse()) 
			{
				callerCount=(String) data.getSessionData("S_CALLER_COUNT");
				if(util.IsNotNUllorEmpty(callerCount) &&Integer.parseInt(callerCount)==0)
				{
					exitState="SU_FIRSTTIME_CALLER";
					data.addToLog(elementName, "The first time caller");
				}
				else
				{
					exitState="SU_NO";
					data.addToLog(elementName, "Not a first time caller. Count="+callerCount);
					
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
