package com.mamo;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundlePurchaseCheck extends DecisionElementBase {

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="Failure";
		Utilities util=new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				data.addToLog(elementName, " MAMO Bundle Purchase Is Successfull");
				 exitState="Success";
			}
			
		}catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
