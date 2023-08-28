package com.common.tariffplans;

import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ProductCatalogCurrentPlan extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitstate = "Failure";
		Utilities util = new Utilities(data);
		Map<String,Map<String,String>> tariffPlans= null;
		Map<String,String> currPlanMap=null;
		try 
		{
			if(util.IsValidRestAPIResponse())
			{
				String serviceClassID=String.valueOf(data.getSessionData("S_PCC_CURR_SERVICE_CLASS"));
				tariffPlans=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("TARIFF_PLANS");
				data.addToLog(elementName, "TARIFF CURR PLAN : SERVICE CLASS ID : "+serviceClassID);
				if(util.IsNotNUllorEmpty(tariffPlans)&&util.IsNotNUllorEmpty(serviceClassID))
				{
					util.addToLog("TARIFF PLAN  MAP : "+tariffPlans, util.DEBUGLEVEL);
					if(tariffPlans.containsKey(String.valueOf(serviceClassID))) 
					{
						currPlanMap=tariffPlans.get(serviceClassID);
						if(util.IsNotNUllorEmpty(currPlanMap))
						{
							String tariffPlanWav=currPlanMap.get("WAV_ID");
							if(util.IsNotNUllorEmpty(tariffPlanWav))
							{
								if(!(tariffPlanWav.contains(".wav")))
								{
									tariffPlanWav+=".wav";
								}
								data.setSessionData("S_TARIFF_PLAN_WAV", tariffPlanWav);
								exitstate="Success";
							}
						}
						else
						{
							data.addToLog(elementName,"NULL VALUE IN WAV ID ");	
						}
					}
					else 
					{
						data.addToLog(elementName,"Service Class Is Not Available " );	
					}
				}
				else 
				{
					data.addToLog(elementName,"NULL VALUE OCCURS IN TARIFF PLAN MAP SHEET " );	
				}
			}

		}
		catch(Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			tariffPlans= null;
			currPlanMap=null;
			util = null;	
		}
		return exitstate;
	}
}