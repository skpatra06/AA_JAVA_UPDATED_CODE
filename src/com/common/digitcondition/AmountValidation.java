package com.common.digitcondition;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AmountValidation extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "YES";
		Utilities util = new Utilities(data);
		try 
		{
			String elementName=data.getElementHistory().get(data.getElementHistory().size()-3);
			if(null!=elementName&&elementName.contains(".")) {
				elementName=elementName.split("\\.")[1];
			}
			String digitElementValue = data.getElementData(elementName, "value");
			data.addToLog(strElementName, "Value entered in digit: "+digitElementValue);
			if(null!=digitElementValue&&digitElementValue.contains("*"))
			{
				return "YES";
			}
			if(util.convertToInt("S_MIN_AMOUNT")<=Integer.parseInt(digitElementValue)&&Integer.parseInt(digitElementValue)<=util.convertToInt("S_MAX_AMOUNT")) 
			{
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
