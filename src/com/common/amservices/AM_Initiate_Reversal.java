package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Initiate_Reversal extends DecisionElementBase 
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		String strOPCOCode = (String) data.getSessionData("S_OPCO_CODE");
		try
		{
			String errorCode = String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
			if(util.IsValidRestAPIResponse())
			{
				exitState = "SU";
			}
			else if(util.IsNotNUllorEmpty(strOPCOCode)&&"ZM".equalsIgnoreCase(strOPCOCode)&&
					util.IsNotNUllorEmpty(errorCode)&&"1131".equalsIgnoreCase(errorCode))
			{
				data.addToLog(strElementName, "Transaction already initiated. Error code:"+errorCode);
				exitState = "Initiated_Already";
			}
		}
		catch(Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
            util=null;
		}
		return exitState ;
	}
}