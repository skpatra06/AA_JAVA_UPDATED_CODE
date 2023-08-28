package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_Security_Question extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "SEQ_QUES_NAVL";
		Utilities util = new Utilities(data);
		try
		{
			if(util.IsValidRestAPIResponse()) 
			{
				String securityQuestion =String.valueOf(data.getSessionData("S_QUESTION_ID"));
				data.addToLog(strElementName, "S SECURITY QUESTION :"+securityQuestion);
				if(util.IsNotNUllorEmpty(securityQuestion))
				{
					exitState="SEQ_QUES_AVL";
					
				}
				else
				{
					util.setTransferData(data, "SEC_AGENT");
					data.addToLog(strElementName, "Security question not available");
				}
			}
			else
			{
				data.addToLog(strElementName, "Security question not available");	
				util.setTransferData(data, "SEC_AGENT");
			}
		}
		catch(Exception e){
			util.errorLog(strElementName, e);
		}
		finally{

		}
		return exitState ;
	}

}
