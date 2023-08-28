package com.common.buygiftbundles;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundlesAMCallerCheck extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "NA";
		Utilities util=new Utilities(data);
		try
		{
			String strAMCallerFlag = (String) data.getSessionData("S_IS_AM_CALLER");
			Double BundleAmount = util.convertToDouble("S_RECHARGE_AMT");
			Double BalAmount = util.convertToDouble("S_AIRTIME_BALANCE");
			util.addToLog(strElementName+ " Bundle amount: "+BundleAmount+"\t Airtime Balance: "+BalAmount+"\t AM check flag: "+strAMCallerFlag, util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(strAMCallerFlag)&& null!=BundleAmount && null !=BalAmount)
			{
				if("Y".equalsIgnoreCase(strAMCallerFlag))
				{
					if(BundleAmount<=BalAmount)
					{
						strExitState="Y";
						data.setSessionData("S_BUNDLE_AIRTIME_FLAG", "Y");
					}
					else
					{
						strExitState="AM_Only";
					}
					
				}else
				{
					if(BundleAmount<=BalAmount)
					{
						data.setSessionData("S_BUNDLE_AIRTIME_FLAG", "Y");
						strExitState="AirtimeOnly";
					}else
					{
						data.setSessionData("S_PAYMENT_MODE", "NA");
						strExitState="NA";
					}
				}
			}
			else
			{
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
