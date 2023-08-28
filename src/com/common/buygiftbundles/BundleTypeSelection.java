package com.common.buygiftbundles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundleTypeSelection extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "";
		Utilities util=new Utilities(data);
		List<Map<String, String>> listBundleNew= null;
		List<Map<String, String>> listBundleTotal= null;
		List<Map<String, String>> listBundleDup= null;
		Map<String,String> mapBundleData = null;
		Map<String,List<Map<String,String>>> mapBundlesProcessed= null;
		List<Map<String,String>> listBundlesProcessed = null;
		try
		{
			listBundleNew= new ArrayList<Map<String, String>>();

			String strBundleType= (String) data.getSessionData("S_BUNDLE_TYPE");
			String strPurchaseType=(String) data.getSessionData("S_PURCHASE_TYPE");

			listBundleTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			data.addToLog(strElementName, "Total Bundle Data: "+listBundleTotal);
			listBundleDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
			data.addToLog(strElementName, "Remaining Bundle Data: "+listBundleDup);
			String strUserInput= (util.getMenuElementValue());
			data.addToLog(strElementName, "User input from Bundle Selection menu:"+strUserInput);
			if(strUserInput.equalsIgnoreCase("*")||strUserInput.equalsIgnoreCase("#"))
			{
				strExitState=strUserInput.equals("#")?"HASH":"STAR";
			}
			else if(Integer.parseInt(strUserInput)<=4) 
			{
				mapBundleData=listBundleTotal.get(Integer.parseInt(strUserInput)-1);

				if(mapBundleData.containsKey("Purchase_Type")&&"Static".equalsIgnoreCase(mapBundleData.get("Purchase_Type")))
				{
					data.setSessionData("S_BUNDLE_AUDIO", String.valueOf(util.IsNotNUllorEmpty(mapBundleData.get("Static_Audio"))?mapBundleData.get("Static_Audio"):mapBundleData.get("Wave ID")));
					data.setSessionData("S_REPEAT_COUNTER","0");
					return "StaticBundle";
				}
				String strPeriodSelected = mapBundleData.get("planWav").replace(".wav","");
				mapBundlesProcessed=(Map<String, List<Map<String, String>>>) data.getSessionData("S_BUNDLE_PERIODWISE");
				if(util.IsNotNUllorEmpty(mapBundlesProcessed)&& mapBundlesProcessed.containsKey(strPeriodSelected))
				{
					listBundlesProcessed = mapBundlesProcessed.get(strPeriodSelected);
					if(util.IsNotNUllorEmpty(listBundlesProcessed))
					{
						util.addToLog(strElementName+ "Loading "+strPeriodSelected+" Bundles: "+listBundlesProcessed, util.DEBUGLEVEL);
						listBundleNew.addAll(listBundlesProcessed);

						if(util.IsNotNUllorEmpty(listBundleNew))
						{
							data.addToLog(strElementName, listBundleNew.size()+" bundles added to Total List:"+listBundleNew);					
							util.loadBundlesMenu(listBundleNew, false, "S_BUNDLE_"+strBundleType+"_"+strPurchaseType+"_DC", data);
							strExitState = "OtherBundles";
							data.setSessionData("S_REPEAT_COUNTER","0");
							return strExitState;
						}
					}
					else
					{
						data.addToLog(strElementName, "Error fetching "+strPeriodSelected+" bundles: "+listBundlesProcessed);

					}
				}
				else
				{
					data.addToLog(strElementName, "Error fetching periodwise map "+mapBundlesProcessed);
				}
				util.addFreqBundle(mapBundleData,strBundleType,data);
				String strBundleAmount = String.valueOf(mapBundleData.get("amount"));
				data.setSessionData("S_PRODUCT_CODE", String.valueOf(mapBundleData.get("Product_code")));
				data.addToLog(strElementName, "Selected Bundle Amount: "+strBundleAmount);
				data.setSessionData("S_RECHARGE_AMT", strBundleAmount);
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
				strExitState = "BundleSelected";
			}
			else if(strUserInput.equalsIgnoreCase("5"))
			{
				util.loadBundlesMenu(listBundleDup, false, "S_BUNDLE_"+strBundleType+"_"+strPurchaseType+"_DC", data);
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
			listBundleNew= null;
			listBundleTotal= null;
			listBundleDup= null;
			mapBundleData = null;
			mapBundlesProcessed= null;
			listBundlesProcessed = null;
		}
		data.setSessionData("S_REPEAT_COUNTER","0");
		return strExitState;
	}
}