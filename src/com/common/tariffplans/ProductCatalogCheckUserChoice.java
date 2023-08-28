package com.common.tariffplans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ProductCatalogCheckUserChoice extends DecisionElementBase
{
	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState ="6";
		Utilities util = new Utilities(data);
		List<Map<String, String>> productCatalogPlanList =null;
		Map<String,String> moreOption=null;
		Map<String,String> mapAvailable=null;
		List<Map<String, String>> remainingProductList=null;
		List<Map<String, String>> totalProductList=null;
		List<Map<String, String>> dupAvailableProductList= null;
		try {
			productCatalogPlanList=(List<Map<String, String>>) data.getSessionData("S_LOADED_PC_PLAN_LIST");
			String previousMenuElement=util.getMenuElementValue();
			int selectedOption =Integer.parseInt(previousMenuElement);
			data.addToLog("--- SELECTED PRODUCT OPTION  : ",""+selectedOption);
			if(selectedOption<=4) 
			{
				if(util.IsNotNUllorEmpty(productCatalogPlanList))
				{
					mapAvailable=productCatalogPlanList.get(selectedOption-1);
					data.setSessionData("S_PCC_NEW_PLANID", mapAvailable.get("planId"));
					data.setSessionData("S_REPEAT_COUNTER","0");
					exitState =""+selectedOption;
				}else
				{
					data.addToLog(elementName, "Error fetching Product Catalog Map. Value:"+productCatalogPlanList);
				}
			}
			else if(5==selectedOption)
			{
				remainingProductList=(List<Map<String, String>>) data.getSessionData("S_REMAINING_PC_PLAN_LIST");
				if(util.IsNotNUllorEmpty(remainingProductList))
				{
				totalProductList=new ArrayList<>();
				dupAvailableProductList=new ArrayList<>();
				dupAvailableProductList.addAll(remainingProductList);
				data.addToLog(elementName, " Remaining Available Product Catalog Plans  : "+remainingProductList.size());
				for(int i=0;i<remainingProductList.size();i++)
				{
					if(i==4)
					{
						moreOption=new HashMap<String, String>();;
						moreOption.put("MORE_OPTION", "S_PC_PLAN_MORE_OPTION_DC");
						totalProductList.add(moreOption);
						break;
					}
					totalProductList.add(dupAvailableProductList.remove(0));
				}
				util.setAudioItemListForMenu("S_PC_AVAILABLE_PLANS_DETAILS",totalProductList, "S_PC_AVAILABLE_PLAN_DC");
				data.setSessionData("S_LOADED_PC_PLAN_LIST", totalProductList);
				data.setSessionData("S_REMAINING_PC_PLAN_LIST",dupAvailableProductList);
				exitState="5";
				}
			}
			else
			{
				data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				exitState="6";	
			}
		}catch(Exception e) {
			util.errorLog(elementName, e);
		}finally {
			productCatalogPlanList=null;
			util=null;
			moreOption=null;
			mapAvailable=null;
			remainingProductList=null;
			totalProductList=null;
			dupAvailableProductList= null;
		}
		return exitState;
	}

}

