package com.common;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;


public class Get_KYC_Status extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		List<Map<String,String>> listKycDetails= null;
		try{
			if(util.IsValidRestAPIResponse()) {
				String registrationStatus =(String)data.getSessionData("S_KYC_REGISTRATION_STATUS" );
				listKycDetails=(List<Map<String, String>>) data.getSessionData("S_KYC_REG_LIST");
				if(util.IsNotNUllorEmpty(listKycDetails)){
					for(Map<String,String> map:listKycDetails){
						if(map.containsKey("product")){
							if(null!=map.get("product")&&"GSM".equalsIgnoreCase(map.get("product"))){
							   registrationStatus=map.get("status");
							}
							
						}
					}
				}
				util.addToLog(elementName+ " Registration Status fetched: "+registrationStatus+" Kyc List : "+listKycDetails,util.DEBUGLEVEL);
				if("APPROVED".equalsIgnoreCase(registrationStatus)||"PENDING".equalsIgnoreCase(registrationStatus) || "ACTIVE".equalsIgnoreCase(registrationStatus)) {
					exitState = "RMN";	
				}else {
					exitState = "NRMN";
				}
			}
		}catch (Exception e) {
			util.errorLog(elementName, e);
			
		}finally{
			listKycDetails= null;
			util=null;
		}
		return exitState;
	}

}
