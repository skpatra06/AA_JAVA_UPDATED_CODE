package com.common.recharge;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Recharge_Voucher_Beneficiary_Sms extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		Utilities util = new Utilities(data);
		String callingNum = "";
		String rechargeNumber = "";
		String existState = "ER";
		try {
			callingNum = (String)data.getSessionData("S_CLI");
			rechargeNumber = (String)data.getSessionData("S_RMOBILENUM");
			String statusCode = String.valueOf(data.getSessionData("S_STATUS_CODE"));
			data.addToLog(existState, statusCode);
			if("200".equals(statusCode))
			{
				if(util.IsNotNUllorEmpty(rechargeNumber)&&util.IsNotNUllorEmpty(callingNum) 
						&& !rechargeNumber.equalsIgnoreCase(callingNum)) {
					existState = "SU";
					data.addToLog(strElementName, " Sending SMS for Recharged Number  : " +rechargeNumber);
				}
			}
		}catch(Exception e){
			util.errorLog(strElementName, e);
		}finally{
			util = null;
		}
		return existState;
	}
}