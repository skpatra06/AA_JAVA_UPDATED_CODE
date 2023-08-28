package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Profile_ResetPinWIthout_SecretWord extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try{
			if(util.IsValidRestAPIResponse()) {
					exitState = "SU";	
			}
		}catch (Exception e) {
			util.errorLog(elementName, e);
		}finally{
			util=null;
			}
		return exitState;
	}
}

