package com.common.vas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Vas_Unsubscribe_CheckUserchoice extends DecisionElementBase
{ 

	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState ="";
		Utilities util=new Utilities(data);
		Map<String,String> vasUnsub=null;
		Map<String,String> moreOption=null;
		Map<String,String> vadPlanIdsMap=null;
		List<Map<String,String>> listNewVas=null;
		List<Map<String, String>> vasUnsubList=null;
		List<Map<String,String>> availableActiveList=null;
		List<Map<String, String>> vaUnsubRemainingList=null;
		List<Map<String,String>> totalVasUnsubsLoadedList=null;
		List<Map<String,String>> dupVaslist=null;
		try{
			String slectedOpt=util.getMenuElementValue();
			availableActiveList=(List<Map<String, String>>) data.getSessionData("S_VAS_SERVICES_ACTIVE_LIST");
			vasUnsubList=(List<Map<String, String>>) data.getSessionData("S_LOADED_VAS_UNSUB_LIST");
			vadPlanIdsMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("VAS_PLAN_ID");
			util.addToLog(elementName+" VAS UnsubList : "+vasUnsubList,util.DEBUGLEVEL);
			data.addToLog("VAS UNSUBSCRIBE SELECTED OPTION ",""+ slectedOpt);
			if(util.IsNotNUllorEmpty(vasUnsubList)) {
				int userChoice= Integer.parseInt(slectedOpt);
				if(userChoice<=4) {
					vasUnsub=vasUnsubList.get(userChoice-1);
					if(util.IsNotNUllorEmpty(availableActiveList)&&vadPlanIdsMap!=null&&vadPlanIdsMap.containsKey("periodType")&&!"Y".equalsIgnoreCase(String.valueOf(data.getSessionData("S_PERIOD_WISE_CHECK_FLAG")))) {
						String vasName=String.valueOf(vasUnsub.get("vasId"));
						String periodTypeValues[]=String.valueOf(vadPlanIdsMap.get("periodType")).split("\\|");
						for(Map<String,String> vasMap:availableActiveList) {
							if(vasName.equalsIgnoreCase(vasMap.get("vasId"))) {
								for(String periodType:periodTypeValues) {
									if(periodType.equalsIgnoreCase(vasMap.get("subVasId"))) {
										if(listNewVas==null) {
											listNewVas=new ArrayList<Map<String,String>>();
										}
										vasMap.put("vasPlanWav", periodType.toLowerCase()+".wav");
										listNewVas.add(vasMap);
									}
								}
							}
						}  
					}
					if(util.IsNotNUllorEmpty(listNewVas)) {
						util.setAudioItemListForMenu("S_UNSUBSCRIBE_VAS_DETAILS", listNewVas,"S_VAS_PERIOD_TYPE_DC");
						data.setSessionData("S_PERIOD_WISE_CHECK_FLAG","Y");
						data.setSessionData("S_LOADED_VAS_UNSUB_LIST", listNewVas);
						data.setSessionData("S_REPEAT_COUNTER","0");
						exitState ="5";
					}else {
						data.setSessionData("S_UNSUB_SUB_VAS_ID", vasUnsub.get("subVasId"));
						data.setSessionData("S_UNSUB_SUB_VENDOR_ID",vasUnsub.get("subVendorId"));
						data.setSessionData("S_UNSUB_VASID",vasUnsub.get("vasId"));
						data.setSessionData("S_UNSUB_VENDOR",data.getSessionData("S_ACTIVE_SERVICE_VENDOR"));
						data.setSessionData("S_UNSUB_ACTIVATE","false");
						data.setSessionData("S_UNSUB_SUB_AGENT_ID",vasUnsub.get("agentId"));
						data.setSessionData("S_UNSUB_SUB_AUTORENEW",vasUnsub.get("autorenewable"));
						data.setSessionData("S_UNSUB_SUB_CHANNEL",vasUnsub.get("subscriptionChannel"));
						data.setSessionData("S_UNSUB_SUB_LANG",vasUnsub.get("language"));
						data.setSessionData("S_UNSUB_VASNAME",vasUnsub.get("vasName"));
						data.setSessionData("S_UNSUB_VAS_AMT", vasUnsub.get("activationAmount"));
						data.setSessionData("S_REPEAT_COUNTER","0");
						exitState = ""+userChoice;
					}
				}
				else if(5==userChoice)
				{
					vaUnsubRemainingList= (List<Map<String, String>>) data.getSessionData("S_VAS_UNSUB_REMAINING_LIST");
					totalVasUnsubsLoadedList= new ArrayList<Map<String,String>>();
					dupVaslist=new ArrayList<Map<String,String>>();
					dupVaslist.addAll(vaUnsubRemainingList);
					data.addToLog(elementName, " Remaining Available Vas UnSubscribe List Size : "+vaUnsubRemainingList.size());
					for(int i=0;i<vaUnsubRemainingList.size();i++ )
					{
						if(i==4) {
							moreOption=new HashMap<String, String>();
							moreOption.put("MORE_OPTION", "S_VAS_MORE_OPTION_DC");
							totalVasUnsubsLoadedList.add(moreOption);
							break;
						}
						totalVasUnsubsLoadedList.add(dupVaslist.remove(0));
					}
					util.setAudioItemListForMenu("S_UNSUBSCRIBE_VAS_DETAILS", totalVasUnsubsLoadedList,"S_UNSUBSCRIBE_VAS_DC");
					data.setSessionData("S_LOADED_VAS_UNSUB_LIST", totalVasUnsubsLoadedList);
					data.setSessionData("S_VAS_UNSUB_REMAINING_LIST", dupVaslist);
					exitState = "5";
				}else {
					data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
					exitState = "6";
				}
			}else {
				data.addToLog(elementName,"Null Values In Vas Unsubscribe List");
			}
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			moreOption=null;
			vaUnsubRemainingList=null;
			totalVasUnsubsLoadedList=null;
			dupVaslist=null;
			vadPlanIdsMap=null;
			availableActiveList=null;
			vasUnsubList=null;
			util=null;
		}
		return exitState;
	}
}