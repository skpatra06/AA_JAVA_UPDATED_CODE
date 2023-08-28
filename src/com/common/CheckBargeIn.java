package com.common;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.airtel.framework.creator.voice.VoiceElementsInterface;
import com.audium.server.AudiumException;
import com.audium.server.session.APIBase;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;

public class CheckBargeIn extends ActionElementBase
{
	@Override
	public void doAction(String elementName, ActionElementData data) throws Exception {	
		setBarginFlag(data, elementName);
	}
	
	public void setBarginFlag(APIBase data, String elementName) throws AudiumException {
		
		Map<String, List<String>> mapBarginInfo =(Map<String, List<String>>) data.getApplicationAPI().getApplicationData(VoiceElementsInterface.BARGIN_DISABLE);
		String barginFlag = (String) data.getSessionData(VoiceElementsInterface.S_BARGIN_FLAG);
		data.addToLog(elementName, " mapBarginInfo :"+mapBarginInfo +" barginFlag :"+barginFlag);
		if(null!=mapBarginInfo  && barginFlag==null){
			data.addToLog(elementName, "Setting Bargin Flag");
			for (Iterator<Map.Entry<String,List<String>>> it = mapBarginInfo.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, List<String>> entry=(Map.Entry<String,List<String>>) it.next();
				String key = entry.getKey();
				List<String> value = entry.getValue();
				String sessionValue = (String) data.getSessionData(key);
				data.addToLog(elementName, "key :"+key+" Value :"+value+ "Session Value :"+sessionValue);
				if(sessionValue!=null && value.contains(sessionValue)) {
					data.setSessionData(VoiceElementsInterface.S_BARGIN_FLAG,"false");
					data.addToLog(elementName, "Disabled Bargin");
				}

			}
		}
	}

	
	
}
