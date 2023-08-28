package com.common.creditservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CheckStoreChoseLang extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util =new Utilities(data);
		try {
			if(util.IsValidDBSUResponse()){
				exitState="SU";
				
			}else if(util.IsValidDBNRResponse()){
				exitState="ER";
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}
		return exitState;
	}

}
