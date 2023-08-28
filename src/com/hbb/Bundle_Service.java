package com.hbb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Bundle_Service extends DecisionElementBase 
{
	
	@Override
	@SuppressWarnings("unused")
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		String strExitState = "Failure";
		boolean flagMulti = false;
		Utilities util=new Utilities(data);
		Set<String> setPeriodType= null;
		Map<String,String> hbbBundlesConfig=null;
		Map<String,Set<String>> mapPerioddata=null;
		Map<String,List<Map<String,String>>> mapBundlesPeriodwise=null;
		Map<String,List<Map<String, String>>> mapFreqBundlesAll=null;
		Map<String ,List<Map<String, String>>> mapBundlesConfig=null;
		List<Map<String, String>> listMultiBundlesConfig = null;
		List<Map<String, String>> listFreqBundles =null;
		List<Map<String, String>> listBundleConfig=null;
		List<Map<String, String>> listBundlesPeriodwise = null;
		List<Map<String, String>> listBundlesNew=null;
		try
		{
			String strBundleType = "HBB";
			String strPeriodType= "";
			String selectedOption=data.getElementData("AA_HBB_MN_0008_DM", "value");
			hbbBundlesConfig = (Map<String, String>) data.getApplicationAPI().getApplicationData("HBB_PERIOD_TYPE_MENU");
			util.addToLog(strElementName+" Bundles Menu config: "+hbbBundlesConfig,util.DEBUGLEVEL);
			if(null!=selectedOption&&util.IsNotNUllorEmpty(hbbBundlesConfig)&&hbbBundlesConfig.get("MENU_ID").equals("AA_HBB_MN_0008_DM")&&hbbBundlesConfig.containsKey(selectedOption))
			{
				strPeriodType=hbbBundlesConfig.get(selectedOption);
			}
			if(!util.IsNotNUllorEmpty(strPeriodType)) 
			{
				strPeriodType=String.valueOf(data.getSessionData("S_PER_TYPE"));
			}
			else
			{
				strPeriodType=strPeriodType.toLowerCase();
			}
			data.addToLog(strElementName, "HBB Bundle Service Selected Option : "+selectedOption+" Period Type Seg : "+strPeriodType);
			data.setSessionData("S_USER_SELECTED_PERIOD_TYPE", strPeriodType);
			mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
			try 
			{
				if(null!=data.getSessionData("S_FREQUENT_BUNDLE")){
					mapFreqBundlesAll = (Map<String, List<Map<String, String>>>) data.getSessionData("S_FREQUENT_BUNDLE");
				}
			}
			catch (Exception e) {
				util.errorLog(strElementName, e);
			}
			
			listBundlesNew= new ArrayList<Map<String, String>>();
			Map<String,String> serviceClassMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("HBB_SERVICE_CLASS");
		    String serviceID=(String) data.getSessionData("S_HBB_SERVICE_CLASS");
			String sreviceBundleName="";
		    if(util.IsNotNUllorEmpty(serviceClassMap)) 
		    {
		    	util.addToLog(strElementName+" Service Class Map : "+serviceClassMap,util.DEBUGLEVEL);
		    	Set<String> serClsID=serviceClassMap.keySet();
		    	for(String service:serClsID) 
		    	{
		    		if(util.IsNotNUllorEmpty(service)&&service.contains(serviceID))
		    		{
		    			sreviceBundleName=serviceClassMap.get(service);
		    			data.setSessionData("S_HBB_SERVICE_NAME", sreviceBundleName);
		    			data.addToLog(strElementName, "Service Class ID : "+serviceID+" - HBB Bundle Category : "+sreviceBundleName);
		    			break;
		    		}
		    	}
		    }
			if(util.IsNotNUllorEmpty(mapBundlesConfig)) 
			{
				if(mapBundlesConfig.containsKey(sreviceBundleName)) 
				{
					listBundleConfig = mapBundlesConfig.get(sreviceBundleName);
				}
				else
				{
					listBundleConfig = mapBundlesConfig.get(strBundleType);
				}
				util.addToLog(strElementName+ "HBB Bundle data : "+listBundleConfig,util.DEBUGLEVEL );
				if(util.IsNotNUllorEmpty(listBundleConfig))
				{
					for(Map<String, String> mapMultiBundle : listBundleConfig)
					{
						if("NA".equalsIgnoreCase(mapMultiBundle.get("Product_code")))
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
					data.setSessionData("S_REPEAT_COUNTER","0");
					data.addToLog(strElementName, strBundleType+" Bundles retrieved: "+listBundleConfig);
					if(util.IsNotNUllorEmpty(mapFreqBundlesAll) && mapFreqBundlesAll.containsKey(strBundleType)) 
					{
						listFreqBundles =  mapFreqBundlesAll.get(strBundleType);
						if(util.IsNotNUllorEmpty(listFreqBundles)&&!"N".equalsIgnoreCase(String.valueOf(data.getSessionData("S_FREQ_BUNDLE_CHECK_FLAG"))))
						{
							data.addToLog(strElementName, listFreqBundles.size()+" Frequent bundles fetched:"+listFreqBundles);
							for(Map<String,String> mapBundlesLoader : listBundleConfig) 
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
								util.loadBundlesMenu(listBundlesNew, true, "S_BUNDLE_"+strBundleType+"_BUY_"+strPeriodType.toUpperCase()+"_DC", data);
								strExitState = "SuccessFREQ";
								return strExitState;
							}
						}
						else
						{
							data.addToLog(strElementName, "Frequent Bundles not available: "+listFreqBundles);
						}
					}
					else
					{
						data.addToLog(strElementName, "Frequent Bundles not available: "+listFreqBundles);
					}
					/** MultiSegCheck */
					for(Map<String,String> mapMultiBundle:listBundleConfig) 
					{
						if("NA".equals(mapMultiBundle.get("Product_code")))
						{
							listBundlesNew.addAll(listBundleConfig);
							strExitState="Multi_Choice";
							break;
						}
					}
					data.addToLog(strElementName, "Multi Segment found in "+strBundleType+" Bundles. Hence exit state:"+strExitState);
					if(!util.IsNotNUllorEmpty(listBundlesNew))
					{
						mapPerioddata=(Map<String, Set<String>>) data.getApplicationAPI().getApplicationData("S_PERIOD_TYPES");
						if(util.IsNotNUllorEmpty(mapPerioddata)) 
						{
							setPeriodType= mapPerioddata.get(strBundleType)!=null&&!mapPerioddata.isEmpty()?mapPerioddata.get(strBundleType):mapPerioddata.get(sreviceBundleName);
							if(util.IsNotNUllorEmpty(setPeriodType))
							{
								mapBundlesPeriodwise = util.splitBundlePeriodwise(setPeriodType, listBundleConfig);
								data.addToLog(strElementName, "Periodwise data: "+mapBundlesPeriodwise);
								listBundlesPeriodwise = mapBundlesPeriodwise.get(strPeriodType);
								if(util.IsNotNUllorEmpty(listBundlesPeriodwise))
								{
									listBundlesNew.addAll(listBundlesPeriodwise);
								}
								strExitState = "SuccessOTH";
							}
							else
							{
								data.addToLog(strElementName, " Error fetching HBB period data: "+setPeriodType);
							}
						}
						else
						{
							data.addToLog(strElementName, " Null Value In Period Type Data Map : "+mapPerioddata);
						}
					}
					if(util.IsNotNUllorEmpty(listBundlesNew)) 
					{			
						data.addToLog(strElementName, listBundlesNew.size()+" Bundles added to Total List:"+listBundlesNew);					
						util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_BUY_"+strPeriodType.toUpperCase()+"_DC", data);
					}
				}
				else
				{
					data.addToLog(strElementName, strBundleType+" Bundles details not found: "+listBundleConfig);
				}

			}
			else
			{
				data.addToLog(strElementName, " Bundles details not found: "+mapBundlesConfig);
			}
		} 
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			mapPerioddata=null;
			mapBundlesPeriodwise=null;
			mapFreqBundlesAll=null;
			mapBundlesConfig=null;
			setPeriodType= null;
			listBundlesNew=null;
			listFreqBundles =null;
			listBundleConfig=null;
			listMultiBundlesConfig = null;
			listBundlesPeriodwise = null;
			util=null;
		}
		return strExitState;
	}
}