package com.common.amservices;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Initiate_Reversal_Params extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		Map<String,String> mapAmReversal=  null;
		List<Map<String, String>> checkTransaction = null;
		try{
			checkTransaction = (List<Map<String, String>>) data.getSessionData("S_AM_REVERSE_TRANSACTION_DETAILS");
			int OptionSelected  =Integer.parseInt((String)data.getElementData("AA_PRP_XX_MN_0038_DM", "value"));
			if(util.IsNotNUllorEmpty(checkTransaction))
			{
				mapAmReversal=checkTransaction.get(OptionSelected-1);
				if(util.IsNotNUllorEmpty(mapAmReversal)) {
					data.setSessionData("S_AMR_TXNID",mapAmReversal.get("txnId"));
				}
				exitState = "SU";
			}else
			{
				data.addToLog(strElementName, "Transactions not fetched "+checkTransaction);
			}
		}
		catch(Exception e){
			util.errorLog(strElementName, e);
		}
		finally{
			checkTransaction = null;
			mapAmReversal = null;
			util=null;
		}
		return exitState ;
	}

}
