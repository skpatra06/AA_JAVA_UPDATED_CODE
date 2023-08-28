package com.common;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

public class CheckDOB extends DecisionElementBase {

	@Override
	public String doDecision(String name, DecisionElementData data) throws Exception {
		String exitState = "STD";

		try {
			String Count = (String) data.getSessionData("S_RE_ENTRY_COUNT");
			data.addToLog(name, "The Re-Entry Count :" + Count);
			int entryCount = Integer.parseInt(Count);
			String menuOption = data.getElementData("AA_PRP_XX_MN_0073_DM", "value");
			data.addToLog(name, "The Entered MenuOption : " + menuOption);
			if ("1".equals(menuOption)) {
				exitState = "1";
			} else {
				entryCount++;
				if (3 > entryCount) {
					exitState = "2";
				}
			}
		} catch (Exception e) {
         
		} finally {

		}
		return exitState;
	}
}
