package com.common.vas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Vas_Unsubscription_Details extends DecisionElementBase{

	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "Failure";
		List<Map<String,String>> activeVasListDetails=null;
		List<Map<String,String>> activeVasList=null;
		List<Map<String, String>> vasUnsubscribeList=null;
		List<Map<String, String>> dupVasUnsubscribe=null;
		List<String> vasSubList=null;
		Map<String,String> moreOption=null;
		Map<String,String> vadPlanIdsMap=null;
		Utilities util=new Utilities(data);
		try
		{
			if(util.IsValidRestAPIResponse()){
				vadPlanIdsMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("VAS_PLAN_ID");
				activeVasListDetails = (List<Map<String, String>>) data.getSessionData("S_VAS_SERVICES_ACTIVE_LIST");
				util.addToLog("VAS ACTIVE  LIST  : "+activeVasListDetails+" VAS PLAN ID MAP : "+vadPlanIdsMap,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(activeVasListDetails)&&util.IsNotNUllorEmpty(vadPlanIdsMap)){
					data.addToLog("VAS ACTIVE  LIST SIZE : ",""+activeVasListDetails.size());
					activeVasList=new ArrayList<Map<String,String>>();
					vasSubList=new ArrayList<>();
					for(Map<String,String> vasDetailsMap:activeVasListDetails) {
						if(null!=vasDetailsMap.get("vasId")&&vadPlanIdsMap.containsKey(String.valueOf(vasDetailsMap.get("vasId")))) {
							if(!vasSubList.contains(String.valueOf(vasDetailsMap.get("vasId")))) {
								vasDetailsMap.put("vasPlanWav",vadPlanIdsMap.get(String.valueOf(vasDetailsMap.get("vasId"))));
								vasSubList.add(String.valueOf(vasDetailsMap.get("vasId")));
								activeVasList.add(vasDetailsMap);	
							}
						}

					}
					if(!util.IsNotNUllorEmpty(activeVasList)) 
					{
						data.addToLog(elementName,"No Matching Vas ID : No Active Service To Unsubscribe ");
					}
					if(util.IsNotNUllorEmpty(activeVasList)) 
					{
						if(activeVasList.size()==1)
						{
							util.setAudioItemListForMenu("S_UNSUBSCRIBE_VAS_DETAILS", activeVasList,"S_UNSUBSCRIBE_VAS_DC");
							data.setSessionData("S_LOADED_VAS_UNSUB_LIST", activeVasListDetails);
							exitState="Success1Value";
						}else {
							vasUnsubscribeList= new ArrayList<Map<String,String>>();
							dupVasUnsubscribe= new ArrayList<Map<String,String>>();
							dupVasUnsubscribe.addAll(activeVasList);
							data.addToLog(elementName,"Available Vas UnSubscribe List Size :"+activeVasListDetails.size());
							for(int i=0;i<activeVasList.size();i++)
							{
								if(i==4) {
									moreOption=new HashMap<String, String>();
									moreOption.put("MORE_OPTION", "S_VAS_MORE_OPTION_DC");
									vasUnsubscribeList.add(moreOption);
									break;
								}
								vasUnsubscribeList.add(dupVasUnsubscribe.remove(0));
							}
							util.setAudioItemListForMenu("S_UNSUBSCRIBE_VAS_DETAILS", vasUnsubscribeList,"S_UNSUBSCRIBE_VAS_DC");
							data.setSessionData("S_LOADED_VAS_UNSUB_LIST", vasUnsubscribeList);
							data.setSessionData("S_VAS_UNSUB_REMAINING_LIST", dupVasUnsubscribe);
							exitState = "SuccessMoreThan1";
						}
					}
				}
			}
		}
		catch (Exception e){
			util.errorLog(elementName, e);
		}finally {
			dupVasUnsubscribe=null;
			vasUnsubscribeList=null;
			vadPlanIdsMap=null;
			moreOption=null;
			activeVasList=null;
			activeVasListDetails=null;
			util=null;
		}
		return exitState;
	}
}