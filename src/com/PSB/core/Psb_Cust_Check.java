package com.PSB.core;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Psb_Cust_Check extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util=null;
		String kycstatus=null;
		try {				
			util = new Utilities(data);

			if(util.IsValidRestAPIResponse())
			{
				kycstatus=(String) data.getSessionData("S_KYCSTATUS");
				data.addToLog(elementName, "User  kyc status"+kycstatus);
				if(util.IsNotNUllorEmpty(kycstatus)&&kycstatus.equalsIgnoreCase("FULL")) {
					exitState="Authoreised";


				}
				else {
					exitState="notAuthoreised";
				}
			}
		}





		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			util=null;

		}
		return exitState;






	}



}
