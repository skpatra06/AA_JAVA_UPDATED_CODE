package com.common.buygiftbundles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class FreqBundleSelection extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "ER";
		Utilities util=new Utilities(data);
		List<Map<String, String>> listBundlesTotal= null;
		List<Map<String, String>> listBundlesDup= null;
		Map<String,String> mapBundleData = null;
		List<Map<String, String>> listBundlesNew= null;
		Map<String,List<Map<String,String>>> mapBundlesProcessed= null;
		Map<String,Set<String>> mapPerioddata= null;
		Map<String,List<Map<String, String>>> mapBundlesConfig = null;
		List<Map<String, String>> listBundlesConfig = null;
		Set <String> setPerioddata = null;
		List<Map<String, String>> listBundlesProcessed = null;
		Map<String, String> mapBundleLoader = null;
		try
		{
			String strBundleType= (String) data.getSessionData("S_BUNDLE_TYPE");
			String strPurchaseType=(String) data.getSessionData("S_PURCHASE_TYPE");
			data.addToLog(strElementName, "Selected Bundle Type is : "+strBundleType+"\t Purchase type: "+strPurchaseType);
			listBundlesTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			data.addToLog(strElementName, "Total Bundle Data: "+listBundlesTotal);


			String strUserInput= util.getMenuElementValue();
			data.addToLog(strExitState, "User input from Bundle Selection menu:"+strUserInput);
			if(strUserInput.equalsIgnoreCase("*")||strUserInput.equalsIgnoreCase("#"))
			{
				strExitState=strUserInput.equals("#")?"HASH":"STAR";
			}
			else if(Integer.parseInt(strUserInput)<=4) {
				if(util.IsNotNUllorEmpty(listBundlesTotal))
				{
					mapBundleData=listBundlesTotal.get(Integer.parseInt(strUserInput)-1);
					util.addFreqBundle(mapBundleData,strBundleType,data);
					String strBundleAmount = String.valueOf(mapBundleData.get("amount"));
					data.setSessionData("S_PRODUCT_CODE", String.valueOf(mapBundleData.get("Product_code")));
					data.addToLog(strElementName, "Selected Bundle: "+mapBundleData);
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
					strExitState = ""+strUserInput;
				}
			} 

			else if(strUserInput.equalsIgnoreCase("5"))
			{
				listBundlesDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
				data.addToLog(strElementName, "Remaining Bundle Data: "+listBundlesDup);

				listBundlesNew= new ArrayList<Map<String, String>>();
				mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
				util.addToLog(strElementName+" Map sheet data:"+mapBundlesConfig, util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(mapBundlesConfig)&&mapBundlesConfig.containsKey(strBundleType))
				{ 
					listBundlesConfig=mapBundlesConfig.get(strBundleType);
					util.addToLog(strElementName+" "+strBundleType+" List sheet data:"+listBundlesConfig, util.DEBUGLEVEL);

					if(util.IsNotNUllorEmpty(listBundlesConfig)) 
					{
						for(Map<String,String> mapBundle:listBundlesConfig) 
						{
							if(mapBundle.containsKey("Tab Name"))
							{
								listBundlesNew.addAll(listBundlesConfig);
								strExitState= "Multi_Choice";
								break;
							}
						}
						if(util.IsNotNUllorEmpty(listBundlesNew))
						{
							data.addToLog(strElementName, "Multi Segment found in "+strBundleType+" Bundles. Hence exit state:"+strExitState);
							util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_"+strPurchaseType+"_DC", data);
							data.setSessionData("S_REPEAT_COUNTER","0");
							return strExitState;
						}

						for(Map<String,String> mapBundle:listBundlesConfig)
						{
							if("NA".equalsIgnoreCase(mapBundle.get("period_type")))
							{
								listBundlesNew.addAll(listBundlesConfig);
								strExitState = "5";
								break;
							}
						}
						if(util.IsNotNUllorEmpty(listBundlesNew))
						{
							data.addToLog(strElementName, "No Period type given in "+strBundleType+" Bundles. Hence playing altogether. Exitstate:"+strExitState);
							util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_"+strPurchaseType+"_DC", data);
							data.setSessionData("S_REPEAT_COUNTER","0");
							return strExitState;
						}

					}
					else
					{
						data.addToLog(strElementName, "Error fetching Bundle sheet data");
					}
				}
				mapPerioddata=(Map<String, Set<String>>) data.getApplicationAPI().getApplicationData("S_PERIOD_TYPES");
				if(util.IsNotNUllorEmpty(mapPerioddata))
				{
					setPerioddata = mapPerioddata.get(strBundleType);
					if(util.IsNotNUllorEmpty(setPerioddata))
					{
						mapBundlesProcessed= util.splitBundlePeriodwise(setPerioddata,listBundlesConfig);
						util.addToLog(strElementName+" Bundle Periodwise Details fetched: "+mapBundlesProcessed+
								"\n Period details Map: "+mapPerioddata, util.DEBUGLEVEL);
						if(util.IsNotNUllorEmpty(mapBundlesProcessed))
						{
							data.setSessionData("S_BUNDLE_PERIODWISE", mapBundlesProcessed);

							for(String strPeriodType : setPerioddata)
							{
								listBundlesProcessed = mapBundlesProcessed.get(strPeriodType);

								if(util.IsNotNUllorEmpty(listBundlesProcessed))
								{
									if(listBundlesProcessed.size()==1)
									{
										listBundlesNew.add(listBundlesProcessed.get(0));
									}
									else
									{
										mapBundleLoader = new HashMap<>();
										mapBundleLoader.put("planWav",strPeriodType+".wav");
										listBundlesNew.add(mapBundleLoader);
									}
								}
								else 
								{
									data.addToLog(strElementName, "No "+strPeriodType+" bundles found");
								}	
								strExitState = "5";
							}
							if(util.IsNotNUllorEmpty(listBundlesNew))
							{
								data.addToLog(strElementName, listBundlesNew.size()+" bundles added to Total List:"+listBundlesNew);					
								util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_"+strPurchaseType+"_DC", data);
							}
						}
						else
						{
							data.addToLog(strElementName, "Bundles details not fetched: "+mapBundlesProcessed);
						}
					}

				}
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
			listBundlesTotal= null;
			listBundlesDup= null;
			mapBundleData = null;
			listBundlesNew= null;
			mapBundlesProcessed= null;
			mapPerioddata= null;
			mapBundlesConfig = null;
			listBundlesConfig = null;
			setPerioddata = null;
			listBundlesProcessed = null;
			mapBundleLoader = null;
		}
		data.setSessionData("S_REPEAT_COUNTER","0");
		return strExitState;

	}
}
