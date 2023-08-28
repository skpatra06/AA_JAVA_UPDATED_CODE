package com.mamo;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Balance_Check extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "InSufficient_NOBalance";
		Utilities util = new Utilities(data);
		try {
			String balance=null;
			//data.setSessionData("S_LAST_DIGITS", util.GetLastDigits(data.getAni()));
			String bundleAmount=null;
			if (util.IsValidRestAPIResponse())
			{
				List<Map<String, Object>> availableAirtimeBalance = (List<Map<String, Object>>) data.getSessionData("S_BALANCE_DETAILS");
				data.addToLog("BalanceBundle Details :", "" + availableAirtimeBalance);
				if (util.IsNotNUllorEmpty(availableAirtimeBalance))
				{
					for (Map<String, Object> balanceMap:availableAirtimeBalance)
					{
						if ("AIRTIME".equalsIgnoreCase(String.valueOf(balanceMap.get("balanceDescription")))) 
						{
							balance =String.valueOf(balanceMap.get("balance"));
							break;
						}
					}
					bundleAmount=String.valueOf(data.getSessionData("S_RECHARGE_AMT"));
					data.addToLog(strElementName, "AVAILABLE BALANCE : "+balance+" RECHARGE AMOUNT : "+bundleAmount);
					if(util.IsNotNUllorEmpty(balance)&&util.IsNotNUllorEmpty(bundleAmount)) 
					{
						if(Double.parseDouble(bundleAmount)<=Double.parseDouble(balance))
						{
							data.setSessionData("S_PAYMENT_MODE","AIR_TIME");
							data.setSessionData("S_BUNDLE_NUM",data.getSessionData("S_CLI"));
							exitState = "Sufficient_Balance";
						}
					}
				}
			} 
		}catch (Exception e)
		{
			util.errorLog(strElementName, e);
		} 
		finally
		{
			util = null;
		}
		return exitState;
	}
}
