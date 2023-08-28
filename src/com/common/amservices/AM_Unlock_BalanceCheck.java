package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Unlock_BalanceCheck extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util= new Utilities(data);
		Double walletBalance=0.00;
		Double wallentMinLimit=0.00;
		Double walletMaxLimit=0.00;
		try 
		{
			String accountStatus = String.valueOf(data.getSessionData("S_AM_ACC_STATUS"));
			String amBalance = String.valueOf(data.getSessionData("S_AM_BALANCE"));
			data.addToLog("user Airtem Money Account status is :",""+ accountStatus);
			data.addToLog("user wallet balance is :",""+ amBalance);
			
			String minBalance=String.valueOf(data.getSessionData("S_AM_UNLOCK_MIN_BALANCE_LIMIT"));
			String maxBalance=String.valueOf(data.getSessionData("S_AM_UNLOCK_MAX_BALANCE_LIMIT"));
			data.addToLog("Minimum AM wallet balance Limit is :"+minBalance,"Maximum AM wallet balance Limit is :" +maxBalance);
			walletBalance= Double.parseDouble(amBalance);
			wallentMinLimit=Double.parseDouble(minBalance);
			walletMaxLimit=Double.parseDouble(maxBalance);
			if(util.IsNotNUllorEmpty(amBalance) && util.IsNotNUllorEmpty(accountStatus))
			{
				if(walletBalance>=walletMaxLimit) 
				{
					util.addToLog("wallet balance is High");
					exitState="High_Balance";	
				}
				else if(walletBalance>=wallentMinLimit && walletBalance <=walletMaxLimit)
				{
					util.addToLog("wallet balance is less");
					exitState="Low_Balance";
				}	
			}
		} 
		catch (Exception e)
		{
			data.addToLog(elementName, exitState);
		}
		finally
		{
			util=null;
		}
		return exitState;
	}
}
