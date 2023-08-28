package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Profile_KYC_Request_Acc_Unlock extends DecisionElementBase
{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		
		String exitState = "ER";
		Utilities util = new Utilities(data);
		String opcoCode="";
		try 
		{
			if(util.IsValidRestAPIResponse())
			{
				opcoCode=String.valueOf(data.getSessionData("S_OPCO_CODE"));
				String userBarred=""+data.getSessionData("S_USER_BARRED");
				data.addToLog("caller Registered With Airtel Money status : ",""+ userBarred);
				if("false".equalsIgnoreCase(userBarred))
				{
					exitState="SU";
				}
				else if("UG".equalsIgnoreCase(opcoCode)&&"True".equalsIgnoreCase(userBarred))
				{
				 	data.setSessionData("S_RS_STATUS_CODE","1006");
				}
			}
		} catch (Exception e) {
			data.addToLog(elementName, exitState);
		}finally {
			util=null;
		}
		return exitState;
	}
	
}
