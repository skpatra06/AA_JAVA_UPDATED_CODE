package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_AM_Wallet_Balance extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState="NO";
		Utilities util=new Utilities(data);
		try {
			double am_wallet_balance=0.0;
			String amWallentBalanceLimit=String.valueOf(data.getSessionData("S_AM_WALLET_BALANCE_MIN_LIMIT"));
			data.addToLog(strElementName, "AM WOLLET BALANCE LIMIT : " +amWallentBalanceLimit);
			String amBalance=String.valueOf(data.getSessionData("S_AM_BALANCE"));
			data.addToLog(strElementName, "AM UNLOCK BALANCE : "+amBalance);
			if(util.IsNotNUllorEmpty(amWallentBalanceLimit)) {
				am_wallet_balance = Double.parseDouble(amWallentBalanceLimit);
			}else {
				data.addToLog(strElementName, " Null Value Occurs In AM Wallet Balance Min Limit Session Var . So Default Value Consider to be 1000 ");
				am_wallet_balance=1000; /** Default Value */
			}
			if(util.IsNotNUllorEmpty(amBalance)){
				if(Double.parseDouble(amBalance)<=am_wallet_balance){
					exitState="YES";
				}else if(Double.parseDouble(amBalance)>am_wallet_balance){
					exitState="NO";
				}
			}
		} catch (Exception e) {
			util.errorLog(strElementName, e);
		} finally {
			util=null;
		}
		return exitState;
	}
}
