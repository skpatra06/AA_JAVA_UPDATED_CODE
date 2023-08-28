package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

import com.util.Utilities;

public class Fraud_Caller extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitstate = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			if(util.IsValidDBSUResponse()) 
			{
				exitstate ="FRAUD";
			}
			else if(util.IsValidDBNRResponse()) {
				exitstate ="SU";
			}

		}catch(Exception e) 
		{
			util.errorLog(strElementName, e);
		}finally{
			util = null;
		}
		return exitstate;
	}

}
