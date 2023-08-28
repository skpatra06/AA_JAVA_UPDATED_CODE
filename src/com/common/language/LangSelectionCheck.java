package com.common.language;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class LangSelectionCheck extends DecisionElementBase{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState="MainMenu";
		Utilities util=new Utilities(data);
		Map<String, Object> languageSettings = null;
		List<String> avlLanguages=null;
		try {
			String selOpt=util.getMenuElementValue();
			data.addToLog(strElementName, "Selected Option : "+selOpt);
			if("1".equalsIgnoreCase(selOpt)) {
				languageSettings = (Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");
				String activeLang=(String) data.getSessionData("S_ACTIVE_LANG");
				util.addToLog(strElementName+" Language Setting : "+languageSettings,util.DEBUGLEVEL);
				data.addToLog(strElementName," Active Lang      : "+activeLang);
				if (util.IsNotNUllorEmpty(languageSettings)) {
					avlLanguages = (List<String>) languageSettings.get("LANGUAGES");
					if(null!=avlLanguages) {
						for(String 	lang:avlLanguages) {
							if(null!=activeLang&&!lang.equalsIgnoreCase(activeLang)) {
								data.setSessionData("S_ACTIVE_LANG",lang.toUpperCase());
								util.setDefaultAudioPath();	
								data.addToLog(strElementName, "Language Changed Successfully .... ");
								exitState="LangChanged";
								break;
							}
						}
					}
				}else {
					data.addToLog(strElementName, "Lang Map Config Value Is Null : "+languageSettings);
				}

			}
		}catch (Exception e) {
			util.errorLog(strElementName, e);
		}finally {
			languageSettings=null;
			util=null;
		}
		return exitState;
	}

}
