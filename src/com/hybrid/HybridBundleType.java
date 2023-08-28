package com.hybrid;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class HybridBundleType extends ActionElementBase {
	
	
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception {
		Utilities util=new Utilities(data);
		String strBundleType="";
		try
		{
			String strUserInput= data.getElementData("AA_HY_MN_0004_DM", "value");
			switch (strUserInput)
			{
			
			case "5":
				strBundleType = "SMS";
				break;
			default:
				strBundleType = "VOICE";
				break;
			}
			
			data.addToLog(strElementName, "Setting bundle type as "+strBundleType);
			data.setSessionData("S_BUNDLE_TYPE",strBundleType);
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}

	}
}
