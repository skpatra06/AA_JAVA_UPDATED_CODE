package com.common.buygiftbundles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.airtel.framework.loader.common.ApplicationStartLoader;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.common.ATCDataLoader;
import com.util.Utilities;

public class BundleHitRespCheck extends DecisionElementBase {


	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "Failure";
		Utilities util = new Utilities(data);
		Map<String,List<Map<String,String>>> mapBundles= null;
		Map<String ,List<Map<String, String>>> mapBundlesConfig= null;
		Map<String,List<String>> mapBundleCodes = null;
		Map<String,Set<String>> mapPeriodTypes = null;
		Map<String ,List<Map<String, String>>> mapUpdatedConfig= null;
		List<String> listCodes = null;
		List<Map<String,String>> listBundlesdata=null;
		List<Map<String, String>> listUpdatedConfig= null;
		Set<String> setPeriodType = null;
		Set<String> setBundlesKey = null;
		ATCDataLoader atcExcelLoader=null;
		try{

			if(util.IsValidRestAPIResponse()) 
			{
				listBundlesdata=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_PREPAID_DETAILS");
				util.addToLog(strElementName+" Bundles API data: "+listBundlesdata, util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(listBundlesdata)) 
				{
					data.addToLog(strElementName, "Bundle Hit successful. Updating current Date and Prepaid bundles to App data. Bundles received: "+listBundlesdata);
					data.getApplicationAPI().setApplicationData("S_BUNDLE_API_HIT_DATE", util.getCurrDateInStr("ddMMyy"));
					data.getApplicationAPI().setApplicationData("S_BUNDLE_PREPAID_DETAILS",listBundlesdata);
					exitState = "Success";
					//mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
					mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigDataMap");
					if(!util.IsNotNUllorEmpty(mapBundlesConfig)) 
					{
						data.addToLog(strElementName, "Bundle Data sheet is not available, so invoking bundle sheet loading method");
						atcExcelLoader = new ATCDataLoader(data);
						atcExcelLoader.bundleSheetLoad("c:\\airtel\\XL-ATC");
						mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigDataMap");
					}
					if(util.IsNotNUllorEmpty(mapBundlesConfig)) 
					{
						mapBundleCodes = new HashMap<String,List<String>>();
						mapPeriodTypes = new HashMap<String,Set<String>>();
						util.addToLog(strElementName+" Bundle config Map:"+mapBundlesConfig,util.DEBUGLEVEL);
						setBundlesKey = mapBundlesConfig.keySet();
						for (String strBundleType : setBundlesKey)
						{
							setPeriodType = new LinkedHashSet<String>();
							listCodes = new ArrayList<String>();
							for (Map<String, String> mapBundleConfig : mapBundlesConfig.get(strBundleType)) 
							{
								if(mapBundleConfig.containsKey("Wave ID"))
								{
									String wavFile = mapBundleConfig.get("Wave ID");

									if(util.IsNotNUllorEmpty(wavFile))
									{
										wavFile+=wavFile.contains(".wav")?"":".wav";
										mapBundleConfig.put("Wave ID",wavFile);
										mapBundleConfig.put("planWav",wavFile);
									}
									if(mapBundleConfig.containsKey("Static_Audio"))
									{
										String strStaticAud=String.valueOf(mapBundleConfig.get("Static_Audio"));
										strStaticAud+=strStaticAud.contains(".wav")?"":".wav";
										mapBundleConfig.put("Static_Audio",strStaticAud);
									}
								}

								if(mapBundleConfig.containsKey("Product_code") && mapBundleConfig.containsKey("period_type")) 
								{
									String strProductCode=mapBundleConfig.get("Product_code");
									String periodType = mapBundleConfig.get("period_type");	
									if(util.IsNotNUllorEmpty(strProductCode)&&!"NA".equalsIgnoreCase(strProductCode))
									{
										listCodes.add(strProductCode);
									}
									if(util.IsNotNUllorEmpty(periodType)&&!"NA".equalsIgnoreCase(periodType))
									{
										setPeriodType.add(periodType.toLowerCase());
									}
								}else
								{
									data.addToLog(strElementName, "Excel sheet does not contain product code or period type for "+strBundleType+" Bundle: "+mapBundleConfig);
								}
							}
							data.addToLog("Bundle Code retrieval", strBundleType+" Bundle codes: "+listCodes+"\nPeriodTypes: "+setPeriodType);
							if(util.IsNotNUllorEmpty(listCodes)) {
								mapBundleCodes.put(strBundleType.toString(), listCodes);
							}
							if(util.IsNotNUllorEmpty(setPeriodType))
							{
								mapPeriodTypes.put(strBundleType.toString(), setPeriodType);
							}
							else {
								data.addToLog(strElementName, "No Data found for Bundle Type :"+strBundleType);
							}

						}
						if(util.IsNotNUllorEmpty(mapBundleCodes)) {
							data.getApplicationAPI().setApplicationData("BUNDLE_CODES", mapBundleCodes);
						}
						if(util.IsNotNUllorEmpty(mapPeriodTypes))
						{
							data.getApplicationAPI().setApplicationData("S_PERIOD_TYPES", mapPeriodTypes);
						}

						mapBundles=util.splitJsonData(listBundlesdata, "split.productCode"+mapBundlesConfig.keySet(),"BUNDLE_CODES");
						util.addToLog(strElementName+" Bundle Data after segregated with DATA,SMS,VOICE,INT,ROAMING,COMBO :"+mapBundles,util.DEBUGLEVEL);
						if(util.IsNotNUllorEmpty(mapBundles))
						{
							mapUpdatedConfig = new HashMap<String, List<Map<String,String>>>();
							for (String strBundleType : setBundlesKey)
							{
								listUpdatedConfig = new ArrayList<Map<String,String>>();
								for(Map<String, String> mapBundleInfo : mapBundlesConfig.get(strBundleType))
								{
									if(mapBundleInfo.containsKey("Product_code")&&"NA".equalsIgnoreCase(mapBundleInfo.get("Product_code")))
									{
										listUpdatedConfig.add(mapBundleInfo);
										continue;
									}
									if(mapBundles.get(strBundleType)==null) {
										continue;
									}
									for (Map<String, String> mapBundleData:mapBundles.get(strBundleType)) 
									{
										if(mapBundleInfo.containsKey("Product_code")&&mapBundleInfo.get("Product_code").equalsIgnoreCase(mapBundleData.get("productCode")))
										{
											mapBundleInfo.put("amount", String.valueOf(mapBundleData.get("amount")));
											listUpdatedConfig.add(mapBundleInfo);
										}

									}
								}
								mapUpdatedConfig.put(strBundleType, listUpdatedConfig);
							}
							if(util.IsNotNUllorEmpty(mapUpdatedConfig)) {
								util.addToLog(strElementName+ " Filtered Bundle Map Configuration is setted as Application Data Successfully ...  "+mapUpdatedConfig,util.DEBUGLEVEL);
								data.getApplicationAPI().setApplicationData("BundleConfigData", mapUpdatedConfig);
							}else {
								data.addToLog(strElementName, " Null Value In The Bundle Map Congiguration ....  ");
							}
						}
						else
						{
							data.addToLog(strElementName, "Error processing bundles: "+mapBundles);
						}
					}
					else
					{
						data.addToLog(strElementName, "Error fetching Sheet bundles: "+mapBundlesConfig);
					}
				}
				else
				{
					data.addToLog(strElementName, "Error fetching API bundles: "+listBundlesdata);
				}
			}
			else
			{
				data.addToLog(strElementName, "Bundles API hit failed");
			}
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
			listBundlesdata=null;
			mapBundlesConfig= null;
			mapBundleCodes = null;
			listCodes = null;
			mapPeriodTypes = null;
			setPeriodType = null;
			setBundlesKey = null;
			atcExcelLoader=null;
		}
		return exitState;

	}
}
