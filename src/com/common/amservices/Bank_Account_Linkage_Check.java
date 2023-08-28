package com.common.amservices;

import java.util.List;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Bank_Account_Linkage_Check extends DecisionElementBase 
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState="ER";
		Utilities util= new Utilities(data);
		List< Map< String, Object>> availableAccountlist=null;
		try 
		{
			String statusCode = String.valueOf(data.getSessionData("S_STATUS_CODE"));
			data.addToLog("Status code is :", " "+statusCode);
			if("200".equals(statusCode))
			{
				availableAccountlist = (List<Map<String, Object>>) data.getSessionData("S_BANK_ACCOUNT_DETAILS");
				data.addToLog("Available Account List :", ""+availableAccountlist);
				if(util.IsNotNUllorEmpty(availableAccountlist))
				{	
					for(Map<String,Object> accountDetails :availableAccountlist)
					{
						if((accountDetails.containsKey("accountNumber")  && util.IsNotNUllorEmpty(String.valueOf(accountDetails.get("accountNumber")))) ||(accountDetails.containsKey("branchCode")  && util.IsNotNUllorEmpty(String.valueOf(accountDetails.get("branchCode"))))  ||
								(accountDetails.containsKey("bankName") && util.IsNotNUllorEmpty(String.valueOf(accountDetails.get("bankName")))) ||(accountDetails.containsKey("bankID") && util.IsNotNUllorEmpty(String.valueOf(accountDetails.get("bankID")))) || 
								(accountDetails.containsKey("userGarde") && util.IsNotNUllorEmpty(String.valueOf(accountDetails.get("userGarde"))))){
							exitState="Account_Linked";	
						}
						else
						{
							exitState="No_Account_Linked";	
						}
					}
				}

			}
			else if("400".equals(statusCode))
			{
				util.addToLog("Status code :"+statusCode);
				exitState="No_Account_Linked";
			}
		} 
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			availableAccountlist=null;
			util=null;
		}
		return exitState;
	}
}