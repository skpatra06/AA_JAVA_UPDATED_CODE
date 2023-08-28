package com.common;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class RepeatMenuCheck extends DecisionElementBase {

	@Override
	public String doDecision(String elementElement, DecisionElementData data) throws Exception {
		String exitState="Retry";
		Utilities util=new Utilities(data);
		try {
			String repeatMaxLimit=String.valueOf(data.getSessionData("S_REPEAT_MAX_LIMIT"));
            int maxLimit=0;
			try {
            	maxLimit=Integer.parseInt(repeatMaxLimit);
            }catch (Exception e) {
				maxLimit=3; // 
			}
			String repeatCount=String.valueOf(data.getSessionData("S_REPEAT_COUNTER"));
			if(!util.IsNotNUllorEmpty(repeatCount)) {
				repeatCount="0";
			}
			int menuCount=Integer.parseInt(repeatCount);
			menuCount++;
			data.addToLog(elementElement, "--- REPEAT MENU COUNT : "+menuCount+" -----");
			if(menuCount<maxLimit) 
			{
				exitState="Retry";
			}else{
				data.addToLog(elementElement, "--- REPEAT MENU TRIES EXCEEDED -----");
				data.setSessionData("DTMF_KEYPRESS",null);
				exitState="Exceed";
			}
			data.setSessionData("S_REPEAT_COUNTER", menuCount);
		}catch (Exception e) {
			util.errorLog(elementElement, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
