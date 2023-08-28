package com.common;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class TicketAnalysis extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		Utilities util = new Utilities(data);
		List<Map<String,String>> listTktDetails = null;
		String exitState = "ER";
		String strTktStatus = "";
		String strExpClosureDate = "";
		try {
			if(util.IsValidRestAPIResponse()) 
			{
				listTktDetails = (List<Map<String, String>>) data.getSessionData("S_TICKET_DETAILS");
				int closedCounter = 0;
				int openCounter = 0;
				if(util.IsNotNUllorEmpty(listTktDetails))
				{
					for(Map<String,String> mapTktinfo : listTktDetails )
					{
						if(mapTktinfo.containsKey("status"))
						{
							if("Closed".equalsIgnoreCase(mapTktinfo.get("status"))||"CANCELLED".equalsIgnoreCase(mapTktinfo.get("status")))
							{
								if(!util.IsNotNUllorEmpty(strTktStatus)||strTktStatus.equalsIgnoreCase("CLOSED")||strTktStatus.equalsIgnoreCase("CANCELLED")||strTktStatus.equalsIgnoreCase("RESOLVED"))
								{
									strTktStatus = "CLOSED";
								}
								closedCounter++;
							}
							else
							{
								openCounter++;
								strTktStatus = mapTktinfo.get("status").trim().toUpperCase();
								strExpClosureDate = mapTktinfo.get("expectedClosureDate");
								data.setSessionData("S_EXP_CLOSURE_DATE", strExpClosureDate);
							}
						}

					}
					data.setSessionData("S_TKT_STATUS_AUD", data.getSessionData("S_TKT_STATUS_"+strTktStatus.replaceAll(" ", "_")));
					data.setSessionData("S_TKT_STATUS", strTktStatus);

					data.addToLog(strElementName, "Total tickets: "+listTktDetails.size()+"\n No. of open tickets: "+openCounter+"\n No. of closed tickets: "+closedCounter);
					if(closedCounter==listTktDetails.size())
					{
						exitState = "All_Closed";
					}
					else if(openCounter>1)
					{
						exitState = "MoreThanOne_Open";
						util.setTransferData(data, "OPEN_TICKET_TRANSFER");
					}
					else if(openCounter==1)
					{
						String unixFormatDate=util.parseUnixDate(strExpClosureDate, "ddMMyyyyHHmm");
						if(util.diffInHours(unixFormatDate, "ddMMyyyyHHmm")>0)
						{

							data.addToLog(strElementName, "Ticket within SLA. ");
							exitState = "Within_SLA";
						}
						else
						{
							data.addToLog(strElementName, "Ticket beyond SLA. ");
							exitState = "SLA_Breach";
							util.setTransferData(data, "OPEN_TICKET_TRANSFER");
						}
					}
				}
				else
				{
					data.addToLog(strElementName, "No tickets available: "+listTktDetails);
					exitState = "No_Tkt";
				}
			}
			else
			{
				data.addToLog(strElementName, "Error fetching ticket details.");
			}

		}catch(Exception e) {
			util.errorLog(strElementName, e);
		}finally {
			util = null;
			listTktDetails = null;
		}
		return exitState;
	}

}
