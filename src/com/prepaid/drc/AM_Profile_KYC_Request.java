package com.prepaid.drc;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Profile_KYC_Request extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		List<Map<String,String>> AMBalList=null;
		try 
		{
			if(util.IsValidRestAPIResponse()) 
			{
				AMBalList=(List<Map<String, String>>) data.getSessionData("S_AM_BALANCE_LIST");
				String currencyType=(String) data.getSessionData("S_CURRENCY_CODE");
				String balance="";
				util.addToLog("AM LIST MAP : "+AMBalList+" \n Selected Currency Type : "+currencyType);
				if(util.IsNotNUllorEmpty(AMBalList)) 
				{
					for(Map<String,String> mapList:AMBalList)
					{
						if(mapList.containsKey("currency")&&util.IsNotNUllorEmpty(currencyType)&&currencyType.equalsIgnoreCase(mapList.get("currency"))) 
						{
							balance=mapList.containsKey("balance")?mapList.get("balance"):"";
						}
					}
					data.addToLog(elementName," AIRTEL MONEY ALANCE :"+balance);
					if(util.IsNotNUllorEmpty(balance))
					{
						data.setSessionData("S_AM_BALANCE",balance.replaceAll(",",""));
						data.setSessionData("S_IS_AM_CALLER","Y");
						exitState="RC";
					}
					else
					{
						data.setSessionData("S_IS_AM_CALLER","N");
						exitState="NRC";
					}
				}
				else
				{
					data.addToLog(elementName,"Null Value Occurs In AM Bal List ");
					data.setSessionData("S_IS_AM_CALLER","N");
					exitState="NRC";
				}
			}
			else
			{
				data.setSessionData("S_IS_AM_CALLER","N");
			}
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally 
		{
			AMBalList=null;
			util=null;
		}
		return exitState;
	}
}
