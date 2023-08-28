package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Profile_KYC_Request_Caller_Check extends DecisionElementBase
{
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try {
			//data.addToLog(elementName,"Element data: "+data.getElementData("AA_PRP_XX_MN_0006_DM", "value"));
			if(util.IsValidRestAPIResponse())
			{
				String strAcStatus=String.valueOf(data.getSessionData("S_AM_ACC_STATUS"));
				data.addToLog("caller Registered With Airtel Money status : ",""+ strAcStatus);
				String balance=String.valueOf(data.getSessionData("S_AM_BALANCE"));
				data.addToLog("caller Registered With Airtel Money status : ",""+ strAcStatus+" AIRTEL MONEY BALANCE :"+balance);
				data.setSessionData("S_AM_BALANCE",balance.replaceAll(",",""));
				if("active".equalsIgnoreCase(strAcStatus)) {
					data.setSessionData("S_IS_AM_CALLER", "Y");
					exitState="Yes";
				}else {
					data.setSessionData("S_IS_AM_CALLER", "N");
					exitState="No";
				}
			}
			else
			{
				data.setSessionData("S_IS_AM_CALLER", "N");
			}
		} catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
