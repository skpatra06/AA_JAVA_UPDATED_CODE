package com.common.creditservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CreditServiceUserChoice extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="";
		Utilities util = new Utilities(data);
		List<Map<String ,String>> datalist=null,airtimeList=null;
		List<Map<String ,String>> listServiceEligibility= null;
		Map<String,List<Map<String ,String>>> mapServiceEligibility = null;
		List<Map<String,String>> creditServiceObj=null;
		Map<String,String> moreOption=null;
		List<Map<String,String>> dubCreditServiceList= null;
		List<Map<String,String>> totalCreditList= null;

		try {
			//listServiceEligibility=(List<Map<String, String>>) data.getSessionData("S_LOAN_SERVICE_ELIGIBILITY");
			mapServiceEligibility = (Map<String, List<Map<String, String>>>) data.getSessionData("S_LOAN_SERVICE_TYPE_SPLIT");
			util.addToLog("check for customerServiceEligibilities :"+ mapServiceEligibility,util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(mapServiceEligibility)) 
			{
				if(util.IsNotNUllorEmpty(mapServiceEligibility))
				{
					datalist=mapServiceEligibility.get("GSM");
					airtimeList=mapServiceEligibility.get("AM");
					data.addToLog(elementName, "***** DATA LIST ***** :"+datalist+" \n ***** AIRTIME LIST ****** :"+airtimeList);
					String CreditServicesMenu =""+data.getSessionData("S_CREDIT_SERVICES_MENU");
					data.addToLog(elementName, "***** CREDIT SERVICE MENU ****** :"+CreditServicesMenu);
					if("DATA".equalsIgnoreCase(CreditServicesMenu)) {
						creditServiceObj=airtimeList;
						exitState="DATA";
					}else if("VOICE".equalsIgnoreCase(CreditServicesMenu)) {
						creditServiceObj=datalist;
						exitState="VOICE";
					}else {
						String userChoice=util.getMenuElementValue();
						data.addToLog(elementName, "User Entered choice for Airtel Credit Service :"+userChoice);
						if("1".equals(userChoice)){
							creditServiceObj=datalist;
							data.setSessionData("S_CREDIT_SERVICES_MENU", "DATA");
							exitState="DATA";
						}else if("2".equals(userChoice)){
							creditServiceObj=airtimeList;
							data.setSessionData("S_CREDIT_SERVICES_MENU", "VOICE");
							exitState="VOICE";
						}
					}
					data.addToLog(elementName,"******* CREDIT SERVICE LIST SIZE ******** : "+creditServiceObj.size());
					dubCreditServiceList=new  ArrayList<>();
					totalCreditList= new ArrayList<>();
					dubCreditServiceList.addAll(creditServiceObj);
					for(int i=0;i<creditServiceObj.size();i++)
					{
						if(i==4) {
							moreOption=new HashMap<>();
							moreOption.put("MORE_OPTION","S_CREDIT_SERVICE_MORE_OPTION_DC");
							totalCreditList.add(moreOption);
							break;
						}
						totalCreditList.add(dubCreditServiceList.remove(0));
					}
					util.setAudioItemListForMenu("S_CREDIT_SERVICE_LIST",totalCreditList,"S_CREDIT_SERVICES_DC");
					data.setSessionData("S_LOADED_CREDIT_SERVICE_LIST", totalCreditList);
					data.setSessionData("S_REMAINING_CREDIT_SERVICE_LIST",dubCreditServiceList);
				}
			}

		} catch (Exception e) {
			util.errorLog(elementName,e);
		}finally {
			util=null;
			datalist=null;
			airtimeList=null;
			listServiceEligibility= null;
			mapServiceEligibility = null;
			creditServiceObj=null;
			moreOption=null;
			dubCreditServiceList= null;
			totalCreditList= null;
		}
		return exitState;
	}

}
