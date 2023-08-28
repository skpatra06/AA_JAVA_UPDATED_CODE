package com.hbb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Bundle_MultiChoice_Selection extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		Map<String,String> mapBundleData = null;
		Map<String,Set<String>> mapPerioddata= null;
		Map<String ,List<Map<String, String>>> mapBundlesConfig= null;
		Map<String,List<Map<String, String>>> mapBundlesProcessed=null;
		List<Map<String, String>> listBundlesNew= null;
		List<Map<String, String>> listBundlesConfig = null;
		List<Map<String, String>> listBundlesTotal= null;
		List<Map<String, String>> listBundlesDup= null;
		List<Map<String, String>> listBundlesProcessed = null;
		Set<String> setPeriodType= null;
		try {
			listBundlesNew= new ArrayList<Map<String, String>>();	
			String strBundleType= "HBB";
			String strPeriodType=(String) data.getSessionData("S_USER_SELECTED_PERIOD_TYPE");
			String strSelectedOpt=util.getMenuElementValue();
			data.addToLog(strElementName, "User input from Bundle Selection menu:"+strSelectedOpt);
			listBundlesTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			data.addToLog(strElementName, "Total Bundle Data: "+listBundlesTotal);

			if("*".equalsIgnoreCase(strSelectedOpt)||"#".equalsIgnoreCase(strSelectedOpt))
			{
				strExitState=strSelectedOpt.equals("#")?"HASH":"STAR";	
			}
			else if(util.IsNotNUllorEmpty(strSelectedOpt)&&Integer.parseInt(strSelectedOpt)<=4) {
				mapBundleData=listBundlesTotal.get((Integer.parseInt(strSelectedOpt))-1);
				String strBundleTabName=mapBundleData.get("Tab Name");
				data.addToLog(strElementName, "TAB NAME: "+ strBundleTabName);
				mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
				if(util.IsNotNUllorEmpty(mapBundlesConfig))
				{
					listBundlesConfig = mapBundlesConfig.get(strBundleTabName);
					if(util.IsNotNUllorEmpty(listBundlesConfig))
					{
						data.addToLog(strElementName, "Loading "+strBundleTabName+" Bundles: "+listBundlesConfig);
						for(Map<String,String> mapBundleInfo:listBundlesConfig) 
						{
							if("NA".equals(mapBundleInfo.get("Product_code")))
							{

								listBundlesNew.addAll(listBundlesConfig);
								strExitState="More_Bundle_Seg";
								break;
							}
						}
						if(!util.IsNotNUllorEmpty(listBundlesNew)) {
							mapPerioddata=(Map<String, Set<String>>) data.getApplicationAPI().getApplicationData("S_PERIOD_TYPES");
							if(util.IsNotNUllorEmpty(mapPerioddata))
							{
								setPeriodType= mapPerioddata.get(strBundleTabName);
								if(util.IsNotNUllorEmpty(setPeriodType))
								{
									data.addToLog(strElementName, "PERIOD DATA : "+mapPerioddata);

									mapBundlesProcessed= util.splitBundlePeriodwise( setPeriodType,listBundlesConfig);
									data.addToLog(strElementName, strBundleType+" Bundles retrieved: "+mapBundlesProcessed);

									if(!(util.IsNotNUllorEmpty(listBundlesNew)&&util.IsNotNUllorEmpty(mapBundlesProcessed)))
									{

										listBundlesProcessed = mapBundlesProcessed.get(strPeriodType);
										data.addToLog(strElementName,strPeriodType+" Bundles: "+listBundlesProcessed);
										if(util.IsNotNUllorEmpty(listBundlesProcessed))
										{

											listBundlesNew.addAll(listBundlesProcessed);
											strExitState = "BundleSegSelected";
										}		
									}
								}
								else
								{
									data.addToLog(strElementName, "Error fetching "+strBundleTabName+" Period data: "+setPeriodType);
								}
							}
							else
							{
								data.addToLog(strElementName, "Error fetching Period data map: "+mapPerioddata);
							}
						}
						if(util.IsNotNUllorEmpty(listBundlesNew))
						{
							data.addToLog(strElementName, listBundlesNew.size()+" bundles added to Total List:"+listBundlesNew);					
							util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_BUY_"+strPeriodType.toUpperCase()+"_DC", data);
							data.addToLog(strElementName, "EXIT STATE :"+strExitState);
							data.setSessionData("S_BUNDLE_PERIODWISE", mapBundlesProcessed);
							data.setSessionData("S_REPEAT_COUNTER","0");
						}
						else 
						{
							data.addToLog(strElementName, "Null value In Bundle Map ");
						}

					}
					else
					{
						data.addToLog(strElementName, "Error fetching "+strBundleTabName+"Bundles"+listBundlesConfig);
					}
				}
				else 
				{
					data.addToLog(strElementName, "No Bundle Data Matched "+mapBundlesConfig);
				}
			} 
			else if("5".equalsIgnoreCase(strSelectedOpt)) 
			{
				listBundlesDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
				data.addToLog(strElementName, "Remaining Bundle Data: "+listBundlesDup);
				util.loadBundlesMenu(listBundlesDup, false, "S_BUNDLE_"+strBundleType+"_BUY_"+strPeriodType.toUpperCase()+"_DC", data);
				strExitState = "5";
				data.setSessionData("S_REPEAT_COUNTER","0");
				data.addToLog(strElementName, "EXIT STATE :"+strExitState);
			}
			else
			{
				data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				strExitState="6";
				data.addToLog(strElementName, "EXIT STATE :"+strExitState);

			}
		} catch (Exception e) {
			util.errorLog(strElementName, e);
		} finally {
			listBundlesDup= null;
			mapBundleData = null;
			mapPerioddata= null;
			mapBundlesConfig= null;
			mapBundlesProcessed=null;
			listBundlesNew= null;
			listBundlesConfig = null;
			listBundlesTotal= null;
			listBundlesProcessed = null;
			util=null;
			setPeriodType=null;
		}
		return strExitState;
	}


}


