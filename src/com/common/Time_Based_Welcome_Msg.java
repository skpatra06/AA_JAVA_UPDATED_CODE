package com.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.audium.server.session.ActionElementData;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.audium.server.voiceElement.DecisionElementBase;
import com.common.transfer.BusinessHourCheck;
import com.util.Utilities;

public class Time_Based_Welcome_Msg extends ActionElementBase {

	@Override
	public void doAction(String elementName, ActionElementData data) throws Exception {
		Utilities util = new Utilities(data);
		Map<String,String> welcomeMsgMap = null;
		String dnisType = "";
		String welPrompt="";
		try {

			dnisType = (String) data.getSessionData("S_DNIS_TYPE");
			if("AIRTEL_MONEY".equalsIgnoreCase(dnisType)) {
				welcomeMsgMap = (Map<String,String>) data.getApplicationAPI().getApplicationData("TIME_BASED_WELCOME_MSG_AM");
			}else{
				welcomeMsgMap = (Map<String,String>) data.getApplicationAPI().getApplicationData("TIME_BASED_WELCOME_MSG");
			}
			if(util.IsNotNUllorEmpty(welcomeMsgMap))
			{
				data.addToLog(elementName,"Caller Type : " +dnisType+" : Time Based welcome map: "+welcomeMsgMap);
				for(String key:welcomeMsgMap.keySet()) 
				{
					if(key!=null&&key.contains("-")) 
					{
						String timeKey[] = key.split("-");
						if(isTimeWith_in_Interval(timeKey[0], timeKey[1],util))
						{
							welPrompt=welcomeMsgMap.get(key);
							data.addToLog(elementName, "The time range between : " +key);
							break;
						}
					}
				}
				if(!util.IsNotNUllorEmpty(welPrompt))
				{
					welPrompt=welcomeMsgMap.get("DEFAULT");
					data.addToLog(elementName, "Setting the Default Welcome audio:"+welPrompt);
				}
				data.setSessionData("S_TIME_BASED_WELCOME", welPrompt);
			}
			else
			{
				data.addToLog(elementName, "Null Value Occured In  Map Config Data : TIME_BASED_WELCOME_MSG");
			}

		}
		catch(Exception e) 
		{
			data.addToLog(elementName, e.toString());
		}
		finally
		{
			util = null;
			welcomeMsgMap= null;
		}
	}
	public boolean isTimeWith_in_Interval( String startTime, String endTime, Utilities util) {
		boolean isBetween = false;
		try {
			SimpleDateFormat objsdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			String currDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
			Date objStartDate = objsdf.parse(currDate +" "+startTime);
			Date objEndDate = objsdf.parse(currDate +" "+endTime);
			Date objCurrDate = Calendar.getInstance().getTime();
			util.addToLog("start time is : "+objStartDate  );
			util.addToLog("end time is : "+objEndDate  );
			util.addToLog("current time is : "+objCurrDate  );
			if (objCurrDate.after(objStartDate) && objCurrDate.before(objEndDate)) 
			{
				isBetween = true;
			}else{
				isBetween=false;
			}
		} catch (ParseException e) {
			util.errorLog("Time Based welcome message", e);
		}
		return isBetween;
	}
}