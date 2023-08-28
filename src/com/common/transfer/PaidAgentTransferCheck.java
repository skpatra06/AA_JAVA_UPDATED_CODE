package com.common.transfer;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class PaidAgentTransferCheck extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "N";
		Utilities util=new Utilities(data);
		try {
			strExitState= util.getChargeApplicable(data,strElementName)?"Y":"N";
		}catch (Exception e) {
			util.errorLog(strElementName, e);
		}
		return strExitState;
	}
}
