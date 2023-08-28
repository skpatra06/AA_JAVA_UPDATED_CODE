package com.outbound;

import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class OutboundCallFlowCheck extends DecisionElementBase
{
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="CampNotAvai";
		Utilities util=new Utilities(data);
		Map<String,String> dnisDetails=null; 
		try
		{
			String ivrCampID="";
			String dnis=String.valueOf(data.getSessionData("S_DNIS"));
			dnisDetails=(Map<String, String>) data.getApplicationAPI().getApplicationData("IVR_CAMP_DNIS_DETAILS");
			data.addToLog(elementName, " DNIS : "+dnis);
			if(util.IsNotNUllorEmpty(dnisDetails)) 
			{
				util.addToLog("IVR Camp Details Map : "+dnisDetails,util.DEBUGLEVEL );
				if(dnisDetails.containsKey(dnis))
				{
					if("SP".equalsIgnoreCase(dnisDetails.get(dnis))) 
					{
						data.setSessionData("S_APPNAME", "Bundle_Purchase_IVR");
						exitState="SellProduct";
					}
					else if("SS".equalsIgnoreCase(dnisDetails.get(dnis)))
					{
						data.setSessionData("S_APPNAME", "Educational_IVR");
						exitState="SelfServ";
					}
					else if("SF".equalsIgnoreCase(dnisDetails.get(dnis)))
					{
						data.setSessionData("S_APPNAME", "Survey_IVR");
						exitState="SurveyFeedback";
					}
					else if("TP".equalsIgnoreCase(dnisDetails.get(dnis))) {
						data.setSessionData("S_APPNAME", "TelePromotion_IVR");
						exitState="TelePromo";
					}
					else if("EO".equalsIgnoreCase(dnisDetails.get(dnis)))
					{
						data.setSessionData("S_APPNAME","Emergency_IVR");
						exitState="EmergencyOutbound";
					}
				}
				if("CampNotAvai".equalsIgnoreCase(exitState))
					data.addToLog(ivrCampID, "No Campaign Available for this DNIS ");
			}
			else
			{
				data.addToLog(elementName, "Null Value Occurs in IVR Camp details Map");	
			}
		}
		catch(Exception e)
		{
			util.errorLog(elementName, e); 
		}
		finally 
		{
			dnisDetails=null;
			util=null;
		}
		return exitState;

	}

}
