package com.common.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class SetChangedLanguage extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util = new Utilities(data);
		try {
			String changeLangChoice=util.getMenuElementValue();
			List<String> avlLanguages=(List<String>) data.getSessionData("LANGUAGE_CHANGE_MAP");
			if(util.IsNotNUllorEmpty(avlLanguages))
			{
				if(util.IsNotNUllorEmpty(changeLangChoice)) 
				{
					data.addToLog("Language Details :", "" + avlLanguages);
					data.setSessionData("S_ACTIVE_LANG", avlLanguages.get(Integer.parseInt(changeLangChoice)-1));
					util.setDefaultAudioPath();
					exitState="SU";
				}
			}

		} 
		catch (Exception e)
		{
			data.addToLog(elementName, exitState);
		}
		finally 
		{
			util=null;
		}
		return exitState;
	}

}
