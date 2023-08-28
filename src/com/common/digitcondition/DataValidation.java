package com.common.digitcondition;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class DataValidation extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "YES";
		Utilities util = new Utilities(data);
		try {
			String digitElementValue = data.getElementData(data.getElementHistory().get(data.getElementHistory().size()-3), "value");
			data.addToLog(strElementName, "Value entered in digit: "+digitElementValue);
			if(null!=digitElementValue&&digitElementValue.contains("*"))
			{
				return "YES";
			}
			if(util.convertToInt("S_ME2U_MIN_DATA")<=Integer.parseInt(digitElementValue)&&Integer.parseInt(digitElementValue)<=util.convertToInt("S_ME2U_MAX_DATA")) {
				exitState ="NO";
			}
		} catch (Exception e) {
			util.errorLog(strElementName, e);
		}finally {
			util=null;
		}

		return exitState;
	}

}
