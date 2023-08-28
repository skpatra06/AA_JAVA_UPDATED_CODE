package com.common.creditservices;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class SetCreditServicesOptions extends DecisionElementBase{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		List<Map<String ,String>> listCreditServices=null;
		Map<String,String> loanServiceApplyDetails =null;

		try {
			String CreditServicesMenu = (String )data.getSessionData("S_CREDIT_SERVICES_MENU");
			if("DATA".equalsIgnoreCase(CreditServicesMenu)) {
				listCreditServices=(List<Map<String ,String>>) data.getSessionData("S_CREDIT_SERVICES_DATA_LIST");
				data.setSessionData("S_ME2U_TRANSACTION_MODE", "data");
				data.setSessionData("S_ME2U_TRANSACTION_TYPE", "Me2U_data");

			}else if("VOICE".equalsIgnoreCase(CreditServicesMenu)) {
				listCreditServices=(List<Map<String ,String>>) data.getSessionData("S_CREDIT_SERVICES_VOICE_LIST");
				data.setSessionData("S_ME2U_TRANSACTION_MODE", "airtime");
				data.setSessionData("S_ME2U_TRANSACTION_TYPE", "Me2U");
			}

			String creditServiceMenuSelection = (String )data.getElementData("AA_PRP_XX_MN_0023_DM","value");
			int intCreditServiceMenuSelection = Integer.parseInt(creditServiceMenuSelection);
			if(util.IsNotNUllorEmpty(listCreditServices))
			{
				loanServiceApplyDetails = listCreditServices.get(intCreditServiceMenuSelection-1);
				if(util.IsNotNUllorEmpty(loanServiceApplyDetails)) 
				{
					data.setSessionData("S_SELECTED_SUB_TYPE",loanServiceApplyDetails.get("subType"));
					data.setSessionData("S_SELECTED_LOAN_TYPE",loanServiceApplyDetails.get("loanType"));
					data.setSessionData("S_LSE_AMOUNT",loanServiceApplyDetails.get("eligibleAmount"));
					data.setSessionData("S_SELECTED_LOAN_TYPE",loanServiceApplyDetails.get("loanType"));
					data.setSessionData("S_CURRENCY_CODE", loanServiceApplyDetails.get("currency"));
					exitState="SU";
				}
			}
		}catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			util=null;
			listCreditServices=null;
			loanServiceApplyDetails =null;
		}
		return exitState;

	}
}
