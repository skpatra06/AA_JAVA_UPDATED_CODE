package com.common.amservices;

import java.time.Instant;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Last_Sim_Swap_Date_Check extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			long epoch=Instant.now().toEpochMilli();
			String S_AM_SWAP_MODIFIED_DATE=""+data.getSessionData("S_AM_SWAP_MODIFIED_DATE");
			String S_AM_SWAP_Message=""+data.getSessionData("S_AM_SWAP_Message");
			data.addToLog(elementName," Airtel Money S_AM_SWAP_MODIFIED_DATEe : "+ S_AM_SWAP_MODIFIED_DATE+" Current Eepoch Time : "+epoch+" : Meassage :"+S_AM_SWAP_Message);
			if(util.IsNotNUllorEmpty(S_AM_SWAP_Message)){
				return exitState;
			}else if(util.IsNotNUllorEmpty(S_AM_SWAP_MODIFIED_DATE)){
			    long differenceTime=((epoch-Long.parseLong(S_AM_SWAP_MODIFIED_DATE))/1000)/3600;
			    data.addToLog(elementName," Difference time In HOURS : "+ differenceTime);
			    if(differenceTime>48){
			    	exitState = "YES";
			    }else{
			    	exitState = "NO";
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
