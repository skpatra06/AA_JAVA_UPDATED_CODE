package com.hbb;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class HBB_LinkedAccount_UserSelectedOption extends DecisionElementBase {
	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState="";
		Utilities util=new Utilities(data);
		List<Map<String,String>> linkedHBB_AccountList=null;
		Map<String,String> linkedAcct=null;
		try {
			String userSelectedOption=util.getMenuElementValue();
			data.addToLog(strElementName, "USER SECTED OPTION : "+userSelectedOption);
			if("*".equalsIgnoreCase(userSelectedOption)) {
				data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				exitState = "STAR";
			}else {
				int selectedOption=Integer.parseInt(userSelectedOption);
				linkedHBB_AccountList=(List<Map<String, String>>) data.getSessionData("S_HBB_LINKED_ACCOUNTS"); 
				linkedAcct=linkedHBB_AccountList.get(selectedOption-1);
				data.setSessionData("S_HBB_NUMBER", linkedAcct.get("linkedAccountMsisdn"));
				exitState=String.valueOf(selectedOption);
			}
		}
		catch (Exception e) {
			util.errorLog(strElementName, e);
		}finally{
			linkedAcct=null;
			linkedHBB_AccountList=null;
			util=null;
		}
		return exitState;
	}
}
