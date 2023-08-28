package com.common.transfer;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class PaidAgentTransferBalCheck extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "N";
		Utilities util=new Utilities(data);
		try
		{
			Double strChargeAmount = Double.valueOf((String) data.getApplicationAPI().getApplicationData("AGENT_CHARGING_AMOUNT"));
			Double strBalAmount = util.convertToDouble("S_AIRTIME_BALANCE");
			if(strChargeAmount>=strBalAmount)
			{
				data.addToLog(strElementName, "Bundle amount "+strChargeAmount+" is greater than Balance Amount "+strBalAmount);
				strExitState="Y";
			}
			else
			{
				data.addToLog(strElementName, "Bundle amount "+strChargeAmount+" is less than Balance Amount "+strBalAmount);
				data.setSessionData("S_AGENT_CHARGING_AMT",strChargeAmount);
				strExitState="N";
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util=null;
		}
		return strExitState;
	}

}
