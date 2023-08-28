package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_Repeat_Menu extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState ="";
		Utilities util = new Utilities(data);
		try {
			String featureAccessed = (String) data.getSessionData("S_LAST_FEATURE_ACCESSED");
			if(util.IsNotNUllorEmpty(featureAccessed))
			{
				data.addToLog(strElementName, "Menu Return Successfully");
					exitState=featureAccessed;
			}
		}catch(Exception e) {
			util.errorLog(strElementName, e);
		}finally {
			util = null;
		}
		return exitState;
	}

}
