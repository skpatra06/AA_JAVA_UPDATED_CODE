package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Unlock_Acct_EligibilityCheck extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try {

			//wallet min balance =5------- wallet max balance --2000
			String accountStatus=String.valueOf(data.getSessionData("S_AM_ACC_STATUS"));
			data.addToLog("user Airtel Money status  :",""+ accountStatus);

			Double walletBalance=0.0;
			Double wallet_MIN_limit=0.0;
			Double wallet_MAX_limit=0.0;

			String amBalance=String.valueOf(data.getSessionData("S_AM_BALANCE"));
			data.addToLog("user wallet balance is :", "" +amBalance);

			String minBalance=(String) data.getSessionData("S_AM_UNLOCK_MIN_BALANCE");
			String maxBalance=(String) data.getSessionData("S_AM_UNLOCK_MAX_BALANCE");
			try {
				walletBalance=Double.parseDouble(amBalance);
				
				wallet_MIN_limit=Double.parseDouble(minBalance);
				wallet_MAX_limit=Double.parseDouble(maxBalance);
			} catch (Exception e) {
				util.errorLog(elementName, e);

			}
			if(util.IsNotNUllorEmpty(amBalance) && util.IsNotNUllorEmpty(accountStatus)){


				if(accountStatus.equalsIgnoreCase("ACTIVE") && walletBalance<=wallet_MIN_limit)
				{
					exitState="RESET_ID";
					data.addToLog("Acount is : "+ accountStatus + " and AM balance is less than active balance:", "" +wallet_MIN_limit);

				}else if(accountStatus.equalsIgnoreCase("ACTIVE") && walletBalance>=wallet_MAX_limit){
					util.setTransferData(data, "BALANCE_CHECK_AGENT");
					exitState="AGENT_TRANSFER";
					data.addToLog("Acount is : "+ accountStatus + "   AM balance is greater than maxLimit active balance:", "" +wallet_MAX_limit);

				}else if(accountStatus.equalsIgnoreCase("DORMANT") && walletBalance<=wallet_MIN_limit){
					exitState="UNLOCK_ACC";
					data.addToLog("Acount is : "+ accountStatus + "  AM balance is less than minLimit:", "" +wallet_MIN_limit);

				}else if((accountStatus.equalsIgnoreCase("DORMANT") && walletBalance>=wallet_MIN_limit) || (accountStatus.equalsIgnoreCase("ACTIVE") && ((walletBalance)>wallet_MIN_limit)&&walletBalance<wallet_MAX_limit)){
					exitState="SECURITY_QUES";
					data.addToLog("Acount is : "+ accountStatus + "   wallet_min_limit :", "" +wallet_MIN_limit +" wallet_max_limit :"+wallet_MAX_limit);
				}
			}


		}catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
	}

}
