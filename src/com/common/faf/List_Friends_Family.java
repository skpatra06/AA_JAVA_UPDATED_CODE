package com.common.faf;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class List_Friends_Family extends DecisionElementBase{
	String FAF_NUMBER="fafNumber";
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "Failure";
		List<Map<String,String>> listFafBundle = null;
		Utilities util = new Utilities(data);
		try {
			listFafBundle = (List<Map<String, String>>) data.getSessionData("S_FAF_INFORMATION");
			if(util.IsNotNUllorEmpty(listFafBundle)) {
				util.setAudioItemListForAudio("S_FAF_NUMBER_BUNDLE_DETAILS", listFafBundle, "S_LIST_FAF_DETAILS_DC");	
				exitState = "SU_AVL";
			}else {
				exitState = "SU_NULL";
			}
		}catch(Exception e){
			util.errorLog(strElementName, e);
		}finally {
			listFafBundle = null;
			util = null;
		}
		return exitState;
	}

}
