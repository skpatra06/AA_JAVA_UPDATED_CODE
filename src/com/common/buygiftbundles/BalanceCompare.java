package com.common.buygiftbundles;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BalanceCompare  extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "";
		Utilities util=new Utilities(data);
		try
		{
			String strWalletType=(String) data.getSessionData("S_WALLET_TYPE");
			Double strBundleAmount = util.convertToDouble("S_RECHARGE_AMT");
			Double strBalAmount = util.convertToDouble(strWalletType);
			if(strBundleAmount>strBalAmount)
			{
				data.addToLog(strElementName, "Bundle amount "+strBundleAmount+" is greater than Balance Amount "+strBalAmount);
				strExitState="Y";
			}
			else
			{
				data.addToLog(strElementName, "Bundle amount "+strBundleAmount+" is less than Balance Amount "+strBalAmount);
				strExitState="N";
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
