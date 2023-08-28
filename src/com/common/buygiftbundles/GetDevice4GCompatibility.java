package com.common.buygiftbundles;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class GetDevice4GCompatibility extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {


		String strExitState = "Failure";
		Utilities util=new Utilities(data);

		try
		{
			if(util.IsValidRestAPIResponse())
			{
				String strCompatible=(String) data.getSessionData("S_IS4GCOMPATIBLE");
				if("Yes".equalsIgnoreCase(strCompatible))
				{
					strExitState = "Success4G";
				}
				else
				{
					strExitState = "Success3G";
				}
			}

		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		return strExitState;
	}

}
