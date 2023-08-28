package com.postpaid;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Remaining_Limit extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util =new Utilities(data);
		try {
			if(util.IsValidDBSUResponse())
			{
				double totalCredit=0.0;
				String totalAvailableCredit=String.valueOf(data.getSessionData("S_AVAILABLE_CREDIT_LIMIT"));
				String totalCreditLimit=String.valueOf(data.getSessionData("S_TOTAL_CREDIT_LIMIT"));
				String remainingLimit=String.valueOf(data.getSessionData("S_REMAINING_CREDIT_LIMIT"));
				data.addToLog(elementName, "Total Credit Limit : "+totalCreditLimit +" Total Available Credit Limit :"+totalAvailableCredit);
				if(util.IsNotNUllorEmpty(totalAvailableCredit)&&util.IsNotNUllorEmpty(totalCreditLimit))
				{
					//totalCredit=(Double.parseDouble(totalAvailableCredit)-Double.parseDouble(totalCreditLimit));
					data.setSessionData("S_TOTAL_REMAINING_LIMIT", String.valueOf(totalAvailableCredit));
					exitState ="SU";
				}else if(util.IsNotNUllorEmpty(remainingLimit)&&0.0<Double.parseDouble(remainingLimit)) {
					data.setSessionData("S_TOTAL_REMAINING_LIMIT", String.valueOf(remainingLimit));
					exitState="SU";
				}
			}
		} catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
		}
		return exitState;
	}


}
