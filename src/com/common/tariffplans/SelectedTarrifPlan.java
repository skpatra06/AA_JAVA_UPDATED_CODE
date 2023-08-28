package com.common.tariffplans;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class SelectedTarrifPlan extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitstate = "2";
		Utilities util = new Utilities(data);
		try {
			String trafficSelectedOption=util.getMenuElementValue();
			data.addToLog(elementName,"TRAFFIC MIGRATION SELECTED OPTION : "+trafficSelectedOption);
			if("1".equals(trafficSelectedOption)) 
			{
				exitstate ="1";
			}else {
				data.addToLog(elementName, " TRAFFIC MIGRATION PLAN HAS BEEN CANCEL ... ");
			}
		}catch(Exception e)
		{
			util.errorLog(elementName, e);
		}finally{
			util = null;
		}
		return exitstate;
	}

}

