package com.common.buygiftbundles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundleSelectionCheck extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "";
		Utilities util=new Utilities(data);
		Map<String,String> mapBundleData = null;
		List<Map<String, String>> listBundlesTotal= null;
		List<Map<String, String>> listBundlesDup= null;
		
		try
		{
			String strBundleType= (String) data.getSessionData("S_BUNDLE_TYPE");
			String strPurchaseType=(String) data.getSessionData("S_PURCHASE_TYPE");

			listBundlesTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			data.addToLog(strElementName, "Total Bundle Data: "+listBundlesTotal);


			String strBundleAmount="";
			String strUserInput= (util.getMenuElementValue());
			data.addToLog(strElementName, "User input from Bundle Selection menu:"+strUserInput);

			if(strUserInput.equalsIgnoreCase("*")||strUserInput.equalsIgnoreCase("#"))
			{
				strExitState=strUserInput.equals("#")?"HASH":"STAR";
			}
			else if(Integer.parseInt(strUserInput)<=4) {
				mapBundleData=listBundlesTotal.get(Integer.parseInt(strUserInput)-1);

				if(mapBundleData.containsKey("Purchase_Type")&&"Static".equalsIgnoreCase(mapBundleData.get("Purchase_Type")))
				{
					data.setSessionData("S_BUNDLE_AUDIO", String.valueOf(util.IsNotNUllorEmpty(mapBundleData.get("Static_Audio"))?mapBundleData.get("Static_Audio"):mapBundleData.get("Wave ID")));
					data.setSessionData("S_REPEAT_COUNTER","0");
					return "StaticBundle";
				}

				util.addFreqBundle(mapBundleData,strBundleType,data);

				strBundleAmount = String.valueOf(mapBundleData.get("amount"));
				data.addToLog(strElementName, "Selected Bundle Amount: "+strBundleAmount);
				data.setSessionData("S_RECHARGE_AMT", strBundleAmount);
				data.setSessionData("S_PRODUCT_CODE", String.valueOf(mapBundleData.get("Product_code")));
				
				if(mapBundleData.containsKey("Purchase_Account"))
				{
					if("AM".equalsIgnoreCase(mapBundleData.get("Purchase_Account")))
					{
						data.addToLog(strElementName, "User selected AM Bundle");
						return "AM_Bundles";
					}
					else if("GSM".equalsIgnoreCase(mapBundleData.get("Purchase_Account")))
					{
						data.addToLog(strElementName, "User selected Airtime Bundle");
						return "AirTime_Bundles";
					}
				}
				strExitState = ""+strUserInput;
			}
			else if(strUserInput.equalsIgnoreCase("5"))
			{
				listBundlesDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
				data.addToLog(strElementName, "Remaining Bundle Data: "+listBundlesDup);

				util.loadBundlesMenu(listBundlesDup, false, "S_BUNDLE_"+strBundleType+"_"+strPurchaseType+"_DC", data);
				strExitState = "5";
			}
			else
			{
				data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				return "6";
			}
			

		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
			mapBundleData = null;
			listBundlesTotal= null;
			listBundlesDup= null;
		}
		data.setSessionData("S_REPEAT_COUNTER","0");
		return strExitState;
	}
}
