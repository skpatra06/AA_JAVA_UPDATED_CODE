package com.common.buygiftbundles;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundleAPIHitCheck extends DecisionElementBase{

	@Override
	public String doDecision(String name, DecisionElementData data) throws Exception {
		String strExitState = "NoHit";
		String strElementName="BundleAPIHitCheck";
		Utilities util=new Utilities(data);
		
		try
		{
			String strBundleHitDate = (String) data.getApplicationAPI().getApplicationData("S_BUNDLE_API_HIT_DATE");
			util.addToLog(strElementName+ " Date fetched "+strBundleHitDate,util.DEBUGLEVEL);
			if(!util.IsNotNUllorEmpty(strBundleHitDate))
			{
				data.addToLog(strElementName, "Hit date is empty");
				strExitState = "APIHit";
			}else if(util.dateDiffInDays(strBundleHitDate, "ddMMyy")>0)
			{
				data.addToLog(strElementName, "Hit date is before current date");
				strExitState = "APIHit";
			}else
			{
				data.addToLog(strElementName, "Hit date is same as current date");
				strExitState = "NoHit";
			}
		}
		catch (Exception e)
		{
			util.errorLog(name,e);
		}
		finally
		{
			util = null;
		}
		return strExitState;
	}

}
