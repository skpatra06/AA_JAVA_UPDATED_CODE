package com.common.buygiftbundles;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class FreqBundlesInsert extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		try
		{
			if(util.IsValidDBSUResponse())
			{

				util.addToLog(strElementName+ " Frequent bundle inserted successfully",util.DEBUGLEVEL);
				strExitState = "Success";
			}
			else
			{
				util.addToLog(strElementName+ " Frequent bundle insert failed",util.DEBUGLEVEL);
				data.setSessionData("S_FREQUENT_BUNDLE",null);
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
