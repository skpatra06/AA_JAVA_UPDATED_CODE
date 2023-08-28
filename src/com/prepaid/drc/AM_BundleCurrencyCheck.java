package com.prepaid.drc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_BundleCurrencyCheck extends DecisionElementBase 
{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState="BUY_THROUGH_USD";
		Utilities util=new Utilities(data);
		try 
		{
			String currencyType=(String) data.getSessionData("S_AM_CURRENCY_TYPE");
			String rechargeUnit=String.valueOf(data.getSessionData("S_RECHARGE_AMT"));
			util.addToLog("Selected Currency Type : "+currencyType+" - Recharge Unit Amt : "+rechargeUnit);
			Double rechargeAmt=util.convertToDouble(strElementName, rechargeUnit);
			data.setSessionData("S_RECHARGE_AMT_UNIT", rechargeAmt);
			data.setSessionData("S_RECHARGE_AMT",rechargeAmt/100);
			if("CDF".equalsIgnoreCase(currencyType)&&util.IsNotNUllorEmpty(rechargeUnit)) 
			{
				exitState="BUY_THROUGH_CDF"; 
			}
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);
		}
		finally 
		{
			util=null;	
		}
		return exitState;
	}


}
