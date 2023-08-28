package com.common.datainternetsetttings;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Profile_SendInternetSetting extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()){
				String CLI=""+data.getSessionData("S_INTERNET_SETTING_MOBILENUM");
				data.setSessionData("S_LAST_DIGITS",util.GetLastDigits(CLI));
				exitState="SU";
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}finally {
			util=null;
		}
		return exitState;
	}

}
