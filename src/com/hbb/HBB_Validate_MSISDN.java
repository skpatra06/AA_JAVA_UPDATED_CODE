package com.hbb;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class HBB_Validate_MSISDN extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitstate = "Failure";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				String broadband=""+data.getSessionData("S_BROADBAND");
				data.addToLog(" --- Broadband ---", broadband);
				if(util.IsNotNUllorEmpty(broadband)){
					if("true".equalsIgnoreCase(broadband)) {
						exitstate ="HBB";
					}else {
						exitstate ="NON_HBB";
					}
				}
			}
		}catch(Exception e){
			util.errorLog(strElementName,e);
		}finally{
			util = null;
		}
		return exitstate;
	}
}