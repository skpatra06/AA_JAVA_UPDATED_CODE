package com.common.paybill;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CheckEnterAmount extends DecisionElementBase 
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState="NO";
		Utilities util = new Utilities(data);
		try 
		{	
			String userChoice=""+data.getSessionData("S_PAYBILL_SELECTED_OPT");
			data.addToLog(elementName, "user choice is :"+userChoice);
			String am_Balance = String.valueOf(data.getSessionData("S_AM_BALANCE"));
			data.addToLog(elementName, "AM Balance is :"+am_Balance);
			double availableBalance=Double.parseDouble(am_Balance); 
			data.addToLog(elementName, "STRING to fload AM Balance :"+am_Balance);
			String outstandingBalance = String.valueOf(data.getSessionData("S_OUTSTANDING_BALANCE"));
			if(userChoice.equals("1"))
			{
				data.addToLog(elementName, "Outstanding Balance is :"+outstandingBalance);
				double outStandingBalance=Double.parseDouble(outstandingBalance);
				double host_outStandingBalance=Double.parseDouble(am_Balance); 
				data.addToLog(elementName, "STRING to fload AM Balance :"+host_outStandingBalance);
				if(outStandingBalance > availableBalance)
				{
					exitState="YES";

				}
				else
				{
					data.addToLog(elementName, "Outstanding balance is Less than Available Amount");
					data.setSessionData("S_BILL_AMT", String.valueOf(outStandingBalance));
				}
			}
			else if(userChoice.equals("2"))
			{
				String userEnterAmount = String.valueOf( data.getSessionData("S_BILL_AMT"));
				data.addToLog(elementName, "User Enter Amount is :"+userEnterAmount);
				double enterAmount=Double.parseDouble(userEnterAmount);
				data.addToLog(elementName, "string to fload enter Amount :"+enterAmount);
				if(enterAmount > availableBalance)
				{
					exitState="YES";
				}
				else
				{
					data.addToLog(elementName, "Enter Amount is Less than Available Amount");
					double outStandingBalance=Double.parseDouble(outstandingBalance);
					if(outStandingBalance > availableBalance)
					{
						exitState="YES";

					}
					else{
						data.addToLog(elementName, "Outstanding balance is Less than Entered Amount");	
					}
				}
			}
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			util = null;
		}
		return exitState;
	}
}
