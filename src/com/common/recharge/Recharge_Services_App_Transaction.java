package com.common.recharge;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

import com.util.Utilities;

public class Recharge_Services_App_Transaction extends DecisionElementBase{

	@Override
	public String doDecision(String elmentName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				String rechargeStatus=(String)data.getSessionData("S_AM_STATUS");
				data.addToLog(elmentName,"RECHARGE STATUS CODE "+rechargeStatus);
				if("200".equals(rechargeStatus)) {
					exitState="SU";
				}
			}

		} catch (Exception e) {
		  util.errorLog(exitState, e);
		}finally {
			util=null;
		}
		return exitState;
	}
}
