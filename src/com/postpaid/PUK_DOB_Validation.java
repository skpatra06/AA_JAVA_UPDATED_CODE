package com.postpaid;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class PUK_DOB_Validation extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				String pukDOB = (String)data.getSessionData("S_KYC_DOB");
				String DOB = (String)data.getSessionData("S_DOB");
				String pukRegisStatus = (String)data.getSessionData("S_KYC_REGISTRATION_STATUS");
				if("APPROVED".equalsIgnoreCase(pukRegisStatus)){
					String conpukDOB = util.dateConversion(pukDOB, "yyyy-MM-dd", "DDMMYYYY");
					if(conpukDOB.equals(DOB))
						exitState="MATCHING";	
					else
						exitState="NOT_MATCHING";
				}else {
					exitState="NOT_MATCHING";
				}
				
			}
			
		} catch (Exception e) {
			util.errorLog(elementName, e);
		}
		return exitState;
	}

}
