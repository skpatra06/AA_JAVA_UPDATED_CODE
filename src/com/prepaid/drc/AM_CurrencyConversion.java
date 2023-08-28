package com.prepaid.drc;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_CurrencyConversion extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception 
	{
		String exitState="ER";
		Utilities util=new Utilities(data);
		try 
		{
		   if(util.IsValidRestAPIResponse())
		   {
			   String cdfConvertedValue=String.valueOf(data.getSessionData("S_CURR_CONV_VALUE"));
			   if(util.IsNotNUllorEmpty(cdfConvertedValue))
			   {
				   data.addToLog(strElementName, "USD TO CDF Converted Value is : "+cdfConvertedValue);
				   data.setSessionData("S_RECHARGE_AMT", cdfConvertedValue);
				   exitState="SU";
			   }
			   else
			   {
				 data.addToLog(strElementName,"Null Value Occurs In CDF Value");  
			   }
			  
		   }
		}
		catch (Exception e) 
		{

		}
		finally
		{
			util=null;
		}
		return exitState;
	}
}