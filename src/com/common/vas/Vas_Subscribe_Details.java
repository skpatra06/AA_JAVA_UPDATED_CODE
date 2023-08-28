package com.common.vas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Vas_Subscribe_Details extends DecisionElementBase {

	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState = "Failure";	
		Map<String,String> moreOption=null;
		Map<String,String> mapVasStatic=null;
		Map<String,String> vadPlanIdsMap=null;
		Map<String,String> mapVasStaticLoader = null;
		List<String> vasSubList=null;
		List<Map<String,String>> vasList=null;
		List<Map<String,String>> vasListDetails=null;
		List<Map<String,String>> dubVasList=null;
		List<Map<String, String>> availableVasList=null;
		Utilities util = new Utilities(data);
		try {
			vasListDetails=new ArrayList<Map<String,String>>();
			if(util.IsValidRestAPIResponse()){	
				vadPlanIdsMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("VAS_PLAN_ID");
				availableVasList = (List<Map<String, String>>) data.getSessionData("S_AVAILABLE_VAS_LIST");
				util.addToLog(elementName +" Available Vas Details : " +availableVasList+"Vas Plan ID Map : "+vadPlanIdsMap,util.DEBUGLEVEL);	
				if(util.IsNotNUllorEmpty(availableVasList))
				{
					data.addToLog(elementName," Available Vas Subscribe List Size : "+availableVasList.size());
					if(util.IsNotNUllorEmpty(vadPlanIdsMap)) {
						vasSubList=new ArrayList<>();
						for(Map<String,String> vasDetailsMap:availableVasList) 
						{
							if(null!=vasDetailsMap.get("vasId")&&vadPlanIdsMap.containsKey(String.valueOf(vasDetailsMap.get("vasId")))) 
							{
								if(!vasSubList.contains(String.valueOf(vasDetailsMap.get("vasId")))) {
									vasDetailsMap.put("vasPlanWav",vadPlanIdsMap.get(String.valueOf(vasDetailsMap.get("vasId"))));
									vasSubList.add(String.valueOf(vasDetailsMap.get("vasId")));
									vasListDetails.add(vasDetailsMap);	
								}
							}

						}
					}else {
						data.addToLog(elementName,"Null Value Occurs in VAS Plan Config ");
					}
				}else{
					data.addToLog(elementName,"Null Value  Occurs in AvailableVasList");
				}
			}else{
				data.addToLog(elementName, "API Response is failed");
			}

			String vasStaticFlag = (String) data.getSessionData("S_STATIC_VAS_FLAG");
			mapVasStatic = (Map<String, String>) data.getApplicationAPI().getApplicationData("VAS_STATIC_PLANS");
			String strVasStatic = (String) data.getApplicationAPI().getApplicationData("VAS_LIST_ORDER");
			data.addToLog(elementName, "Static flag is "+vasStaticFlag+", Static VAS: "+mapVasStatic);
			if(util.IsNotNUllorEmpty(vasStaticFlag)&&"true".equalsIgnoreCase(vasStaticFlag)&&util.IsNotNUllorEmpty(mapVasStatic)&&util.IsNotNUllorEmpty(strVasStatic))
			{
				String[] arrVasStatic = strVasStatic.split(",");
				for(String vasName: arrVasStatic)
				{
					String strVasAudio = mapVasStatic.get(vasName);
					String arrVasAudios[]=strVasAudio.split("\\|");
					if(util.IsNotNUllorEmpty(arrVasAudios[0])&&util.IsNotNUllorEmpty(arrVasAudios[1]))
					{
						mapVasStaticLoader = new HashMap<String,String>();
						arrVasAudios[0]+=arrVasAudios[0].contains(".wav")?"":".wav";
						arrVasAudios[1]+=arrVasAudios[1].contains(".wav")?"":".wav";
						mapVasStaticLoader.put("vasName", vasName);
						mapVasStaticLoader.put("vasPlanWav",arrVasAudios[0]);
						mapVasStaticLoader.put("vasAudio",arrVasAudios[1]);
						mapVasStaticLoader.put("vasType", "Static");
						vasListDetails.add(mapVasStaticLoader);
					}
					else
					{
						data.addToLog(elementName, "wav file missing for "+vasName);
					}
				}

			}

			if(util.IsNotNUllorEmpty(vasListDetails)) 
			{
				dubVasList=new ArrayList<Map<String,String>>();
				vasList= new ArrayList<Map<String,String>>();
				dubVasList.addAll(vasListDetails);
				for(int i=0;i<vasListDetails.size();i++)
				{
					if(i==4) 
					{
						moreOption=new HashMap<>();
						moreOption.put("MORE_OPTION","S_VAS_MORE_OPTION_DC");
						vasList.add(moreOption);
						break;
					}
					vasList.add(dubVasList.remove(0));
				}
				util.setAudioItemListForMenu("S_AVAILABLE_VAS_DETAILS",vasList, "S_AVAILABLE_VAS_DC");
				data.setSessionData("S_LOADED_SUB_VAS_LIST", vasList);
				data.setSessionData("S_REMAINING_SUB_VAS_LIST",dubVasList);
				exitState="Success";
			}else{
				data.addToLog(elementName,"No Matching Vas ID");
			}
		}catch(Exception e) {
			util.errorLog(elementName, e);
		}finally {
			dubVasList=null;
			availableVasList=null;
			vasListDetails=null;
			vasSubList=null;
			vasList=null;
			vadPlanIdsMap=null;
			util=null;
		}
		return exitState;
	}

}
