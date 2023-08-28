package com.common.recharge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Transaction_Usage extends DecisionElementBase 
{

	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		List<Map<String, String>> lastFiveTransaction = null;
		List<Map<String, String>> availableTransactionUsage=null;
		try {
			String statusCode = String.valueOf(data.getSessionData("S_STATUS_CODE"));
			if ("200".equals(statusCode)) {
				lastFiveTransaction = new ArrayList<>();
				String opcoCode=String.valueOf(data.getSessionData("S_OPCO_CODE"));
				availableTransactionUsage = (List<Map<String, String>>)data.getSessionData("S_TRANSACTION_USAGE");
				if (util.IsNotNUllorEmpty(availableTransactionUsage)) {
					for (int i = 0; i < availableTransactionUsage.size(); i++) {
						if (i > 4)
							break; 
						((Map<String, String>)availableTransactionUsage.get(i)).put("dateTime", util.parseUnixDate(String.valueOf((availableTransactionUsage.get(i)).get("dateTime")), "dd/MM/yyyy HH:mm"));
						if("MG".equalsIgnoreCase(opcoCode)) {
							((Map<String, String>)availableTransactionUsage.get(i)).put("charges",String.valueOf(util.convertToInt(elementName,String.valueOf((availableTransactionUsage.get(i)).get("charges")))));
							((Map<String, String>)availableTransactionUsage.get(i)).put("startBalance",String.valueOf(util.convertToInt(elementName,String.valueOf(((Map)availableTransactionUsage.get(i)).get("startBalance")))));
						}else{
							((Map<String, String>)availableTransactionUsage.get(i)).put("charges", parseAmount(String.valueOf((availableTransactionUsage.get(i)).get("charges"))));
							((Map<String, String>)availableTransactionUsage.get(i)).put("startBalance", parseAmount(String.valueOf(((Map)availableTransactionUsage.get(i)).get("startBalance"))));	
						}
						lastFiveTransaction.add(availableTransactionUsage.get(i));
					}  
					data.setSessionData("S_TRANSACTION_USAGE", lastFiveTransaction);
					data.setSessionData("S_TRANSACTION_COUNT", lastFiveTransaction.size());
					exitState = "SU";
				}else {
					data.addToLog(elementName, "Error fetching Transaction Usage Map Value:" + availableTransactionUsage);
				}
			} 
		} catch (Exception e) {
			util.errorLog(elementName, e);
		} finally {
			availableTransactionUsage=null;
			lastFiveTransaction = null;
			util = null;
		} 
		return exitState;
	}

	private String parseAmount(String amount) {
		if (amount.contains(".") && amount.split("\\.")[1].length() >= 2) {
			amount = amount.substring(0, amount.indexOf(".") + 3);
		} else if (amount.contains(".") && amount.split("\\.")[1].length() == 1) {
			amount = amount.substring(0, amount.indexOf(".") + 2);
		} 
		return amount;
	}
}
