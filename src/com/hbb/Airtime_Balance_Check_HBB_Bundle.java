package com.hbb;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Airtime_Balance_Check_HBB_Bundle extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		List<Map<String,String>> listBalanceDetails= null;
		try
		{
			if(util.IsValidRestAPIResponse())
			{
				String strAvlBalance = "";
				listBalanceDetails= (List<Map<String,String>>) data.getSessionData("S_BALANCE_DETAILS");
				util.addToLog(strElementName + " Airtime Balance details fetched:"+listBalanceDetails,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(listBalanceDetails))
				{
					for(Map<String,String> mapDetails : listBalanceDetails)
					{
						if ("AIRTIME".equalsIgnoreCase(String.valueOf(mapDetails.get("balanceDescription")))) 
						{
							strAvlBalance =String.valueOf(mapDetails.get("balance"));
							data.addToLog(strElementName, "AIRTIME BALANCE : "+strAvlBalance);
							break;
						}
					}
					data.setSessionData("S_AIRTIME_BALANCE", strAvlBalance);
					data.setSessionData("S_PAYMENT_MODE", "AIR_TIME");
					data.setSessionData("S_WALLET_TYPE", "S_AIRTIME_BALANCE");
					data.setSessionData("S_PYMT_MODE", "AA_PRP_CP_0167.wav");
					data.addToLog(strElementName, "Available Airtime Balance:"+strAvlBalance);
					strExitState = "Success";
				}
				else
				{
					data.addToLog(strElementName, " Error fetching balance details: "+strAvlBalance);
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
			listBalanceDetails= null;
		}
		return strExitState;
	}

}
