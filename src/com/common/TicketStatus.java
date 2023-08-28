package com.common;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class TicketStatus extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		Utilities util = new Utilities(data);
		List<Map<String,String>> listTktDetails = null;
		String exitState = "ER";
		String strTktStatus = "";
		try {
			String statuSCode = String.valueOf(data.getSessionData("S_STATUS_CODE"));
			if("200".equals(statuSCode)) {
				listTktDetails = (List<Map<String, String>>) data.getSessionData("S_TICKET_DETAILS");
				util.addToLog(strElementName+" Ticket details fetched: "+listTktDetails, util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(listTktDetails))
				{
					for(Map<String,String> mapTktinfo : listTktDetails )
					{
						if(mapTktinfo.containsKey("status"))
						{
							strTktStatus = mapTktinfo.get("status").trim().toUpperCase();

							if(util.IsNotNUllorEmpty(strTktStatus)&&!(strTktStatus.equalsIgnoreCase("RESOLVED")||strTktStatus.equalsIgnoreCase("CLOSED")))
							{							
								data.setSessionData("S_TKT_STATUS_AUD", data.getSessionData("S_TKT_STATUS_"+strTktStatus.replaceAll(" ", "_")));
								data.setSessionData("S_EXP_CLOSURE_DATE", mapTktinfo.get("expectedClosureDate"));
								break;
							}
						}

					}
					data.setSessionData("S_TKT_STATUS", strTktStatus);
					data.addToLog(strElementName, "Ticket status: "+strTktStatus);
					exitState = "SU";
				}
				else
				{
					data.addToLog(strElementName, "No tickets available: "+listTktDetails);
					exitState ="No_Tkt";
				}
			}
			else
			{
				data.addToLog(strElementName, "Error fetching ticket details.");
			}
		}
		catch(Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
			listTktDetails = null;
		}
		return exitState;
	}

}
