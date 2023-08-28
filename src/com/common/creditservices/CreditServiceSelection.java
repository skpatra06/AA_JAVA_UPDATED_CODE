package com.common.creditservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CreditServiceSelection extends DecisionElementBase{

	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="";
		Utilities util = new Utilities(data);
		List<Map<String, String>> availCreditList =null;
		Map<String,String> mapAvailable= null;
		List<Map<String, String>> remainingCreditServiceList= null;
		List<Map<String, String>> dupCreditList= null;
		List<Map<String, String>> totalCreditList= null;
		Map<String,String> moreOption=null;
		try {
			availCreditList=(List<Map<String, String>>) data.getSessionData("S_LOADED_CREDIT_SERVICE_LIST");
			String previousMenuElement=util.getMenuElementValue();
			int creditServiceSelectedOpt =Integer.parseInt(previousMenuElement);
			data.addToLog("--- CREDIT SERVICE SELECTED OPTION : ",""+creditServiceSelectedOpt);
			if(util.IsNotNUllorEmpty(availCreditList))
			{
				if(creditServiceSelectedOpt<=4) 
				{
					mapAvailable=availCreditList.get(creditServiceSelectedOpt-1);
					if(util.IsNotNUllorEmpty(mapAvailable))
					{
						data.setSessionData("S_SELECTED_SUB_TYPE",mapAvailable.get("subType"));
						data.setSessionData("S_SELECTED_LOAN_TYPE",mapAvailable.get("loanType"));
						data.setSessionData("S_LSE_AMOUNT",Double.parseDouble(mapAvailable.get("amount")));
						data.setSessionData("S_CURRENCY_CODE", mapAvailable.get("currency"));
						data.setSessionData("S_LSE_PRODUCT_ID", mapAvailable.get("productId"));
						data.setSessionData("S_LSE_AVA_RECOVERY", mapAvailable.get("availableRecovery"));
						data.setSessionData("S_REPEAT_COUNTER","0");
					}else
					{
						data.addToLog(elementName, "Error fetching the selected credit option map. Value: "+mapAvailable);
					}
					exitState =""+creditServiceSelectedOpt;
				}else if(5==creditServiceSelectedOpt){
					remainingCreditServiceList=(List<Map<String, String>>) data.getSessionData("S_REMAINING_CREDIT_SERVICE_LIST");
					dupCreditList=new ArrayList<>();
					totalCreditList=new ArrayList<>();
					if(util.IsNotNUllorEmpty(remainingCreditServiceList))
					{
						dupCreditList.addAll(remainingCreditServiceList);
						data.addToLog(elementName, " Remaining Credit Service List Size : "+remainingCreditServiceList.size());
						for(int i=0;i<remainingCreditServiceList.size();i++)
						{
							if(i==4) 
							{
								moreOption=new HashMap<>();;
								moreOption.put("MORE_OPTION", "S_CREDIT_SERVICE_MORE_OPTION_DC");
								totalCreditList.add(moreOption);
								break;
							}
							totalCreditList.add(dupCreditList.remove(0));
						}
						util.setAudioItemListForMenu("S_CREDIT_SERVICE_LIST",totalCreditList, "S_CREDIT_SERVICES_DC");
						data.setSessionData("S_LOADED_CREDIT_SERVICE_LIST", totalCreditList);
						data.setSessionData("S_REMAINING_CREDIT_SERVICE_LIST",dupCreditList);
					}
					else
					{
						data.addToLog(elementName, "Error fetching remaining Credits list. Value:"+remainingCreditServiceList);
					}
					exitState="5";
				}else {
					data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
					exitState="6";	
				}
			}
			util.addPreviousMenu(data);
		}catch(Exception e) {
			util.errorLog(elementName, e);
		}finally {
			availCreditList=null;
			mapAvailable= null;
			remainingCreditServiceList= null;
			dupCreditList= null;
			totalCreditList= null;
			util=null;
			moreOption=null;
		}
		return exitState;
	}

}
