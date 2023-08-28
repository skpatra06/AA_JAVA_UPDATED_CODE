package com.common.me2u;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Transaction_Reset_ME2U_Password extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try{
			if(util.IsValidRestAPIResponse()){
				String pinStatus=(String) data.getSessionData("S_STATUS");
				data.addToLog(elementName, "new M2U PIN Save :"+pinStatus);
				if("200".equals(pinStatus)){
					exitState="SU";
				}
			}
		}catch (Exception e) {
			data.addToLog(elementName, exitState);
		}finally {
			util=null;
		}
		return exitState;
	}
}
