package com.prepaid.drc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AMCurrencyTypeAssign extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState="2";
		Utilities util=new Utilities(data);
		try 
		{
			Double rechargeAMt=util.convertToDouble("S_RECHARGE_AMT_UNIT");
			String menuOption=String.valueOf(util.getMenuElementValue());
			data.addToLog(strElementName, "Recharge Amount : "+rechargeAMt+" : Menu Option : "+menuOption);
			data.setSessionData("S_RECHARGE_AMT",rechargeAMt/100);
			if("1".equalsIgnoreCase(menuOption))
			{
                 data.setSessionData("S_AM_CURRENCY_TYPE","USD");
                 exitState=""+menuOption;
			}
			else
			{
				data.setSessionData("S_AM_CURRENCY_TYPE","CDF");
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
