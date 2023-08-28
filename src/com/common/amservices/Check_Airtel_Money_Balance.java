package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;



public class Check_Airtel_Money_Balance extends DecisionElementBase{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="AMB_NAVL";
		Utilities util=new Utilities(data);
		try {
			double activeBalance = util.convertToDouble("S_AM_BALANCE");
			double userEnterredAmount=util.convertToDouble("S_RECHARGE_AMT");
			if(activeBalance>userEnterredAmount) {
				exitState="AMB_AVL";
			}
		} catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
