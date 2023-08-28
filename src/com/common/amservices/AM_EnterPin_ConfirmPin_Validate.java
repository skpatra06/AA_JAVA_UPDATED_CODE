package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_EnterPin_ConfirmPin_Validate extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "NO";
		String S_EPIN=""+data.getSessionData("S_EPIN");
		String S_ECPIN=""+data.getSessionData("S_ECPIN");
		String S_ECPIN_MAXTRY=""+data.getSessionData("S_MAXPINTRY");
		data.addToLog(strElementName," Airtel Money Reenter PIN counter status S_MAXPINTRY : "+ S_ECPIN_MAXTRY );
		Utilities util = new Utilities(data);
		try{
			if(S_EPIN.equalsIgnoreCase(S_ECPIN)) {
				exitState = "YES";
			}else{
				if(S_ECPIN_MAXTRY.equalsIgnoreCase("null")){
					data.setSessionData("S_MAXPINTRY","1");
				}else{
					data.setSessionData("S_MAXPINTRY",""+(Integer.parseInt(S_ECPIN_MAXTRY)+1));
				}
			}
		}
		catch(Exception e){
			util.errorLog(strElementName, e);
		}
		finally{

		}
		return exitState ;
	}

}
