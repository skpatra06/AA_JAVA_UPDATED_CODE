package com.common;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Sim_Stolen_UnBarring extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try
		{
			String strStatusCode =String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
			data.addToLog("RESPONSE CODE : ", strStatusCode);
			if(util.IsValidRestAPIResponse())
			{
				String kycDOB=(String) data.getSessionData("S_KYC_DOB");
				if(util.IsNotNUllorEmpty(kycDOB))
				{
					exitState = "SuccessMatch";
				}
				else
				{
					data.setSessionData("S_STR_ELEMENTEXIT","max_nomatch");
					exitState="SuccessNotMatch";
				}
			}
			else
			{
				if("514".equalsIgnoreCase(strStatusCode))
				{
					data.setSessionData("S_STR_ELEMENTEXIT","max_nomatch");
					exitState="SuccessNotMatch";
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
