package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

import com.util.Utilities;

public class Get_VIPToAgent extends DecisionElementBase 
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitstate = "ER";
		Utilities util = new Utilities(data); 
		try 
		{
			if(util.IsValidDBSUResponse())
			{
				int agentcounter=util.convertToInt("S_AGENT_COUNTER");
				agentcounter+=1;
				int vipThreshold = util.convertToInt("S_VIP_THRESHOLD");
				if(agentcounter<vipThreshold) 
				{
					data.addToLog(elementName, "VIP Agent Counter :"+ agentcounter+" less than "
							+ " the Threshold :"+vipThreshold+" hence routing the call to menu");
					data.setSessionData("S_AGENT_COUNTER", ""+agentcounter);
					exitstate ="NO";
				}
				else
				{
					util.setTransferData(data, "AA_PRP_XX_HA_0011_DB");
					data.setSessionData("S_AGENT_COUNTER", ""+agentcounter);
					data.addToLog(elementName, "VIP Agent Counter :"+ agentcounter+" reached the Threshold :"+vipThreshold);
					exitstate ="YES";
				}
			}
			else if(util.IsValidDBNRResponse()) {
				data.setSessionData("S_AGENT_COUNTER", "1");
				data.addToLog(elementName, "VIP Caller Is not Found in Table hence setting the Agent counter to 1");
				exitstate ="NO";
			}

		}catch(Exception e) {
			util.errorLog(elementName, e);
		}
		finally{
			util = null;
		}
		return exitstate;
	}
}
