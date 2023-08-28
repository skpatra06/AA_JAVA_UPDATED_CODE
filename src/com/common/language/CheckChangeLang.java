package com.common.language;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CheckChangeLang extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util =new Utilities(data);
		try {
			String changedActiveLang=(String) data.getSessionData("S_ACTIVE_LANG");
			if(util.IsValidDBSUResponse()  && util.IsNotNUllorEmpty(changedActiveLang)){
				exitState="SU";
				data.addToLog(elementName, "Updated Change Language is :"+changedActiveLang);
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}finally {
			util=null;
		}
		return exitState;
	}

}
