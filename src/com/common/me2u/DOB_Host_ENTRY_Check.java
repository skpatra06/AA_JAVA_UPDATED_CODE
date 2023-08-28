package com.common.me2u;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class DOB_Host_ENTRY_Check extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState="STD";
		Utilities util=new Utilities(data);
		try
		{
			String selectedOption=util.getMenuElementValue();
			data.addToLog(elementName, "RE-ENTER DOB MENU : SELECTED OPTION : "+selectedOption );
			if("1".equals(selectedOption))
			{
				String repeatDOBHostCheck=String.valueOf(data.getSessionData("S_REPEAT_HOST_FLAG"));
				if("TRUE".equalsIgnoreCase(repeatDOBHostCheck))
				{
					exitState="SKIP"; 
				}
				else 
				{
					exitState="CheckDOB";
					data.setSessionData("S_REPEAT_HOST_FLAG","TRUE");
				}
			}
		} 
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		return exitState;
	}
}
