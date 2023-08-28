package com.prepaid.drc;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;



public class Check_Airtel_Money_Balance extends DecisionElementBase{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="AMB_NAVL";
		Utilities util=new Utilities(data);
		List<Map<String,String>> amList=null;
		try 
		{
			String amBalance="0.0";
			amList=(List<Map<String, String>>) data.getSessionData("S_AM_BALANCE_LIST");
			String currencyType=(String) data.getSessionData("S_AM_CURRENCY_TYPE");
			if(util.IsNotNUllorEmpty(amList)) 
			{
				for(Map<String,String> map:amList)
				{
					if(map.containsKey("currency")&&null!=currencyType&&currencyType.equalsIgnoreCase(map.get("currency"))) 
					{
						amBalance=map.containsKey("balance")? map.get("balance"):"0.0";
					}
				}
				
				Double userEnterredAmount=util.convertToDouble("S_RECHARGE_AMT");
				data.addToLog(elementName, "Recharge Amt : "+userEnterredAmount);
				if(Double.parseDouble(amBalance)>userEnterredAmount)
				{
					exitState="AMB_AVL";
				}
			}
			else
			{
				data.addToLog(elementName, "Null Value In AM list .... ");
			}
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			util=null;
		}
		return exitState;
	}

}
