package com.common.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class SetSelectedLanguage extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util = new Utilities(data);
		Map<String, Object> languageSettings = null;
		List<String> avlLanguages = null;
		try {
			int userLangChoice=Integer.parseInt(util.getMenuElementValue());
			data.addToLog(elementName, "Menu Input:"+userLangChoice);
			languageSettings = (Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");

			if(util.IsNotNUllorEmpty(languageSettings)) {
				util.addToLog("Language Details : " + languageSettings,util.DEBUGLEVEL);
				avlLanguages = (ArrayList<String>) languageSettings.get("LANGUAGES");
				data.addToLog(elementName, "Available languages for Language selection are :"+avlLanguages);
				data.setSessionData("S_ACTIVE_LANG", avlLanguages.get(userLangChoice-1));
				util.setDefaultAudioPath();
				exitState="SU";
			}
		} catch (Exception e) {
			util.errorLog(elementName, e);;
		}
		finally
		{
			languageSettings = null;
			avlLanguages = null;
			util = null;	
		}
		return exitState;
	}

}
