package com.common.datainternetsetttings;

import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoDataRenewavleDetails extends DecisionElementBase 
{
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		Utilities util = new Utilities((APIBase)data);
		String exitState = "ER";
		List<Map<String, String>> autorenewalBundlesList = null;
		List<Map<String, String>> activeBundleList = null;
		List<Map<String, String>> dataAtoRenewalCancelationList = null;
		List<Map<String, String>> dupdataAtoRenewalCancelationList = null;
		Map<String, String> moreOption = null;
		Map<String, List<Map<String, String>>> mapBundleConfig = null;
		List<Map<String, String>> listBundleMap = null;
		try 
		{
			autorenewalBundlesList = (List<Map<String, String>>)data.getSessionData("S_SUBSCRIPTION_DATA_LIST");
			util.addToLog("SUBSCRIPTION DATA LIST  : " + autorenewalBundlesList);
			activeBundleList = new ArrayList<>();
			if (util.IsNotNUllorEmpty(autorenewalBundlesList))
			{
				data.addToLog("SUBSCRIPTION  LIST SIZE : ",""+ autorenewalBundlesList.size());
				for (Map<String, String> activeBundleMap : autorenewalBundlesList)
				{
					if (util.IsNotNUllorEmpty(activeBundleMap.get("active")) && 
							"true".equalsIgnoreCase(String.valueOf(activeBundleMap.get("active"))))
						activeBundleList.add(activeBundleMap); 
				} 
				
				/** Bundle Config Sheet Data */
				mapBundleConfig = (Map<String, List<Map<String, String>>>)data.getApplicationAPI().getApplicationData("BundleConfigData");
				util.addToLog(String.valueOf(elementName) + " MAP Config : " + mapBundleConfig + " : Bundle Offer List : " + autorenewalBundlesList, util.DEBUGLEVEL);
				if (util.IsNotNUllorEmpty(activeBundleList)) 
				{
					if (util.IsNotNUllorEmpty(mapBundleConfig)) 
					{
						listBundleMap = mapBundleConfig.get("DATA");
						for (Map<String, String> bundleMap : activeBundleList) 
						{
							if (bundleMap.containsKey("productId"))
								for (Map<String, String> mapConfig : listBundleMap)
								{
									if (util.IsNotNUllorEmpty(bundleMap.get("productId")) && ((String)bundleMap.get("productId")).equalsIgnoreCase(mapConfig.get("Product_code"))) {
										String wavfile = mapConfig.get("Wave ID");
										if (util.IsNotNUllorEmpty(wavfile))
										{
											if (!wavfile.contains(".wav"))
												wavfile = String.valueOf(wavfile) + ".wav"; 
											bundleMap.put("planWav", wavfile);
										} 
									} 
								}  
						} 
					} 
					else
					{
						data.addToLog(elementName, "Bundle config map is Null");
					} 
				} 
				else
				{
					data.addToLog(elementName, "No Active Bundle to Renewal ");
				}
				
				if (util.IsNotNUllorEmpty(activeBundleList))
				{
					dataAtoRenewalCancelationList = new ArrayList<>();
					dupdataAtoRenewalCancelationList = new ArrayList<>();
					dupdataAtoRenewalCancelationList.addAll(activeBundleList);
					data.addToLog(elementName, "Available active auto renewal List Size :" + activeBundleList.size());
					for (int i = 0; i < activeBundleList.size(); i++)
					{
						if (i == 4) 
						{
							moreOption = new HashMap<>();
							moreOption.put("MORE_OPTION", "S_DATA_AUTO_RENEWAL_MORE_OPTION_DC");
							dataAtoRenewalCancelationList.add(moreOption);
							break;
						} 
						dataAtoRenewalCancelationList.add(dupdataAtoRenewalCancelationList.remove(0));
					} 
					util.setAudioItemListForMenu("S_DATA_AUTO_RENEWAL_DETAILS", dataAtoRenewalCancelationList, "S_DATA_AUTO_RENEWAL_DC");
					data.setSessionData("S_LOADED_DATA_AUTO_RENEWAL_LIST", dataAtoRenewalCancelationList);
					data.setSessionData("S_DATA_AUTO_RENEWAL_REMAINING_LIST", dupdataAtoRenewalCancelationList);
					exitState = "Success";
				} 
			} 
		} 
		catch (Exception e) 
		{
			util.errorLog(elementName, e);
		} 
		finally
		{
			mapBundleConfig = null;
			moreOption = null;
			dupdataAtoRenewalCancelationList = null;
			dataAtoRenewalCancelationList = null;
			activeBundleList = null;
			autorenewalBundlesList = null;
			util = null;
		} 
		return exitState;
	}
}