package com.common.datainternetsetttings;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class DataInternetSettingUserChoiceCheck extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="2";
		Utilities util = new Utilities(data);
		try {
			String userChoice=data.getElementData("AA_PRP_XX_MN_0041_DM", "value");
			data.addToLog(elementName, "user Enter Choice for menu 0041 Internet Setting :" +userChoice);
			String toContine= util.getMenuElementValue();
			data.addToLog(elementName, "To proceed user enter :" +toContine);
			if("1".equals(toContine)){
				if("3".equals(userChoice)){
					exitState="INTERNET_SET";

				}else{
					data.setSessionData("S_BUNDLE_NUM", data.getSessionData("S_INTERNET_SETTING_MOBILENUM"));
					exitState="GIFT_DATA";
				}
			}else{
				exitState="2";
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}
		return exitState;
	}

}
