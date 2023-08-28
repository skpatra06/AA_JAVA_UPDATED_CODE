package com.common.start;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class OutboundPreferLangCheck extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		// TODO Auto-generated method stub
		String exitState="ER";
		Utilities util = new Utilities(data);
		Map<String, Object> languageSettings = null;
		List<String> avlLanguages =null;
		try {
			
			String preferLang= (String) data.getSessionData("S_PREF_LANG");
			util.addToLog("Prefer Language is :" +preferLang, util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(preferLang)) {
				
				languageSettings=(Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");
				util.addToLog("Language Details :"+languageSettings, util.DEBUGLEVEL);
				
				if(util.IsNotNUllorEmpty(languageSettings)) {
					
					avlLanguages= (List<String>) languageSettings.get("LANGUAGES");
					util.addToLog("Avaiakable Language Details :"+avlLanguages, util.DEBUGLEVEL);
					if(util.IsNotNUllorEmpty(avlLanguages))
					{
						for(int i=0;i<avlLanguages.size();i++) 
						{
							if(null!=avlLanguages.get(i)&&avlLanguages.get(i).equalsIgnoreCase(preferLang)) 
							{
								data.setSessionData("S_ACTIVE_LANG", preferLang.toUpperCase());
								util.addToLog("set Active Language:  "+avlLanguages, util.DEBUGLEVEL);
								util.setDefaultAudioPath();	
								data.setSessionData("S_LANG_AVAI_FLAG", "Y");
								exitState = "SU";
								break;
							}
						}
					}
				
			}
			
			}
			
		} catch (Exception e) {
			util.errorLog(elementName, e);
		}
		return exitState;
	}

}
