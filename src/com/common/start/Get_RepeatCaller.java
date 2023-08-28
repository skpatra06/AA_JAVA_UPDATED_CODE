package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Get_RepeatCaller extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidDBSUResponse()) {
				String calledTime = (String) data.getSessionData("S_LAST_CALLED_TIME");
				if(util.diffInHours(calledTime, "HH:mm")<24)
				{
					exitState = "Yes";
				}
			}
			else if(util.IsValidDBNRResponse()) {
				exitState =" NO";
			}
		}catch(Exception e) {
			util.errorLog(strElementName, e);

		}finally{
			util = null;
		}
		return exitState;
	}
}
