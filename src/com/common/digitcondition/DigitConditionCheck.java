package com.common.digitcondition;

import com.audium.server.session.APIBase;
import com.util.Utilities;

public class DigitConditionCheck {

	
	
	public boolean checkForAllSameNumbers(String digitValue) {
		boolean status = false;
		if (!(digitValue.matches("([0-9]){"+digitValue.length()+"}"))|| digitValue.matches("([0-9])\\1{"+(digitValue.length()-1)+"}"))
		{
			status =true;
		}
		return status;
	}
	
	public boolean isFutureDate(String digitValue, String format,APIBase data) {
		boolean status = false;
		Utilities util = new Utilities(data);
		try
		{
			if(util.validDate(digitValue, format)) 
			{
				long diff=util.dateDiffInDays(digitValue, format);
				data.addToLog("", "date Difference in Days :"+diff);
				if(diff>0) 
				{
					status =true;
				}
			}
			else
			{
				status =true;
			}
		} catch (Exception e) {
			util.errorLog("Exception :", e);
		}
		return status;
	}
	
	

}
