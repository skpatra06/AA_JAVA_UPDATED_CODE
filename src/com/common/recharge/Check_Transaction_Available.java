package com.common.recharge;


import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_Transaction_Available extends DecisionElementBase {
	public String doDecision(String ElementName, DecisionElementData data)	throws Exception 
	{
		String exitState = "N";
		Utilities util = new Utilities(data); 
		try
		{
			String transactionCount=String.valueOf(data.getSessionData("S_TRANSACTION_COUNT")); 
			if(!util.IsNotNUllorEmpty(transactionCount)){
				transactionCount="0";
			}
			int getTransactionCount = Integer.parseInt(transactionCount);
			if(getTransactionCount>=1)
			{
				exitState="Y";
				data.setSessionData("S_MN_LAST_FOUR_DIGIT", util.GetLastDigits(""+data.getAni()));
			}
			data.addToLog(ElementName,"Total Transaction : "+transactionCount);
			String strStatusMsg = String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
			if("5327005".equalsIgnoreCase(strStatusMsg))
			{
				data.addToLog(ElementName, "No transactions. Status: "+strStatusMsg);
				exitState="N";
			}
		}catch (Exception e) {
			util.errorLog(ElementName, e);
			
		}finally{
			util = null;
		}
		return exitState;
	}

}
