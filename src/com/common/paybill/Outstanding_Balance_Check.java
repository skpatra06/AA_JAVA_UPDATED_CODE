package com.common.paybill;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Outstanding_Balance_Check extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="NO";
		Utilities util = new Utilities(data);
		try {
			String outstandingBalance = (String) data.getSessionData("S_OUTSTANDING_BALANCE");
			data.addToLog(elementName, "Outstanding Balance is :"+outstandingBalance);
			float outBalance=Float.parseFloat(outstandingBalance); 
			data.addToLog(elementName, "STRING to fload AM Balance :"+outBalance);
			if(outBalance == 0.00){
				exitState="YES";
				
			}else{
				exitState="NO";
				
			}
		} catch (Exception e) {
			util.errorLog(elementName, e);

		}finally{
			util = null;
		}
		return exitState;
	}

}
