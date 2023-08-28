package com.common.vas;

import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Vas_Subscribe_CheckUserchoice extends DecisionElementBase
{
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "6";
		Utilities util = new Utilities((APIBase)data);
		Map<String, String> moreOption = null;
		Map<String, String> vasPlanIdsMap = null;
		Map<String, String> mapAvailable = null;
		List<Map<String, String>> availableVasList = null;
		List<Map<String, String>> avaiListPack = null;
		List<Map<String, String>> listNewVas = null;
		List<Map<String, String>> remainingVasList = null;
		List<Map<String, String>> totalAvailableList = null;
		List<Map<String, String>> dupVasList = null;
		try 
		{
			availableVasList = (List<Map<String, String>>)data.getSessionData("S_LOADED_SUB_VAS_LIST");
			avaiListPack = (List<Map<String, String>>)data.getSessionData("S_AVAILABLE_VAS_LIST");
			String previousMenuElement = util.getMenuElementValue();
			util.addToLog(" Available VAS List  : "+ availableVasList,util.DEBUGLEVEL);
			data.addToLog(elementName, "VAS User Selected Option : "+previousMenuElement);
			if(util.IsNotNUllorEmpty(availableVasList)) {
				int vasSubscribeSelectedOption = Integer.parseInt(previousMenuElement);
				if (vasSubscribeSelectedOption <= 4) 
				{
					mapAvailable = availableVasList.get(vasSubscribeSelectedOption - 1);
					vasPlanIdsMap = (Map<String, String>)data.getApplicationAPI().getApplicationData("VAS_PLAN_ID");
					if (null!=mapAvailable&&mapAvailable.containsKey("vasType") && "Static".equalsIgnoreCase(mapAvailable.get("vasType")))
					{
						data.setSessionData("S_VAS_STATIC_AUDIO", mapAvailable.get("vasAudio"));
						data.setSessionData("S_REPEAT_COUNTER", "0");
						return "StaticVas";
					} 
					if (util.IsNotNUllorEmpty(avaiListPack) &&vasPlanIdsMap != null && vasPlanIdsMap.containsKey("periodType") && !"Y".equalsIgnoreCase(String.valueOf(data.getSessionData("S_PERIOD_WISE_CHECK_FLAG"))))
					{
						String vasName = String.valueOf(mapAvailable.get("vasId"));
						String[] periodTypeValues = String.valueOf(vasPlanIdsMap.get("periodType")).split("\\|");
						for (Map<String, String> vasMap : avaiListPack) 
						{
							if (vasName.equalsIgnoreCase(vasMap.get("vasId"))) 
							{
								for(String periodType:periodTypeValues) {
									if (periodType.equalsIgnoreCase(vasMap.get("subVasId")))
									{
										if (listNewVas == null) 
										{
											listNewVas = new ArrayList<>();
										}
										vasMap.put("vasPlanWav", String.valueOf(periodType.toLowerCase()) + ".wav");
										listNewVas.add(vasMap);
									} 

								}
							}

						} 
					} 
					if (util.IsNotNUllorEmpty(listNewVas))
					{
						/** VAS PeriodWise Split  */
						util.setAudioItemListForMenu("S_AVAILABLE_VAS_DETAILS", listNewVas, "S_VAS_PERIOD_TYPE_DC");
						data.setSessionData("S_PERIOD_WISE_CHECK_FLAG", "Y");
						data.setSessionData("S_LOADED_SUB_VAS_LIST", listNewVas);
						data.setSessionData("S_REPEAT_COUNTER", "0");
						exitState = "5";
					}
					else
					{
						data.setSessionData("S_SUB_VAS_ID", mapAvailable.get("subVasId"));
						data.setSessionData("S_SUB_VENDORID", mapAvailable.get("subVendorId"));
						data.setSessionData("S_SUB_VASID", mapAvailable.get("vasId"));
						data.setSessionData("S_SUB_VENDOR", data.getSessionData("S_AVAILABLE_SERVICE_VENDOR"));
						data.setSessionData("S_SUB_VAS_AUTORENEW", mapAvailable.get("autorenewable"));
						data.setSessionData("S_SUB_VAS_LANG", mapAvailable.get("language"));
						data.setSessionData("S_SUB_VASNAME", mapAvailable.get("vasName"));
						data.setSessionData("S_SUB_VAS_AMT", mapAvailable.get("activationAmount"));
						data.setSessionData("S_SUB_ACTIVATE", "true");
						data.setSessionData("S_REPEAT_COUNTER", "0");
						exitState=""+vasSubscribeSelectedOption;
					} 
				} 
				else if (5 == vasSubscribeSelectedOption)
				{
					remainingVasList = (List<Map<String, String>>)data.getSessionData("S_REMAINING_SUB_VAS_LIST");
					totalAvailableList = new ArrayList<>();
					dupVasList = new ArrayList<>();
					dupVasList.addAll(remainingVasList);
					data.addToLog(elementName, " Remaining Available Vas Subscribe List Size : " + remainingVasList.size());
					for (int i = 0; i < remainingVasList.size(); i++)
					{
						if (i == 4)
						{
							moreOption = new HashMap<>();
							moreOption.put("MORE_OPTION", "S_VAS_MORE_OPTION_DC");
							totalAvailableList.add(moreOption);
							break;
						} 
						totalAvailableList.add(dupVasList.remove(0));
					} 
					util.setAudioItemListForMenu("S_AVAILABLE_VAS_DETAILS", totalAvailableList, "S_AVAILABLE_VAS_DC");
					data.setSessionData("S_LOADED_SUB_VAS_LIST", totalAvailableList);
					data.setSessionData("S_REMAINING_SUB_VAS_LIST", dupVasList);
					exitState = "5";
				} 
				else
				{

					data.setSessionData("DTMF_KEYPRESS", data.getSessionData("REPEAT_DTMF_KEYPRESS"));
					exitState = "6";
				} 
				//util.addPreviousMenu(data);
			}else {
				data.addToLog(elementName, "Null Value IN VAS Available List ");
			}
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			dupVasList = null;
			totalAvailableList = null;
			remainingVasList = null;
			availableVasList = null;
			listNewVas = null;
			moreOption = null;
			util = null;
		} 
		return exitState;
	} 
}