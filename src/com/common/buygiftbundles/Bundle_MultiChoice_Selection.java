package com.common.buygiftbundles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Bundle_MultiChoice_Selection extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		List<Map<String, String>> listBundlesNew= null;
		Map<String,String> mapBundleData = null;
		Map<String,List<Map<String, String>>> mapBundlesProcessed=null;
		List<Map<String, String>> listBundlesTotal= null;
		List<Map<String, String>> listBundlesDup= null;
		List<Map<String, String>> listBundlesConfig = null;
		Map<String,Set<String>> mapPerioddata=null;
		Map<String ,List<Map<String, String>>> mapBundlesConfig=null;
		List<Map<String, String>> listBundlesProcessed = null;
		Set <String> setPerioddata = null;
		Map<String, String> mapBundleLoader = null;

		try
		{
			listBundlesNew= new ArrayList<Map<String, String>>();	
			String strBundleType=(String) data.getSessionData("S_BUNDLE_TYPE");
			String strUserSelect=(String) data.getSessionData("S_PURCHASE_TYPE");
			String strUserInput= (util.getMenuElementValue());
			util.addToLog(strElementName+ " User input from Bundle Selection menu:"+strUserInput,util.DEBUGLEVEL);
			listBundlesTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			util.addToLog(strElementName+ " Total Bundle Data: "+listBundlesTotal, util.DEBUGLEVEL);
			listBundlesDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
			util.addToLog(strElementName+ " Remaining Bundle Data: "+listBundlesDup, util.DEBUGLEVEL);
			if(strUserInput.equalsIgnoreCase("*")||strUserInput.equalsIgnoreCase("#"))
			{
				strExitState=strUserInput.equals("#")?"HASH":"STAR";
			}
			else if(Integer.parseInt(strUserInput)<=4)
			{
				//Map<String,List<Map<String,String>>> mapOptionBundle = (Map<String, List<Map<String, String>>>) data.getSessionData("S_MAP_BUNDLES");
				//List<Map<String,String>> maploaded=mapOptionBundle.get(strBundleType);
				mapBundleData=listBundlesTotal.get(Integer.parseInt(strUserInput)-1);
				
				if(mapBundleData.containsKey("Purchase_Type")&&"Static".equalsIgnoreCase(mapBundleData.get("Purchase_Type")))
				{
					data.setSessionData("S_BUNDLE_AUDIO", String.valueOf(util.IsNotNUllorEmpty(mapBundleData.get("Static_Audio"))?mapBundleData.get("Static_Audio"):mapBundleData.get("Wave ID")));
					data.setSessionData("S_REPEAT_COUNTER","0");
					return "StaticBundle";
				}
				
				String strBundleTabName=mapBundleData.get("Tab Name");
				if(util.IsNotNUllorEmpty(strBundleTabName))
				{
					mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
					if(util.IsNotNUllorEmpty(mapBundlesConfig)&&mapBundlesConfig.containsKey(strBundleTabName))
					{
						listBundlesConfig =mapBundlesConfig.get(strBundleTabName);
						data.addToLog(strElementName, "Loading "+strBundleTabName+" Bundles: "+listBundlesConfig);
						if(util.IsNotNUllorEmpty(listBundlesConfig))
						{
							for(Map<String,String> mapBundleInfo:listBundlesConfig) 
							{
								if(mapBundleInfo.containsKey("Tab Name"))
								{
									listBundlesNew.addAll(listBundlesConfig);
									break;
								}
							}
							if(util.IsNotNUllorEmpty(listBundlesNew))
							{
								data.addToLog(strElementName, listBundlesNew.size()+" bundles added to Total List:"+listBundlesNew);					
								util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
								strExitState="More_Bundle_Seg";
								data.setSessionData("S_REPEAT_COUNTER","0");
								return strExitState;
							}
							

							for(Map<String,String> mapBundle:listBundlesConfig)
							{
								if("NA".equalsIgnoreCase(mapBundle.get("period_type")))
								{
									listBundlesNew.addAll(listBundlesConfig);
									break;
								}
							}
							if(util.IsNotNUllorEmpty(listBundlesNew))
							{
								data.addToLog(strElementName, listBundlesNew.size()+" bundles added to Total List:"+listBundlesNew);					
								util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
								strExitState = "BundleSegSelected";
								data.setSessionData("S_REPEAT_COUNTER","0");
								return strExitState;
							}
							mapPerioddata=(Map<String, Set<String>>) data.getApplicationAPI().getApplicationData("S_PERIOD_TYPES");
							data.addToLog(strElementName,  "PERIOD DATA : "+mapPerioddata+ "LIST BUNDLE CONFIG : "+listBundlesConfig);

							if(util.IsNotNUllorEmpty(mapPerioddata))
							{
								setPerioddata = mapPerioddata.get(strBundleTabName);
								if(util.IsNotNUllorEmpty(setPerioddata))
								{
									mapBundlesProcessed= util.splitBundlePeriodwise(setPerioddata,listBundlesConfig);
									data.addToLog(strElementName, strBundleType+" Bundles retrieved: "+mapBundlesProcessed);
									if(util.IsNotNUllorEmpty(mapBundlesProcessed))
									{
										for(String strPeriodType : setPerioddata)
										{
											listBundlesProcessed = mapBundlesProcessed.get(strPeriodType);
											data.addToLog("Period Type ",""+strPeriodType+": "+listBundlesProcessed);
											if(listBundlesProcessed.size()==1)
											{
												listBundlesNew.add(listBundlesProcessed.get(0));
											}
											else if(util.IsNotNUllorEmpty(listBundlesProcessed))
											{
												mapBundleLoader = new HashMap<>();
												mapBundleLoader.put("planWav",strPeriodType.toLowerCase()+".wav");
												listBundlesNew.add(mapBundleLoader);
											}	
										}
										if(util.IsNotNUllorEmpty(listBundlesNew))
										{
											data.addToLog(strElementName, listBundlesNew.size()+" bundles added to Total List:"+listBundlesNew);					
											util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
											strExitState = "BundleSegSelected";
											data.setSessionData("S_BUNDLE_PERIODWISE", mapBundlesProcessed);
											data.setSessionData("S_REPEAT_COUNTER","0");
											return strExitState;
										}
									}
									else
									{
										data.addToLog(strElementName, "Error processing "+strBundleTabName+" bundles periodwise: "+mapBundlesProcessed);
									}
								}
								else
								{
									data.addToLog(strElementName, "Error fetching "+strBundleTabName+" period data: "+setPerioddata);
								}
							}
							else 
							{
								data.addToLog(strElementName, "Error fetching period data map: "+mapPerioddata);
							}
						}
						else 
						{
							data.addToLog(strElementName, "No Bundle Data Matched for "+strBundleTabName);
						}
					}
				}
				else
				{
					data.addToLog(strElementName, "Error retrieving Tab name "+strBundleTabName+" from selected bundle: "+mapBundleData);
				}
			}
			else if(strUserInput.equalsIgnoreCase("5"))
			{
				util.loadBundlesMenu(listBundlesDup, false, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
				data.setSessionData("S_REPEAT_COUNTER","0");
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
			listBundlesNew= null;
			mapBundleData = null;
			mapBundlesProcessed=null;
			listBundlesTotal= null;
			listBundlesDup= null;
			mapPerioddata=null;
			mapBundlesConfig=null;
			listBundlesConfig =null;
			listBundlesProcessed = null;
			setPerioddata = null;
			mapBundleLoader = null;
		}
		data.setSessionData("S_REPEAT_COUNTER","0");
		return strExitState;
	}
}