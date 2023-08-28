package com.common.me2u;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ME2UAmountValidation extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "YES";
		Utilities util = new Utilities(data);
		try 
		{
			String digitElementValue = data.getElementData(data.getElementHistory().get(data.getElementHistory().size()-3), "value");
			data.setSessionData("S_AIRTIME_LBAL_FLAG","N");
			data.addToLog(strElementName, "Value entered in digit: "+digitElementValue);
			String strAirtimeBalance = (String) data.getSessionData("S_AVAILABLE_BALANCE");
			data.addToLog(strElementName, "Airtime Balance: "+strAirtimeBalance);
			
			double amBal=util.convertToDouble(strElementName, strAirtimeBalance);
			if(null!=digitElementValue&&digitElementValue.contains("*"))
			{
				return "YES";
			}
			if("KE".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE")))&& null!=digitElementValue&&(int)amBal<Integer.parseInt(digitElementValue))
			{
				int maxTries=0;
				try {
					String triesCount=(String) data.getSessionData("S_TRIES_COUNT");
					maxTries=Integer.parseInt(triesCount);
				} catch (Exception e) {
					maxTries=3;
				}
				
				int count=(Integer)data.getSessionData("S_COUNTER_DIGIT");
				count++;
				if(count<maxTries) {
					data.setSessionData("S_AIRTIME_LBAL_FLAG","Y");
					data.setSessionData("S_COUNTER_DIGIT", count);
				}
			}
			
			if(util.convertToInt("S_ME2U_MIN_AMOUNT")<=Integer.parseInt(digitElementValue)&&Integer.parseInt(digitElementValue)<=util.convertToInt("S_ME2U_MAX_AMOUNT")&&Integer.parseInt(digitElementValue)<=(int)amBal) 
			{
				exitState ="NO";
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
