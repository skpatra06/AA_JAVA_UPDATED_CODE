package com.common.recharge;

import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Subscriber_Transaction_Recharge_History extends DecisionElementBase
{
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities((APIBase)data);
		List<Map<String, Object>> listTrans = null;
		List<Map<String, Object>> availableTransactionHistory = null;
		Map<String,String> transMap=null;
		try {
			String statusCode = String.valueOf(data.getSessionData("S_STATUS_CODE"));
			if ("200".equals(statusCode)) {
				int count = 0;
				String opcoCode=String.valueOf(data.getSessionData("S_OPCO_CODE"));
				listTrans = new ArrayList<>();
				availableTransactionHistory = (List<Map<String, Object>>)data.getSessionData("S_TRANSACTION_HISTORY");
				if (util.IsNotNUllorEmpty(availableTransactionHistory)) {
					for (Map<String, Object> transactionDetail : availableTransactionHistory) {
							String bundleName=null;												
							if(("MG".equalsIgnoreCase(opcoCode)||"RW".equalsIgnoreCase(opcoCode))&&transactionDetail.containsKey("typeDescription")&&util.IsNotNUllorEmpty(String.valueOf(transactionDetail.get("typeDescription")))) {
								bundleName=String.valueOf(transactionDetail.get("typeDescription"));
							}
							else if(transactionDetail.containsKey("bundleName")&&util.IsNotNUllorEmpty(String.valueOf(transactionDetail.get("bundleName")))) {
								bundleName=String.valueOf(transactionDetail.get("bundleName"));
							}else if(transactionDetail.containsKey("type")&&util.IsNotNUllorEmpty(String.valueOf(transactionDetail.get("type")))){
								bundleName=String.valueOf(transactionDetail.get("type"));
							}
							transMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("LAST_TRANS_HISTORY");
							if(util.IsNotNUllorEmpty(transMap)&&util.IsNotNUllorEmpty(bundleName)&&transMap.containsKey(bundleName)) {
								bundleName=transMap.get(bundleName);
							}
							if(!util.IsNotNUllorEmpty(bundleName)&&"MG".equalsIgnoreCase(opcoCode)){
								bundleName="RECHARGE";
							}else if(!util.IsNotNUllorEmpty(bundleName)){
								bundleName="AIRTIME";
							}
							transactionDetail.put("bundleName",bundleName);
							if("MG".equalsIgnoreCase(opcoCode)) {
								transactionDetail.put("transactionAmount",String.valueOf(util.convertToInt(strElementName,String.valueOf(transactionDetail.get("transactionAmount")))));
							}else {
								transactionDetail.put("transactionAmount", parseAmount(String.valueOf(transactionDetail.get("transactionAmount"))));	
							}
							String dateConverted = util.parseUnixDate((String)transactionDetail.get("dateTime"), "dd/MM/yyyy HH:mm");
							data.addToLog(strElementName, "Converted date:" + dateConverted);
							transactionDetail.put("dateTime", dateConverted);
							listTrans.add(transactionDetail);
							count++;
							if (count > 4)
								break; 
						} 
					data.setSessionData("S_TRANSACTION_COUNT", listTrans.size());
					data.setSessionData("S_TRANSACTION_HISTORY", listTrans);
					exitState = "SU";
				} else {
					data.addToLog(strElementName, "Error fetching Transaction History Map. Value:" + availableTransactionHistory);
				} 
			} 
			
		} catch (Exception e) {
			util.errorLog(strElementName, e);
		} finally {
			util = null;
			listTrans = null;
			availableTransactionHistory = null;
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
