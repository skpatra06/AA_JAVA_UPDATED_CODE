package com.common.faf;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Product_GetFAFList extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "2";
		Utilities util = new Utilities(data);
		List<Map<String, String>> fafInformation=null;
		Map<String,String> fafInfo = null;
		try 
		{
			String selectedOpt=util.getMenuElementValue();
			data.addToLog(elementName, "Selected Option is : "+selectedOpt);
			if("1".equalsIgnoreCase(selectedOpt)) {
				String entered_faf_number=String.valueOf(data.getSessionData("S_FAF_NUMBER"));
				String fafallowed =""+data.getSessionData("S_FAF_MAXALLOWED_NUMREACHED_FLAG");
				fafInformation=(List<Map<String, String>>) data.getSessionData("S_FAF_INFORMATION");
				if("false".equalsIgnoreCase(fafallowed)) 
				{
					Date date = new Date();//current date in unix formate
					long unixCurrentTime = date.getTime();
					data.setSessionData("S_FAF_START_DATE",unixCurrentTime);
					String countOfExpirydays =String.valueOf(data.getSessionData("S_EXPIRY_DAYS_COUNT"));
					data.addToLog(elementName," EXPIRY DAY COUNT : "+countOfExpirydays);
					if(!util.IsNotNUllorEmpty(countOfExpirydays)) 
					{
						countOfExpirydays="30";// Default Value 
					}

					int count=Integer.parseInt(countOfExpirydays);  
					long unixEndTime = (date.getTime() + TimeUnit.DAYS.toMillis(count));//add 30 days in current date
					data.setSessionData("S_FAF_EXP_DATE",unixEndTime);
					data.addToLog(elementName, "Expiry Dtae is :"+ String.valueOf(unixEndTime));
					if(util.IsNotNUllorEmpty(fafInformation)) {
						fafInfo = fafInformation.get(0);
						data.setSessionData("S_FAF_GROUP", fafInfo.get("fafGroup"));
						data.setSessionData("S_FAF_OWNER",fafInfo.get("owner"));
						data.setSessionData("S_FAF_ACTION", "ADD");
					}else {
						data.setSessionData("S_FAF_GROUP", "56");
						data.setSessionData("S_FAF_OWNER","Subscriber");
						data.setSessionData("S_FAF_ACTION", "ADD");	
					}
					data.setSessionData("S_FAF_NUMBER",entered_faf_number);
					exitState="1";
				}
			}
		} 
		catch (Exception e)
		{
			util.errorLog(elementName,e);
		}
		finally
		{
			util=null;
			fafInformation=null;
			fafInfo = null;
		}
		return exitState;
	}
}
