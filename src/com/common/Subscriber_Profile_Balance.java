package com.common;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Profile_Balance extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "Failure";
		Utilities util = new Utilities(data);
		List<Map<String, Object>> availableAirtimeBalance = null;
		try {
			String balance=null;
			data.setSessionData("S_LAST_DIGITS", util.GetLastDigits(data.getAni()));
			if (util.IsValidRestAPIResponse())
			{
				availableAirtimeBalance = (List<Map<String, Object>>) data.getSessionData("S_BALANCE_DETAILS");
				util.addToLog("BalanceBundle Details : " + availableAirtimeBalance,util.DEBUGLEVEL);
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
					data.addToLog(strElementName, "GSM / AIRTIME BALANCE :"+balance);
					data.setSessionData("S_AVAILABLE_BALANCE", balance);
					
					if(util.IsNotNUllorEmpty(balance))
					{
						String opcoCode=String.valueOf( data.getSessionData("S_OPCO_CODE"));
						double ambalance=util.convertToDouble(strElementName, balance);
					    String unitType="UNITS.wav";
						if("CD".equalsIgnoreCase(opcoCode)) 
						{
							if(-10<(int)ambalance&&(int)ambalance<10) {
								unitType="UNIT.wav";
							}
							data.setSessionData("S_AIRTIME_UNITTYPE", unitType);
						}
						exitState = "SuccessY";
					}else {
						exitState = "SuccessN";	
					}

				}else {
					exitState = "SuccessN";	
				}
			}
		} 
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		} 
		finally
		{
			availableAirtimeBalance = null;
			util = null;
		}
		return exitState;
	}
}
