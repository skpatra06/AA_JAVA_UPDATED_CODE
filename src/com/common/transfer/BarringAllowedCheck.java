package com.common.transfer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BarringAllowedCheck extends DecisionElementBase {
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String strExitState = "BarringCheck";
		Utilities util=new Utilities(data);
		Map<String, List<String>> barringAllowedCheck=null;
		try {
			barringAllowedCheck =(Map<String, List<String>>) data.getApplicationAPI().getApplicationData("BARRING_NOT_ALLOWED");
			data.addToLog(elementName, " mapChargeInfo :"+barringAllowedCheck);
			if(util.IsNotNUllorEmpty(barringAllowedCheck)){
				for (Iterator<Map.Entry<String,List<String>>> it = barringAllowedCheck.entrySet().iterator(); it.hasNext();) 
				{
					Map.Entry<String, List<String>> entry=(Map.Entry<String,List<String>>) it.next();
					String key = entry.getKey();
					List<String> value = entry.getValue();
					String sessionValue = (String) data.getSessionData(key);
					data.addToLog(elementName, "key :"+key+"Value :"+value+ "Session Value :"+sessionValue);
					if(sessionValue!=null && value.contains(sessionValue))
					{
						strExitState= "NoBarringCheck";
						break;
					}
				}
			}else {
				data.addToLog(elementName, "Null Value Occurs In Barring Config");
			}
		}catch (Exception e){
			util.errorLog(elementName, e);
		}finally{
			barringAllowedCheck=null;
			util=null;
		}
		return strExitState;
	}
}
