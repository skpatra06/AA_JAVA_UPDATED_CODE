package com.common.buygiftbundles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class FreqBundleLoader extends DecisionElementBase 
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data)
			throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		boolean freqFlag = false;
		Map<String ,List<Map<String, String>>> mapBundlesConfig= null;
		List<Map<String, String>> listBundlesNew= null;
		Map<String,List<Map<String, String>>> mapBundlesProcessed=null;
		Map<String,Set<String>> mapPerioddata= null;
		List<Map<String, String>> listBundlesConfig = null;
		List<Map<String, String>> listMultiBundlesConfig = null;
		Map<String,List<Map<String, String>>> mapFreqBundlesAll = null;
		List<Map<String, String>> listFreqBundles = null;
		Set<String> setPerioddata = null;
		Map<String, String> mapBundleLoader = null;
		List<Map<String, String>> listBundlesProcessed = null;
		try
		{
			String strUserSelect=(String) data.getSessionData("S_PURCHASE_TYPE");
			if(!util.IsNotNUllorEmpty(strUserSelect))
			{
				data.addToLog(strElementName, "Error fetching Purchase type :"+strUserSelect+". Hence setting BUY");
				strUserSelect = "BUY";
				data.setSessionData("S_PURCHASE_TYPE", strUserSelect);
			}

			setMobileNumForBundles(data, strUserSelect,util);
			mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
			util.addToLog(strElementName+" Bundle Sheet Map :"+mapBundlesConfig,util.DEBUGLEVEL);
			String strBundleType= (String) data.getSessionData("S_BUNDLE_TYPE");
			if(util.IsNotNUllorEmpty(mapBundlesConfig) && mapBundlesConfig.containsKey(strBundleType)) 
			{
				listBundlesConfig = mapBundlesConfig.get(strBundleType);
				if(util.IsNotNUllorEmpty(listBundlesConfig)&&listBundlesConfig.size()==1) 
				{
					Map<String,String> mapDetails=listBundlesConfig.get(0);
					util.addToLog(""+mapDetails);
					if(mapDetails.containsKey("Purchase_Type")&&"Static".equalsIgnoreCase(mapDetails.get("Purchase_Type")))
					{
						data.setSessionData("S_BUNDLE_AUDIO", String.valueOf(util.IsNotNUllorEmpty(mapDetails.get("Static_Audio"))?mapDetails.get("Static_Audio"):mapDetails.get("Wave ID")));
						return "StaticBundle";
					}
				}

				listBundlesNew= new ArrayList<Map<String, String>>();
				try 
				{
					if(null!=data.getSessionData("S_FREQUENT_BUNDLE"))
					{
						mapFreqBundlesAll = (Map<String, List<Map<String, String>>>) data.getSessionData("S_FREQUENT_BUNDLE");
					}
				}
				catch (Exception e) 
				{
					util.errorLog(strElementName, e);
				}

				if(util.IsNotNUllorEmpty(listBundlesConfig)) {
					for(Map<String, String> mapMultiBundle : listBundlesConfig)
					{
						if(mapMultiBundle.containsKey("Tab Name"))
						{
							listMultiBundlesConfig =mapBundlesConfig.get(mapMultiBundle.get("Tab Name"));

							if(util.IsNotNUllorEmpty(mapFreqBundlesAll)) 
							{
								listFreqBundles =  mapFreqBundlesAll.get(strBundleType);
								if(util.IsNotNUllorEmpty(listFreqBundles)) 
								{
									data.addToLog(strElementName, listFreqBundles.size()+" Multi Frequent bundles fetched:"+listFreqBundles);
									for(Map<String,String> mapBundlesLoader : listMultiBundlesConfig)
									{
										for(Map<String,String> mapBundlesFrequent : listFreqBundles)
										{
											if(mapBundlesLoader.containsKey("Product_code")&&mapBundlesLoader.get("Product_code").equalsIgnoreCase(mapBundlesFrequent.get("productCode")))
											{
												listBundlesNew.add(mapBundlesLoader);
											}
										}
									}
								}
							}
						}
					}

					data.addToLog(strElementName, listBundlesConfig.size()+" "+strBundleType+" Bundles retrieved: "+listBundlesConfig);
					data.setSessionData("S_REPEAT_COUNTER","0");

					if(util.IsNotNUllorEmpty(mapFreqBundlesAll)) 
					{
						listFreqBundles =  mapFreqBundlesAll.get(strBundleType);
						if(util.IsNotNUllorEmpty(listFreqBundles)) 
						{

							data.addToLog(strElementName, listFreqBundles.size()+" Frequent bundles fetched:"+listFreqBundles);

							for(Map<String,String> mapBundlesLoader : listBundlesConfig)
							{
								for(Map<String,String> mapBundlesFrequent : listFreqBundles)
								{
									if(mapBundlesLoader.containsKey("Product_code")&&mapBundlesLoader.get("Product_code").equalsIgnoreCase(mapBundlesFrequent.get("productCode")))
									{
										listBundlesNew.add(mapBundlesLoader);
									}
								}
							}
							if(util.IsNotNUllorEmpty(listBundlesNew))
							{
								data.addToLog(strElementName, listBundlesNew.size()+" Bundles added to Total List:"+listBundlesNew);					
								freqFlag = true;
								util.loadBundlesMenu(listBundlesNew, freqFlag, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
								strExitState = "SuccessY";
								return strExitState;
							}
						}else
						{
							data.addToLog(strElementName, strBundleType+" Frequent Bundles not available: "+listFreqBundles);
						}
					}else
					{
						data.addToLog(strElementName, "Frequent Bundles not available: "+mapFreqBundlesAll);
					}

					for(Map<String,String> mapMultiBundle:listBundlesConfig) 
					{
						if(mapMultiBundle.containsKey("Tab Name"))
						{
							listBundlesNew.addAll(listBundlesConfig);
							strExitState="SuccessMulti";
							break;
						}
					}
					if(util.IsNotNUllorEmpty(listBundlesNew))
					{
						data.addToLog(strElementName, "Multi Segment found in "+strBundleType+" Bundles. Hence exit state:"+strExitState);
						util.loadBundlesMenu(listBundlesNew, freqFlag, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
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
						util.loadBundlesMenu(listBundlesNew, freqFlag, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
						strExitState = "SuccessN";
						return strExitState;
					}


					mapPerioddata=(Map<String, Set<String>>) data.getApplicationAPI().getApplicationData("S_PERIOD_TYPES");
					if(util.IsNotNUllorEmpty(mapPerioddata))
					{
						util.addToLog(strElementName+ " Period Types Map: "+mapPerioddata,util.DEBUGLEVEL);
						setPerioddata = mapPerioddata.get(strBundleType);
						if(util.IsNotNUllorEmpty(setPerioddata))
						{
							mapBundlesProcessed= util.splitBundlePeriodwise(setPerioddata,listBundlesConfig);
							util.addToLog(strElementName+ " Periodwise bundle data: "+mapBundlesProcessed,util.DEBUGLEVEL);

							if(util.IsNotNUllorEmpty(mapBundlesProcessed))
							{

								for(String strPeriodType : setPerioddata)
								{
									listBundlesProcessed = mapBundlesProcessed.get(strPeriodType);
									if(listBundlesProcessed.size()==1)
									{
										mapBundleLoader = listBundlesProcessed.get(0);
										listBundlesNew.add(mapBundleLoader);
									}
									else if(util.IsNotNUllorEmpty(listBundlesProcessed))
									{
										mapBundleLoader = new HashMap<>();
										mapBundleLoader.put("planWav",strPeriodType.toLowerCase()+".wav");
										listBundlesNew.add(mapBundleLoader);
									}	
								}
								data.setSessionData("S_BUNDLE_PERIODWISE", mapBundlesProcessed);
								if(util.IsNotNUllorEmpty(listBundlesNew))
								{
									util.loadBundlesMenu(listBundlesNew, freqFlag, "S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC", data);
									strExitState = "SuccessN";
								}
							}
						}
						else
						{
							data.addToLog(strElementName, "Null Value Period Type  : "+ strBundleType);	
						}
					}
					else 
					{
						data.addToLog(strElementName, "Null Value Occured In Map Period Data "+mapPerioddata);
					}
				}
				else
				{
					data.addToLog(strElementName, strBundleType+" Bundles details not found :"+listBundlesConfig);
				}
			}
			else
			{
				data.addToLog(strElementName, "Bundles details not fetched Config Map:"+mapBundlesConfig+"\n Selected Bundle Type:"+strBundleType);
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
			mapBundlesConfig= null;
			listBundlesNew= null;
			mapBundlesProcessed=null;
			mapPerioddata= null;
			listBundlesConfig = null;
			mapFreqBundlesAll = null;
			listFreqBundles = null;
			setPerioddata = null;
			mapBundleLoader = null;
			listBundlesProcessed = null;
			listMultiBundlesConfig = null;
		}
		return strExitState;
	}
	private void setMobileNumForBundles(APIBase data, String strUserSelect,Utilities util) throws Exception {
		String giftBundleMobileNum = (String) data.getSessionData("S_INTERNET_SETTING_MOBILENUM");
		if(strUserSelect.equals("GIFT")&&util.IsNotNUllorEmpty(giftBundleMobileNum)){
			data.setSessionData("S_BUNDLE_NUM", giftBundleMobileNum);		
		}else{
			data.setSessionData("S_BUNDLE_NUM", data.getSessionData("S_CLI"));
		}
	}
}
