package com.prepaid.niger;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class GSM_BalActiveLangCheck extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {

			String exitState="BalAud";
			Utilities util = new Utilities(data);
			List<Map<String,String>> accountDetails =null;
			
			try {
				String balance=null;
				String expireDate=null;
				String activeLang = String.valueOf(data.getSessionData("S_ACTIVE_LANG"));
				data.addToLog(elementName, "Active Language :"+activeLang);
				String balSMSLang = String.valueOf(data.getSessionData("S_BAL_SMS_LANG"));
				data.addToLog(elementName, "Languages for which balance SMS should be send "+balSMSLang);
				
				accountDetails = (List<Map<String, String>>) data.getSessionData("S_BALANCE_DETAILS");
				util.addToLog(elementName+" Account Deatials as:",util.DEBUGLEVEL);
				
				if(util.IsNotNUllorEmpty(accountDetails)) {
					
					if(balSMSLang.contains(activeLang)) {
						
						for(Map<String, String> accountDetailsMap : accountDetails) {
							
							if ("AIRTIME".equalsIgnoreCase(String.valueOf(accountDetailsMap.get("balanceDescription")))) 
							{
								balance =String.valueOf(accountDetailsMap.get("balance"));
								data.setSessionData("S_AIRTIME_GMS_BALANCE", util.parseAmount(balance));
								data.addToLog(elementName,"Balace is :"+balance);
								
								expireDate =String.valueOf(accountDetailsMap.get("expireTime"));
								String dateConverted = util.parseUnixDate(expireDate, "dd/MM/yyyy HH:mm");
								data.setSessionData("S_AMIRTIME_GSM_DATE",dateConverted);
								data.addToLog(elementName,"Converted Date  is :"+dateConverted);
								
								break;
							}
							
						}
						exitState="BalSMS"; 
					}
					
					
				}
				
				
			} catch (Exception e) {
				util.errorLog(elementName, e);
			}
			finally {
				util=null;
			}
		return exitState;
	}

}
