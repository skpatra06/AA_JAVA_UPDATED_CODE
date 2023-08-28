package com.common.vas;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Vas_Services_Active extends DecisionElementBase{
	@Override
	public String doDecision(String elementName, DecisionElementData data)throws Exception {
		String exitState = "Failure";
		Utilities util = new Utilities(data);
		List<Map<String,String>>  vasServiceList=null;
		Map<String,String> currentAvailableVas= null;
		try{
			if(util.IsValidRestAPIResponse()){
				vasServiceList= (List<Map<String,String>>) data.getSessionData("S_VAS_SERVICES_ACTIVE_LIST");
				util.addToLog(elementName+ " You have currently subscribed for VAS:" +vasServiceList,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(vasServiceList))
				{
					//util.setAudioItemListForAudio("S_VAS_SERVICES_ACTIVE_LIST",(List<Map<String, String>>) vasServiceList.get(0),"S_VAS_SERVICES_ACTIVE_LIST_DC");
					currentAvailableVas=vasServiceList.get(0);
					if(util.IsNotNUllorEmpty(currentAvailableVas))
					{
						data.setSessionData("S_CURRENT_ACTIVE_VASNAME",currentAvailableVas.get("vasName"));
						data.setSessionData("S_CURRENT_VAS_ENDDATE",currentAvailableVas.get("vasEndDate"));
						exitState = "SuccessY";
					}
				}
				else
				{
					exitState = "SuccessNull";
					data.addToLog(elementName, "VAS success with Null");
				}

			}
			else{
				data.addToLog(elementName, "API Response is failed");
			}
		}catch(Exception e){
			util.errorLog(elementName, e);
		}finally{
			util = null;
			vasServiceList=null;
			currentAvailableVas= null;
		}

		return exitState;
	}
}
