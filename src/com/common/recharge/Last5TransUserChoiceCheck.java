package com.common.recharge;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Last5TransUserChoiceCheck extends DecisionElementBase
{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		String exitState="";
		Utilities util=new Utilities(data);
		try
		{
		  String userSelectedOption=util.getMenuElementValue();
		  if("2".equalsIgnoreCase(userSelectedOption)) 
		  {
			    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
				Date date = new Date();
                data.setSessionData("S_REC_USAGE_ENDDATE",date.getTime());
		        String endDate=String.valueOf(data.getSessionData("S_REC_USAGE_STARTDATE_COUNT"));
                int endDateCount=0;
		        try {
                	endDateCount=Integer.parseInt(endDate);
                }catch (Exception e) {
					endDateCount=-7;
				}
		        Calendar cal = Calendar.getInstance();
		        cal.add(Calendar.DATE,endDateCount);
		        Date endDateFormat = cal.getTime();    
		        data.setSessionData("S_REC_USAGE_STARTDATE",endDateFormat.getTime()); 
		  }
		  exitState=userSelectedOption;
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
