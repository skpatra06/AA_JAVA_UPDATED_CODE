package com.prepaid.niger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Enterprise_Check_Active_Lang extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState="BalAud";
		Utilities util = new Utilities(data);
		List<Map<String,String>> ticketDetailsList = null;
		String ticketID = null;
		try 
		{
			String activeLang = String.valueOf(data.getSessionData("S_ACTIVE_LANG"));
			String balSMSLang = String.valueOf(data.getSessionData("S_BAL_SMS_LANG"));
			ticketDetailsList =(List<Map<String,String>>)data.getSessionData("S_TICKET_DETAILS");
			data.addToLog(elementName, "The Ticket id of the ticket "+ticketDetailsList);
			data.addToLog(elementName, "Active Language :"+activeLang+" : Languages for which balance SMS should be send "+balSMSLang);
			if(util.IsNotNUllorEmpty(balSMSLang)&&balSMSLang.contains(activeLang)) {
				String closerDate = String.valueOf(data.getSessionData("S_EXP_CLOSURE_DATE"));
				long l=Long.parseLong(closerDate);
				SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
				Date date=new Date(l);
				data.addToLog("CONVERTED  DUE DATE : ",dateFormat.format(date));
				data.setSessionData("S_EXP_CLOSURE_DATE_SMS", dateFormat.format(date));
				if(util.IsNotNUllorEmpty(ticketDetailsList)){
					util.addToLog(elementName+ "Ticket Details : "+ticketDetailsList,util.DEBUGLEVEL);
					for(Map<String, String> ticketDetail : ticketDetailsList){
						if(ticketDetail.containsKey("ticketId")) {
							ticketID=String.valueOf(ticketDetail.get("ticketId"));
						}
					}
					data.setSessionData("S_TICKET_ID", ticketID);
					data.addToLog(elementName, "S_TICKET_ID is : "+ticketID);
					exitState ="BalSMS";
				}else{
					util.addToLog(elementName+ "Null Value Occur In Ticket Details : "+ticketDetailsList);	
				}
			}
		} 
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			ticketDetailsList=null;
			util=null;
		}
		return exitState;
	}
}