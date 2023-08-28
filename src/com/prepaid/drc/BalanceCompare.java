package com.prepaid.drc;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BalanceCompare  extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception 
	{
		String strExitState = "Y";
		Utilities util=new Utilities(data);
		List<Map<String,String>> amList=null;
		try
		{ 
			amList=(List<Map<String, String>>) data.getSessionData("S_AM_BALANCE_LIST");
			Double strBundleAmount = util.convertToDouble("S_RECHARGE_AMT");
			Double strBalAmount = 0.0;
			String currencyType=(String) data.getSessionData("S_AM_CURRENCY_TYPE");
			String amBalance="";
			if(!util.IsNotNUllorEmpty(currencyType)) 
			{
				data.addToLog(strElementName, "Null Value In Currency Type");
				currencyType="NA";
			}
			if(util.IsNotNUllorEmpty(amList)) 
			{
				
				for(Map<String,String> map:amList)
				{
					if(map.containsKey("currency")&&currencyType.equalsIgnoreCase(map.get("currency"))) {
						amBalance=map.containsKey("balance")? map.get("balance"):"";
					}
				}
				try
				{
					strBalAmount=Double.parseDouble(amBalance);
				}
				catch (Exception e)
				{
					util.errorLog(strElementName, e);
				}
				
				if(strBundleAmount>strBalAmount)
				{
					data.addToLog(strElementName, "Bundle amount "+strBundleAmount+" is greater than Balance Amount "+strBalAmount);
					strExitState="Y";
				}
				else
				{
					
					data.addToLog(strElementName, "Bundle amount "+strBundleAmount+" is less than Balance Amount "+strBalAmount);
					strExitState="N";
				}	
			}
			else
			{
				data.addToLog(strElementName,"Null Value in AM List");
				strExitState="Y";
			}
			//String strWalletType=(String) data.getSessionData("S_WALLET_TYPE");
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			amList=null;
			util = null;
		}
		return strExitState;

	}
}
