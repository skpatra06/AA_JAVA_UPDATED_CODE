package com.common.recharge;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

import com.util.Utilities;

public class Subscriber_Transaction_Voucher_Recharge extends DecisionElementBase{

	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) 
			{
				data.addToLog(elementName, "voucher recharge successful");
				exitState="SU";
			}
		} catch (Exception e) 
		{
			util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
