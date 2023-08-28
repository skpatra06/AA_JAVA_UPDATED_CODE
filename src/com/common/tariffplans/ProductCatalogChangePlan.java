package com.common.tariffplans;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ProductCatalogChangePlan extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitstate = "Failure";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidRestAPIResponse()) {
				String changePlanStatus=(String) data.getSessionData("S_CHANGE_PLAN_STATUS");
				data.addToLog(elementName, "CHANGE PLAN STATUS :"+changePlanStatus);
				if(util.IsNotNUllorEmpty(changePlanStatus)&&"200".equals(changePlanStatus))
				{
					exitstate ="Success";
				}
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

