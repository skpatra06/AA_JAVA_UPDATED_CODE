package com.postpaid.IDAN;

import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;
import java.util.List;

public class SetSelectedLanguage extends DecisionElementBase 
{
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{ 
		String exitState = "ER";
		Utilities util = new Utilities((APIBase)data);
		List<String> languages = null;
		try 
		{
			languages = (List<String>)data.getApplicationAPI().getApplicationData("IDAN_LANGUAGES");
			String userLangChoice = util.getMenuElementValue();
			data.addToLog(elementName, "Menu Input:" + userLangChoice);
			if (util.IsNotNUllorEmpty(languages))
			{
				data.addToLog(elementName, "Available languages for Language selection are :" + languages);
				data.setSessionData("S_ACTIVE_LANG", languages.get(Integer.parseInt(userLangChoice) - 1));
				util.setDefaultAudioPath();
				exitState = userLangChoice;
			} 
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		} 
		finally 
		{
			languages = null;
			util = null;
		} 
		return exitState;
	}
}