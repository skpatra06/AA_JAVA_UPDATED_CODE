package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class WelcomeBargeinCheck extends DecisionElementBase 
{
	@Override
	public String doDecision(String strtElementName, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		String exitState = "Bargein_enabled";
		Utilities util = new Utilities(data);
		String bargeinDisable=null;
		try 
		{
			bargeinDisable=(String) data.getApplicationAPI().getApplicationData("WEL_PROMPT_BARGEIN_DISABLE");
			if(util.IsNotNUllorEmpty(bargeinDisable)) 
			{
				util.addToLog("Bargein Disable segment :"+ bargeinDisable,util.DEBUGLEVEL);
				String segmentValue=(String) data.getSessionData("S_CUSTOMER_SEGMENT");
				if(util.IsNotNUllorEmpty(segmentValue))
				{
					data.addToLog(strtElementName,"Active Segment : "+ segmentValue);
					if(bargeinDisable.contains(segmentValue)) 
					{
						data.addToLog(strtElementName, "Bargein Is Diabled For this Caller");
						data.setSessionData("S_WELCOME_BARGEIN","TRUE");
						exitState="Bargein_disabled";
					}
					else
					{
						data.addToLog(strtElementName, "Bargein Is Enabled For this Caller");
					}
				}
				else
				{
					data.addToLog(strtElementName, "Null Value Occured In Customer Segmentation Value");
				}
			}
			else
			{
				data.addToLog(strtElementName, "Null Value Occured In Config Data"); 
				exitState="Bargein_enabled";
			}
		} 
		catch (Exception e)
		{
			util.errorLog(strtElementName, e);
		}
		finally 
		{
			util=null;
		}
		return exitState;
	}
}