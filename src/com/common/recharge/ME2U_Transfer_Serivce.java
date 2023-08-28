package com.common.recharge;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ME2U_Transfer_Serivce extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()){
				String status=(String) data.getSessionData("S_ME2U_STATUS");
				data.addToLog(elementName, "status of M2U transfer is :"+status);
				if(util.IsValidRestAPIResponse()){
					if("200".equals(status)){
						exitState="SU";
					}
				}
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}finally {
			util=null;
		}
		return exitState;
	}

}
