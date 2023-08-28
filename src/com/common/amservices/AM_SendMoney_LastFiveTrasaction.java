package com.common.amservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_SendMoney_LastFiveTrasaction extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		List<Map<String, Object>> transactionDetails = null;
		List<Map<String,Object>> listTransactionDetails = null;
		try {
			if(util.IsValidRestAPIResponse()) 
			{
				transactionDetails = (List<Map<String, Object>>) data.getSessionData("S_TRANSACTION_DETAILS_BUNDLE");
				if(util.IsNotNUllorEmpty(transactionDetails))
				{
					data.setSessionData("S_TRANSACTION_COUNT",transactionDetails.size());
					if(transactionDetails.size()>4) 
					{
						listTransactionDetails=new ArrayList<Map<String,Object>>();
						for(int i=0;i<5;i++)
						{
							listTransactionDetails.add(transactionDetails.get(i));
						}
						data.setSessionData("S_TRANSACTION_DETAILS_BUNDLE",listTransactionDetails);
					}
					exitState="YES";
					data.setSessionData("S_MN_LAST_FOUR_DIGIT", util.GetLastDigits(""+data.getAni()));
				}else {
					exitState="NO";
				}
			}
		} catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
			transactionDetails = null;
			listTransactionDetails = null;
			
		}
		return exitState;
	}
}
