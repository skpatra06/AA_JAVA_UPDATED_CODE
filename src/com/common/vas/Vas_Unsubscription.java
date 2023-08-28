package com.common.vas;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Vas_Unsubscription extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "Failure";
		Utilities util = new Utilities(data);
		try {
			
			if(util.IsValidRestAPIResponse())
			{
				String vasName=""+data.getSessionData("S_VAS_UNSUBSCRIPTION");
				data.addToLog(elementName," ------ Requested VAS UnSubscribe Successful ---- ");
				if(util.IsNotNUllorEmpty(vasName)&&!"null".equals(vasName)) 
				{
					vasName+="|"+data.getSessionData("S_UNSUB_VASNAME");
				}else {
					vasName=""+data.getSessionData("S_UNSUB_VASNAME");
				}
				data.setSessionData("S_VAS_UNSUBSCRIPTION", vasName);
				data.addToLog(elementName," UNSUB VAS :"+ vasName);
				exitState = "Success";
			}else{
				data.setSessionData("S_VAS_UNSUBSCRIPTION", "NA");
			}
		}catch(Exception e) {
			util.errorLog(exitState, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
