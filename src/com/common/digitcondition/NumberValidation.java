package com.common.digitcondition;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class NumberValidation extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception 
	{
		String exitState = "NO";
		Utilities util = new Utilities(data);
		try
		{
			String elementName=data.getElementHistory().get(data.getElementHistory().size()-3);
			if(null!=elementName&&elementName.contains(".")) {
				elementName=elementName.split("\\.")[1];
			}
			String digitElementValue = data.getElementData(elementName, "value");
			data.addToLog(strElementName, "Value entered in digit: "+digitElementValue);
			DigitConditionCheck dcc = new DigitConditionCheck();
			if(null!=digitElementValue&&digitElementValue.contains("*"))
			{
				return "YES";
			}
			if(dcc.checkForAllSameNumbers(digitElementValue)) 
			{
				exitState ="YES";
			}
		} 
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally 
		{
			util=null;
		}
		return exitState;
	}
}