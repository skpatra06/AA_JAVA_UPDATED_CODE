package com.common.creditservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_User_Choice extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="";
		Utilities util = new Utilities(data);
		try {
			String userChoice=util.getMenuElementValue();
			data.addToLog(elementName, "User Entered choice for Airtel Credit Service"+userChoice);
			if("1".equals(userChoice)){
				data.setSessionData("S_CREDIT_SERVICE_LIST", data.getSessionData("S_CREDIT_SERVICES_DATA"));

			}else if("2".equals(userChoice)){
				data.setSessionData("S_CREDIT_SERVICE_LIST", data.getSessionData("S_CREDIT_SERVICES_VOICE"));
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}
		return exitState;
	}

}
