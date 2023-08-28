package com.common.buygiftbundles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Bundle_Service_GetBundles extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data)
			throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		Map<String,String> mapMoreOption = null;

		try
		{
			String strUserSelect="";
			String strBundleType= (String) data.getSessionData("S_BUNDLE_TYPE");
			String strUserInput= data.getElementData("AA_PRP_XX_MN_0041_DM", "value");

			if("2".equals(strUserInput)){
				strUserSelect = "GIFT";
				data.addToLog(strElementName, "User selected "+strUserInput+": Gift Bundle Option");
			}else {
				strUserSelect = "BUY";
				data.addToLog(strElementName, "User selected "+strUserInput+":Buy Bundle Option");
			}
			List<Map<String,String>> bundleDetails=(List<Map<String, String>>) data.getApplicationAPI().getApplicationData("S_BUNDLE_PREPAID_DETAILS");
			Map<String,List<Map<String,String>>> mapBundles=util.splitJsonData(bundleDetails, "split.productCode[DATA,SMS,VOICE,INT,ROAMING]","BUNDLE_CODES");
			data.addToLog(strElementName, mapBundles.size()+" Bundle Prepaid Details fetched:"+mapBundles);

			if(util.IsNotNUllorEmpty(mapBundles))
			{
				List<Map<String, String>> listBundle= mapBundles.get(strBundleType);
				if(util.IsNotNUllorEmpty(listBundle))
				{
					data.addToLog(strElementName, strBundleType+" Bundles retrieved: "+listBundle);

					List<Map<String, String>> listBundleNew= new ArrayList<Map<String, String>>();
					Map<String,List<Map<String, String>>> mapFreqBundlesAll = (Map<String, List<Map<String, String>>>) data.getSessionData("S_FREQUENT_BUNDLE");


					if(util.IsNotNUllorEmpty(mapFreqBundlesAll)) {
						List<Map<String, String>> listFreqBundle =  mapFreqBundlesAll.get(strBundleType);
						if(util.IsNotNUllorEmpty(listFreqBundle)) {

							data.addToLog(strElementName, listFreqBundle.size()+" Frequent bundles fetched:"+listFreqBundle);

							int matchCounter=0;
							//listBundleNew.addAll( listFreqBundle );
							for(Map<String,String> mapBundlesLoader : listBundle)
							{
								for(Map<String,String> mapBundlesFrequent : listFreqBundle)
								{
									if(mapBundlesLoader.get("productCode").equalsIgnoreCase(mapBundlesFrequent.get("productcode")))
									{
										listBundleNew.add(mapBundlesLoader);
									}
									else
									{
										data.addToLog(strElementName, "Frequent bundle not in the List:"+mapBundlesFrequent);
									}
								}
							}
							data.addToLog(strElementName, listBundleNew.size()+" Frequent bundles added to Total List:"+listBundleNew);

							for(Map<String,String> mapBundlesLoader : listBundle)
							{
								matchCounter=0;
								for(Map<String,String> mapBundlesFrequent : listFreqBundle)
								{
									if(matchCounter==listFreqBundle.size()-1)
									{
										listBundleNew.add(mapBundlesLoader);
									}
									if(mapBundlesLoader.get("productCode").equalsIgnoreCase(mapBundlesFrequent.get("productcode")))
									{
										continue;
									}
									else 
									{
										matchCounter++;
									}

								}
							}
							data.addToLog(strElementName, "Rest of the bundles added to Total List:"+listBundleNew);

						}else
						{
							data.addToLog(strElementName, strBundleType+" Frequent Bundles not available: "+listFreqBundle);
							listBundleNew.addAll(listBundle) ;
						}
					}else
					{
						data.addToLog(strElementName, "Frequent Bundles not available: "+mapFreqBundlesAll);
						listBundleNew.addAll(listBundle) ;
					}

					List<Map<String, String>> listBundleDup= new ArrayList<>();
					listBundleDup.addAll(listBundleNew);
					data.addToLog(strElementName, "Total List size:"+listBundleNew.size());

					List<Map<String,String>> listBundleDetails= new ArrayList<Map<String,String>>();
					int pressCount = 1;
					for(;pressCount<=listBundleNew.size();)
					{
						if(pressCount==5) {
							mapMoreOption=new HashMap<String, String>();
							mapMoreOption.put("MORE_OPTION", "S_BUNDLE_MORE_OPTION_DC");
							data.addToLog(strElementName, "More than 4 bundles, Hence loading more option"+mapMoreOption);
							listBundleDetails.add(mapMoreOption);
							break;
						}
						listBundleDetails.add(listBundleDup.remove(0));
						pressCount++;
						
					}

					data.addToLog(strElementName, "Bundle details to be loaded : "+listBundleDetails);
					util.setAudioItemListForMenu("S_BUNDLE_DETAILS", listBundleDetails,"S_BUNDLE_"+strBundleType+"_"+strUserSelect+"_DC");
					data.setSessionData("S_BUNDLE_TOTAL", listBundleNew);
					data.setSessionData("S_BUNDLE_REMAINING", listBundleDup);
					strExitState = "Success";
				}
				else
				{
					data.addToLog(strElementName, strBundleType+" Bundles details not found: "+listBundle);
				}
			}
			else
			{
				data.addToLog(strElementName, "Bundles details not fetched: "+mapBundles);
			}

		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		return strExitState;
	}
	
}
