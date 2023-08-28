package com.prepaid.drc;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class SetBargin extends ActionElementBase{

	@Override
	public void doAction(String strElement, ActionElementData data) throws Exception {
		Utilities util=new Utilities(data);
		try {
		   data.setSessionData("S_BARGIN_FLAG","N");
		   data.setSessionData("S_BARGIN_RESET_FLAG", null);
		}catch (Exception e){
			util.errorLog(strElement, e);
		}
	}

}
