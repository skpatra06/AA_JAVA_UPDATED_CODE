package com.common.faf;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Product_ManageFAFList extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) 
			{
				exitState = "SU";
			}
		} catch (Exception e)
		{
			data.addToLog(elementName,e.toString());
		}finally {
			util=null;
		}
		return exitState;
	}
}
