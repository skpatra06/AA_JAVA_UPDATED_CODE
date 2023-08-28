package com.common.me2u;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

import com.util.Utilities;

public class PUK_SIM_ME2_DOB_Validation extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		List<Map<String,String>> listKycDetails= null;
		try {
			if(util.IsValidRestAPIResponse()) 
			{
				String strHasError = String.valueOf(data.getSessionData("S_KYC_HAS_ERROR"));
				if("true".equalsIgnoreCase(strHasError))
				{
					data.addToLog(elementName, "Data not found in KYC API");
					String strPrev = (String) data.getSessionData("S_PREVIOUS_PAGE");
					if("PUK_MENU".equalsIgnoreCase(strPrev))
					{
						exitState = "NR_PUK";
					}
				}
				else
				{
					String pukDOB = (String)data.getSessionData("S_KYC_DOB");
					String EDOB = (String)data.getSessionData("S_EDOB");
					String pukRegisStatus = (String)data.getSessionData("S_KYC_REGISTRATION_STATUS");
					listKycDetails=(List<Map<String, String>>) data.getSessionData("S_KYC_REG_LIST");
					if(util.IsNotNUllorEmpty(listKycDetails)){
						for(Map<String,String> map:listKycDetails){
							if(map.containsKey("product")){
								if(null!=map.get("product")&&"GSM".equalsIgnoreCase(map.get("product"))){
									pukRegisStatus=map.get("status");
								}

							}
						}
					}
					data.addToLog(elementName,"REGISTRATION STATUS : "+pukRegisStatus +" KYC LIST MAP : "+listKycDetails);
					if("APPROVED".equalsIgnoreCase(pukRegisStatus) || "PENDING".equalsIgnoreCase(pukRegisStatus)||"ACTIVE".equalsIgnoreCase(pukRegisStatus))
					{
						String convertedDOB = util.dateConversion(pukDOB, "yyyy-MM-dd", "ddMMyyyy");
						data.addToLog(elementName,"Entered DOB :"+ EDOB+" ,KYC DOB :"+convertedDOB +", host DOB "+pukDOB);
						if(convertedDOB.equals(EDOB))
						{
							exitState="MATCHING";	
						}
						else
						{
							exitState="NOT_MATCHING";
						}
					}
					else 
					{
						exitState="NOT_MATCHING";
					}
				}
			}

		} 
		catch (Exception e) 
		{
			util.errorLog(elementName, e);
		}
		finally 
		{
			util=null;	
			listKycDetails= null;
		}
		return exitState;
	}

}
