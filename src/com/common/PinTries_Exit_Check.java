package com.common;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class PinTries_Exit_Check extends DecisionElementBase
{
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "INVALID_ENTRY";
		Utilities util = new Utilities(data);
		try {
			String menuElementExit=data.getExitStateHistory().get(data.getElementHistory().size()-4);
			data.addToLog(elementName, "Exit state from previous menu:"+menuElementExit);
			if("max_noinput".equalsIgnoreCase(menuElementExit))
			{
				exitState = "NO_INPUT_HOLD";
			}
			
			} catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
}
}
