package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ShowroomCallerCheck extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		String returnState = "NON_SHOWROOM";
		Utilities util = new Utilities(data);
		try {
			String strConnectionType = (String) data.getSessionData("S_CONN_TYPE");
			String strCustSegment = (String) data.getSessionData("S_CUSTOMER_SEGMENT");
			if(util.IsNotNUllorEmpty(strConnectionType)) {
				if(("PRE".equalsIgnoreCase(strConnectionType)|| "PREPAID".equalsIgnoreCase(strConnectionType))&& "SHOWROOM".equalsIgnoreCase(strCustSegment)) {
					returnState ="PRE_SHOWROOM";
				}else if(("POS".equalsIgnoreCase(strConnectionType)|| "POSTPAID".equalsIgnoreCase(strConnectionType))&& "SHOWROOM".equalsIgnoreCase(strCustSegment)) {
					returnState ="POS_SHOWROOM";
				}	
			}else{
			  	data.addToLog(strElementName, "Null Value Occurs In Connection Type");
			}
		}catch(Exception e) {
			util.errorLog(strElementName,e);
		}finally{
			util=null;
		}
		return returnState;
	}
}
