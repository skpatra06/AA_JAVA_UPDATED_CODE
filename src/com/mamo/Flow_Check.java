package com.mamo;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Flow_Check extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState ="";
		Utilities util = new Utilities(data);
		String shortCode ="";
		try {
			if(shortCode.equalsIgnoreCase("100"))
			{
				exitState ="Wh_List";
			}
			else if(shortCode.equalsIgnoreCase("323"))
			{
				exitState ="TSO";
			}
			else if(shortCode.equalsIgnoreCase("242"))
			{
				exitState ="CId_Offer";
			}

		} catch (Exception e) {
			util.errorLog(elementName, e);
		}
		return exitState;
	}
}
