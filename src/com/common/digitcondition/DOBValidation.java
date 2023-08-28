package com.common.digitcondition;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class DOBValidation extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String status = "NO";
		Utilities util = new Utilities(data);
		try 
		{
			String digitValue = (String)data.getSessionData("S_EDOB");
			DigitConditionCheck dcc = new DigitConditionCheck();
			if(null!=digitValue&&digitValue.contains("*"))
			{
				return "YES";
			}
			if(dcc.isFutureDate(digitValue,"ddMMyyyy",data)) 
			{
				status ="YES";
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
		return status;
	}
}