package com.mamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Customer_ElibilityCheck extends DecisionElementBase
{
	@Override
	public String doDecision(String strElement, DecisionElementData data) throws Exception 
	{
		String strExistState="ER";
		Utilities util=new Utilities(data);
		Map<String ,List<Map<String, String>>> mapBundleConfig=null;
		List<Map<String,String>> bundleList=null;
		List<Map<String,String>> listNewBundle=null;
		List<Map<String,String>> listBundleMap=null;
		try 
		{    
			if(util.IsValidRestAPIResponse()) 
			{
				bundleList=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_OFFER_LIST");  
				mapBundleConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigDataMap");
				listNewBundle=new ArrayList<Map<String,String>>();
				util.addToLog(strElement+ " MAP Config : "+mapBundleConfig +" : Bundle Offer List : "+bundleList,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(bundleList)) {
					if(util.IsNotNUllorEmpty(mapBundleConfig)) {
						Set<String> bundleConfigMapKey=mapBundleConfig.keySet();
						for(Map<String,String> bundleMap:bundleList) 
						{
							if(bundleMap.containsKey("productCode"))
							{
								for(String mapKey:bundleConfigMapKey)
								{
									listBundleMap=mapBundleConfig.get(mapKey);
									for(Map<String, String> mapConfig:listBundleMap) 
									{
										if(null!=bundleMap.get("productCode")&&bundleMap.get("productCode").equalsIgnoreCase(mapConfig.get("Product_code"))) 
										{
											String wavfile = mapConfig.get("Wave ID");
											if(util.IsNotNUllorEmpty(wavfile))
											{
												if(!(wavfile.contains(".wav")))
												{
													wavfile+=".wav";
												}
												bundleMap.put("planWav",wavfile);
												listNewBundle.add(bundleMap);
												strExistState="Eligible";
											}
										}
									}

								}
							}
						}	
					    if(util.IsNotNUllorEmpty(listNewBundle))
						{
							util.addToLog(strExistState+" :  List New Bundle "+listNewBundle ,util.DEBUGLEVEL);
							util.loadBundlesMenu(listNewBundle, false, "S_BUNDLE_MAMO_DC", data);
							strExistState="Eligible";
						}else {
							strExistState="Not_Eligible";
						}
					    data.setSessionData("S_REPEAT_COUNTER","0");
					}
					else 
					{
						data.addToLog(strElement, "Bundle Offer List Value is Null");
					}
					
				}else 
				{
					data.addToLog(strElement, "Bundle Config Map is Null");
				}
				String responseCode=String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
				if(!"Eligible".equalsIgnoreCase(strExistState)&&"204".equalsIgnoreCase(responseCode)) 
				{
					data.addToLog(strElement, "No Bundle Offer Is Found ");
					strExistState="Not_Eligible";
				}
			}
			else
			{
				String responseCode=String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
				if("204".equalsIgnoreCase(responseCode)) 
				{
					data.addToLog(strElement, "No Bundle Offer Is Found ");
					strExistState="Not_Eligible";
				}
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElement, e);
		}
		finally
		{
			mapBundleConfig=null;
			bundleList=null;	
			listNewBundle=null;
			listBundleMap=null;
			util=null;
		}
		return strExistState;
	}
}