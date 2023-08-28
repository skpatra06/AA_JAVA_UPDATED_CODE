package com.postpaid;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Curret_Usage extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitstate = "Failure";
		Utilities util = new Utilities(data);
		try 
		{
			if(util.IsValidRestAPIResponse()) 
			{
				double totalCredit=0.0;
				double tempCredit=0.0;
				String totalAvailableCredit=String.valueOf(data.getSessionData("S_AVAILABLE_CREDIT_LIMIT"));
				String totalCreditLimit=String.valueOf(data.getSessionData("S_TOTAL_CREDIT_LIMIT"));
				String tempCreditLimit=String.valueOf(data.getSessionData("S_TEMP_CREDIT_LIMIT"));
				String totalUnbilledAmt=String.valueOf(data.getSessionData("S_TOTAL_UNBILLED_AMT"));
				String totalOutStandingBal=(String) data.getSessionData("S_OUTSTANDING_BALANCE");
				data.addToLog(elementName, "Total Credit Limit : "+totalCreditLimit +" Total Available Credit Limit :"+totalAvailableCredit);
				
				if(util.IsNotNUllorEmpty(totalAvailableCredit)&&util.IsNotNUllorEmpty(totalCreditLimit)&&util.IsNotNUllorEmpty(tempCreditLimit))
				{
					totalCredit=((Double.parseDouble(totalCreditLimit)+Double.parseDouble(tempCreditLimit))-Double.parseDouble(totalAvailableCredit));
					data.setSessionData("S_TOTAL_CREDIT_USAGE", util.parseAmount(String.valueOf(totalCredit)));
					tempCredit=(Double.parseDouble(totalCreditLimit)+Double.parseDouble(tempCreditLimit));
					data.setSessionData("S_TOTAL_CREDIT_LIMIT", String.valueOf(tempCredit));
					exitstate ="Success";
				}
				else if(util.IsNotNUllorEmpty(totalAvailableCredit)&&util.IsNotNUllorEmpty(totalCreditLimit))
				{
					totalCredit=(Double.parseDouble(totalCreditLimit)-Double.parseDouble(totalAvailableCredit));
					data.setSessionData("S_TOTAL_CREDIT_USAGE", String.valueOf(totalCredit));
					data.setSessionData("S_TOTAL_CREDIT_USAGE_INT", String.valueOf((int)totalCredit));
					exitstate ="Success";
				}
				else if(util.IsNotNUllorEmpty(totalAvailableCredit)&&util.IsNotNUllorEmpty(totalUnbilledAmt)&&util.IsNotNUllorEmpty(totalOutStandingBal))
				{
					totalCredit=(Double.parseDouble(totalUnbilledAmt)+Double.parseDouble(totalAvailableCredit))+Double.parseDouble(totalOutStandingBal);
					data.setSessionData("S_TOTAL_CREDIT_LIMIT",String.valueOf(totalCredit));
					data.setSessionData("S_TOTAL_CREDIT_USAGE", String.valueOf(totalUnbilledAmt));
					exitstate ="Success";
				}
			}
		}
		catch(Exception e)
		{
			util.errorLog(elementName,e);
		}
		finally
		{
			util = null;
		}
		return exitstate;
	}

}