package com.common.transfer;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AgentTransferChargingCheck extends DecisionElementBase{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				data.addToLog(strElementName, "Agent Transfer Charging is Successful");
				strExitState = "Success";
			}
		}catch (Exception e){
			util.errorLog(strElementName, e);
		}finally{
			util=null;
		}
		return strExitState;
	}
}
