package com.common.transfer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CheckHolidays extends DecisionElementBase
{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState="NO";
		Utilities util = new Utilities(data);
		Map<String ,Map<String , String>> holidayMap = null;
		Map<String ,String > holiday = null;
		try
		{
			//Map<String ,Map<String , String>> holidayMap = (Map<String, Map<String, String>>) data.getApplicationAPI().getApplicationData("Holidays");
			holidayMap = (Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("Holidays");
			data.addToLog(elementName, "Holiday map is :" +holidayMap);
			if(util.IsNotNUllorEmpty(holidayMap))
			{
				for(String key1:holidayMap.keySet()) 
				{
					holiday = holidayMap.get(key1);
					if(util.IsNotNUllorEmpty(holiday))
					{
						String startDate = holiday.get("StartDateTime");
						String endDate = holiday.get("EndDateTime");
						data.addToLog(elementName, "Holiday  : "+ key1);
						data.addToLog(elementName, "start & End date time is  :"+ startDate+" : " +endDate);
						if(null!=startDate && !startDate.isEmpty()&&null!=endDate && !endDate.isEmpty())
						{
							if(dateBeforEndDateTime(util,data,startDate,endDate))
							{
								exitState="YES";
								data.addToLog(elementName, "Holiday");
								break;

							}else{
								exitState="NO";
								data.addToLog(elementName, "Transfer Agent allowed end time crossed");
							}
						}else{
							exitState="NO";
							data.addToLog(elementName, "Transfer Agent Allowed not holiday");
						}
					}
				}
			}
		} catch (Exception e)
		{
			util.errorLog(elementName, e);
		}finally
		{
			holiday = null;
			holidayMap = null;
		    util=null;
		}
		return exitState;
	}

	private  boolean dateBeforEndDateTime(Utilities util,DecisionElementData data,String startDateTime,String endDateTime) 
	{
		boolean exit = false;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try 
		{
			String currDate=formatter.format(new Date());
			Date currentDateT=formatter.parse(currDate);
			Date startDateTimeT = formatter.parse(""+startDateTime);
			Date endDateT = formatter.parse(""+endDateTime);
			data.addToLog("Current Date  : ",""+currentDateT);
			data.addToLog("Start   Date  : ",""+startDateTimeT);
			data.addToLog("End     Date  : ",""+endDateT);
			if(currentDateT.compareTo(startDateTimeT)>=0&&currentDateT.compareTo(endDateT)<=0)
			{
				exit = true;
			}
			else
			{
				exit=false;
			}

		}
		catch (Exception e)
		{
			util.errorLog("CHECK HOLIDAYS ",e);
		}
		return exit; 
	}

}
