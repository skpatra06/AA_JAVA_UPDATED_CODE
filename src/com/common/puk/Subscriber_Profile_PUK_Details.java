package com.common.puk;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

import com.util.Utilities;

public class Subscriber_Profile_PUK_Details extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				String PUK=(String) data.getSessionData("S_PUK1");
				data.addToLog(elementName, "---- PUK : "+PUK);
				if(util.IsNotNUllorEmpty(PUK)) {
					exitState="SU";
				}else {
					exitState="NR";
				}
			}

		} catch (Exception e) {
		util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
