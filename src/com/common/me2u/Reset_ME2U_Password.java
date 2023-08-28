package com.common.me2u;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Reset_ME2U_Password extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try{
			if(util.IsValidRestAPIResponse()){
				exitState="SU";
			}
		}catch (Exception e) {
			data.addToLog(elementName, exitState);
		}finally {
			util=null;
		}
		return exitState;
	}
}
