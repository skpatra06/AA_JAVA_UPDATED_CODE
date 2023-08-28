package com.common.tariffplans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ProductCatalogAvailablePlans extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "Failure";
		Utilities util = new Utilities(data);
		List<Map<String, String>> availableProductCatalogDetails =null;
		Map<String,String> moreOption=null;	
		List<Map<String,String>> dupProductCatalogList=null;
		List<Map<String,String>> totalProductCataLog=null;
		try {
			if(util.IsValidRestAPIResponse())
			{
				availableProductCatalogDetails = (List<Map<String, String>>) data.getSessionData("S_PC_AVAILABLE_PLAN");
				util.addToLog(elementName+" Available Product Catalog      : "+availableProductCatalogDetails,util.DEBUGLEVEL);
				data.addToLog(elementName," Available Product Catalog Size : "+availableProductCatalogDetails.size());				
				if(util.IsNotNUllorEmpty(availableProductCatalogDetails))
				{
					dupProductCatalogList=new ArrayList<>();
					totalProductCataLog=new ArrayList<>();
					dupProductCatalogList.addAll(availableProductCatalogDetails);
					for(int i=0;i<availableProductCatalogDetails.size();i++)
					{
						if(i==4) {
							moreOption=new HashMap<>();
							moreOption.put("MORE_OPTION","S_PC_PLAN_MORE_OPTION_DC");
							totalProductCataLog.add(moreOption);
							break;
						}
						totalProductCataLog.add(dupProductCatalogList.remove(0));
					}
					util.setAudioItemListForMenu("S_PC_AVAILABLE_PLANS_DETAILS",totalProductCataLog,"S_PC_AVAILABLE_PLAN_DC");
					data.setSessionData("S_LOADED_PC_PLAN_LIST", totalProductCataLog);
					data.setSessionData("S_REMAINING_PC_PLAN_LIST",dupProductCatalogList);
					exitState="Success";
				}else {
					data.addToLog(elementName,"Null Value  Ocuurs in Product Catalog Bundle");
				}
			}else{
				data.addToLog(elementName, "API Response is failed");
			}
	}catch(Exception e) {
		util.errorLog(elementName, e);
	}finally {
		util=null;
		availableProductCatalogDetails =null;
		moreOption=null;	
		dupProductCatalogList=null;
		totalProductCataLog=null;
	}
	return exitState;
}



}

