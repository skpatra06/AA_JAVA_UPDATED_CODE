package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_PIN_Validation_check extends DecisionElementBase {

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse())
			{
				data.addToLog(elementName, "PIN validation successful");
				exitState="SU";
			}
			else
			{
				data.addToLog(elementName, "PIN validation failed");
				String strPrevPage = (String) data.getSessionData("S_PREVIOUS_PAGE");
				if("DataBundle".equals(strPrevPage))
				{
					exitState = "Bundles_Ret";
				}
			}
		}
		catch(Exception e){
			util.errorLog(elementName, e);
		}
		finally{
			util =null;
		}
		return exitState;
	}
}
