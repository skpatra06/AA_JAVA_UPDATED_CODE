package com.common.creditservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Loan_Service_Apply extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()){
				exitState="SU";
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}finally {
			util=null;
		}
		return exitState;
	}

}
