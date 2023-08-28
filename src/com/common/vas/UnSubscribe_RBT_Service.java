package com.common.vas;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class UnSubscribe_RBT_Service extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitstate = "";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				data.addToLog(elementName, "For unsubscribing user from hello tunes is success");
				exitstate = "Success";
			}
			else {
				data.addToLog(elementName, "API Response is failed");
				exitstate = "Failure";
			}
		}catch(Exception e) {
			util.errorLog(elementName, e);
		}
		finally
		{
			util=null;
		}
		return exitstate;
	}
	
}
