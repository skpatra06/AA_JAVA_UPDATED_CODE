package com.common.creditservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Loan_Service_Eligibility extends DecisionElementBase{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		List<Map<String ,String>> listServiceEligibility = null;
		Map<String,List<Map<String ,String>>> mapServiceEligibility = null;

		try {
			if(util.IsValidRestAPIResponse()){

				listServiceEligibility=(List<Map<String, String>>) data.getSessionData("S_LOAN_SERVICE_ELIGIBILITY");
				util.addToLog("check for customerServiceEligibilities :"+ listServiceEligibility,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(listServiceEligibility)) {
					mapServiceEligibility = splitJsonData(util,data,listServiceEligibility,"split.loanType[AM,GSM]","LOAN_SERVICE_CODE");
					data.addToLog(elementName, "Customer Eligibile Data :" +mapServiceEligibility);
					if(util.IsNotNUllorEmpty(mapServiceEligibility))
					{
						if(mapServiceEligibility.size()==2&&util.IsNotNUllorEmpty(mapServiceEligibility.get("AM"))&&util.IsNotNUllorEmpty(mapServiceEligibility.get("GSM"))) 
						{
							exitState = "BOTH";
						}
						else
						{
							if(mapServiceEligibility.containsKey("GSM")&&util.IsNotNUllorEmpty(mapServiceEligibility.get("GSM")))
							{
								exitState = "VOICE";
							}
							else if(mapServiceEligibility.containsKey("AM")&&util.IsNotNUllorEmpty(mapServiceEligibility.get("AM")))
							{
								exitState = "DATA";
							}
							else
							{
								exitState="NOT_ELIGIBLE";
							}
						}
						data.setSessionData("S_LOAN_SERVICE_TYPE_SPLIT", mapServiceEligibility);
					}
				}else {
					exitState="NOT_ELIGIBLE";
				}
			}else {
				exitState="NOT_ELIGIBLE";
			}
		}catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
			listServiceEligibility = null;
			mapServiceEligibility = null;
		}
		return exitState;

	}


	public Map<String,List<Map<String,String>>> splitJsonData(Utilities util,APIBase cData,List<Map<String,String>> jsonData,String filterList,String configFileName){
		Map<String,List<Map<String,String>>> filteredData=null;
		Map<String, Object> productCodeDetails= (HashMap<String, Object>) cData.getApplicationAPI().getApplicationData(configFileName);
		List<String> productCode=null;
		try {
			cData.addToLog(" Product Code Deatils  : ",""+productCodeDetails);
			if(null!=productCodeDetails) {
				if(filterList.contains("split")){
					filteredData=new HashMap<>();
					String split[]=filterList.split("\\.")[1].split("\\[|\\]");
					String key=split[0];
					String productTypeList[]=split[1].split(",");
					List<Map<String,String>> dataBundle=null;
					for(String productType:productTypeList){
						productType=productType.trim();
						dataBundle=new ArrayList<>();
						if(productCodeDetails.get(productType) instanceof List) {
							productCode=(List<String>) productCodeDetails.get(productType);
						}else{
							productCode=new ArrayList<String>();
							productCode.add(String.valueOf(productCodeDetails.get(productType)));
						}
						cData.addToLog(productType," : "+productCode);
						for(String product:productCode){
							for(Map<String,String> responseBundle:jsonData){
								if(responseBundle.containsKey(key)){
									if(null!=responseBundle.get(key)&&product.trim().equalsIgnoreCase(String.valueOf(responseBundle.get(key)))){
										dataBundle.add(responseBundle);
									}
								}
							}
						}
						cData.addToLog(productType,"Total Matched Product Count : "+dataBundle.size());
						filteredData.put(productType, dataBundle);
						dataBundle=null;
					}
				}
			}else {
				cData.addToLog("","Null Values Occurs In Product Code Details");
			}
		}catch (Exception e){
			util.errorLog("Exception", e);
		}
		return filteredData;
	}
}
