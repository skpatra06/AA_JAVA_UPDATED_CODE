package com.postpaid.niger;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_BalActiveLangCheck extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {

			String exitState="BalAud";
			Utilities util = new Utilities(data);
			
			try {
				String activeLang = String.valueOf(data.getSessionData("S_ACTIVE_LANG"));
				data.addToLog(elementName, "Active Language :"+activeLang);
				
				String balSMSLang = String.valueOf(data.getSessionData("S_BAL_SMS_LANG"));
				data.addToLog(elementName, "Languages for which balance SMS should be send "+balSMSLang);
				
				if(balSMSLang.contains(activeLang)) {
					exitState="BalSMS";
				}
				
			} catch (Exception e) {
				util.errorLog(elementName, e);
			}
			finally {
				util=null;
			}
			return exitState;
			
			
	}

}
