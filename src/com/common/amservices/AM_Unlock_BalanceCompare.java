package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Unlock_BalanceCompare extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		String exitState="NO";
		Utilities util=new Utilities(data);
		try
		{
			double AM_Min_Balance=0.0;
			String minBalance=String.valueOf(data.getSessionData("S_AM_UNLOCK_MIN_BALANCE"));
			try 
			{
				AM_Min_Balance=Double.parseDouble(minBalance);
			}
			catch (Exception e)
			{ 
				AM_Min_Balance=500;
			}
			String amBalance=String.valueOf(data.getSessionData("S_AM_BALANCE"));
			data.addToLog(strElementName, "AM UNLOCK BALANCE : "+amBalance);
			if(util.IsNotNUllorEmpty(amBalance)) 
			{
				if((Double.parseDouble(amBalance.replace(",", "")))>AM_Min_Balance) 
				{
					//Assigning Transfer Code
					util.setTransferData(data,"AM_ACCOUNT_UNLOCK");
					exitState="YES";
				}
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