package com.common.buygiftbundles;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.google.gson.Gson;
import com.util.Utilities;

public class BundlePurchaseCheck extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		Object frequentObj= null;
		Gson gsonObj = null;
		try
		{
			if(util.IsValidRestAPIResponse())
			{
				String strTransId = (String) data.getSessionData("S_AM_TRANSACTIONID");
				if(util.IsNotNUllorEmpty(strTransId))
				{
					frequentObj=data.getSessionData("S_FREQUENT_BUNDLE");
					gsonObj = new Gson();
					data.setSessionData("S_FREQUENT_BUNDLE",gsonObj.toJson(frequentObj));
					data.addToLog(strElementName, "bundle purchase successful");
					strExitState = "Success";
				}
				else
				{
					data.addToLog(strElementName, "bundle purchase failed");
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
			frequentObj= null;
			gsonObj = null;
		}
		return strExitState;

	}
}
