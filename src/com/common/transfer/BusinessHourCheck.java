package com.common.transfer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BusinessHourCheck extends DecisionElementBase{

	@SuppressWarnings("unchecked")
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {

		String exitState="NO";
		Utilities util = new Utilities(data);
		Map<String ,Map<String, String>> businessHourMap= null;
		Map<String, String> objStartEndMap= null;
		Map<String,String> mapLimit= null;
		try {
			data.addToLog(elementName,"DNIS type is :"+data.getSessionData("S_DNIS_TYPE"));
			if("TCHL".equalsIgnoreCase(String.valueOf(data.getSessionData("S_DNIS_TYPE")))) 
			{
				data.addToLog(elementName,"TCHL business hours ");
				businessHourMap=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("TCHL_BusinessHour");
			}
			else
			{
				data.addToLog(elementName,"Global business hours ");
				businessHourMap=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("BusinessHour");
			}
			data.addToLog(elementName, "BusinessHours map is :" +businessHourMap);
			Date today = Calendar.getInstance().getTime();
			SimpleDateFormat simpleDateFormatDay= new SimpleDateFormat("EEEE");
			String currentDay=simpleDateFormatDay.format(today);

			SimpleDateFormat simpleDateFormatTime=new SimpleDateFormat("hh:mm");
			String currentTime=simpleDateFormatTime.format(today);
			data.addToLog(elementName, "current Time  is :" +currentTime);

			if(util.IsNotNUllorEmpty(businessHourMap)){
				objStartEndMap=businessHourMap.get(currentDay);
				String starttime= objStartEndMap.get("StartTime");
				String endTime=objStartEndMap.get("CloseTime");
				data.addToLog(elementName, "start time is :" +starttime +" end timr is :"+endTime);

				data.setSessionData("S_START_TIME", starttime);
				data.setSessionData("S_END_TIME", endTime);
				data.addToLog(elementName, "data set successfully  "+"start time is :"+starttime +"end time is :"+endTime);

				if(isTimeWith_in_Interval(data, starttime, endTime, util)){
					exitState="YES";
					mapLimit=(Map<String, String>) data.getApplicationAPI().getApplicationData("TRANSFER_BARRING_COUNT");
					if(util.IsNotNUllorEmpty(mapLimit))
					{
						String strDailyCount = mapLimit.get("TRANSFER_DAILY_COUNT");
						String strMonthlyCount = mapLimit.get("TRANSFER_MONTHLY_COUNT");
						data.addToLog(elementName, "Barring daily count: "+strDailyCount+"\t monthly count:"+strMonthlyCount);
						data.setSessionData("S_DAILY_COUNT", Integer.parseInt(strDailyCount));
						data.setSessionData("S_MONTHLY_COUNT", Integer.parseInt(strMonthlyCount));
						data.addToLog(elementName, "Transfer Agent  is :" +exitState);
					}
					else
					{
						data.addToLog(elementName, "Error fetching Transfer count map. Value:"+mapLimit);
					}
				}else{
					exitState="NO";
					data.addToLog(elementName, "Transfer Agent is :" +exitState);
				}

			}
		}catch (Exception e) {
			data.addToLog(elementName, e.toString());

		}
		finally
		{
			util = null;
			businessHourMap= null;
			objStartEndMap= null;
			mapLimit= null;	
		}
		return exitState;
	}

	private static boolean isTimeWith_in_Interval(DecisionElementData data, String startTime, String endTime, Utilities util) {
		boolean isBetween = false;
		try {
			SimpleDateFormat objsdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
			String currDate = new SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().getTime());
			Date objStartDate = objsdf.parse(currDate +" "+startTime);
			Date objEndDate = objsdf.parse(currDate +" "+endTime);
			Date objCurrDate = Calendar.getInstance().getTime();
			data.addToLog("start time is :", ""+objStartDate  );
			data.addToLog("end time is :", ""+objEndDate  );
			data.addToLog("current time is :", ""+objCurrDate  );

			if (objCurrDate.after(objStartDate) && objCurrDate.before(objEndDate)) {
				isBetween = true;
			}else{
				isBetween=false;
			}
		} catch (ParseException e) {
			util.errorLog("Business Hours Check", e);
		}
		return isBetween;
	}

}

