package com.common.vas;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Vas_Subscribe extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState ="Failure";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				String vasName=""+data.getSessionData("S_VAS_SUBSCRIPTION");
				data.addToLog(elementName," ------ Requested VAS Subscribe Successful ---- "+ " VAS SUB NAME :"+ vasName);
				if( !util.IsNotNUllorEmpty(vasName) || "NA".equalsIgnoreCase(vasName)){
					vasName=String.valueOf(data.getSessionData("S_SUB_VASNAME"));	
				}
				else if( util.IsNotNUllorEmpty(vasName) && !"NA".equalsIgnoreCase(vasName) ){	
					vasName+="|"+data.getSessionData("S_SUB_VASNAME");
				}
				if(!util.IsNotNUllorEmpty(vasName)) {
					vasName="NA";
				}
				data.setSessionData("S_VAS_SUBSCRIPTION", vasName);
				exitState = "Success";
			}
		} catch(Exception e) {
			util.errorLog(elementName, e);
		} finally {
			util=null;
		}
		return exitState;
	}
}
