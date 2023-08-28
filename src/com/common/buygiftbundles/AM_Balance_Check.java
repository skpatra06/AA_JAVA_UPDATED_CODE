package com.common.buygiftbundles;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Balance_Check extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);

		try
		{
			if(util.IsValidRestAPIResponse())
			{
				String strAvlBalance =  (String) data.getSessionData("S_AM_BALANCE");
				data.setSessionData("S_PAYMENT_MODE", "AIRTEL_MONEY");
				data.setSessionData("S_PYMT_MODE", "AA_PRP_CP_0168.wav");
				data.setSessionData("S_WALLET_TYPE", "S_AM_BALANCE");
				util.addToLog(strElementName+ " Available Airtel Money Balance:"+strAvlBalance,util.DEBUGLEVEL);
				strExitState = "Success";
			}
			else
			{
				String strPrevPage = (String) data.getSessionData("S_PREVIOUS_PAGE");
				if("DataBundle".equals(strPrevPage))
				{
					strExitState = "InvalidPIN";
				}
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
		}
		return strExitState;

	}
}
