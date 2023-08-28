package com.common.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class LanguageChange extends DecisionElementBase {


	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{	
			exitState =configurelangaugeChange(elementName, data,util);
		} 
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			util = null;
		}
		return exitState;
	}

	private String configurelangaugeChange(String elementName,APIBase data,Utilities util)
	{
		String exitstate ="ER";
		Map<String, Object> languageSettings = null;
		List<String> languageAudios = null;
		List<String> langAdded = null;
		List<String> listDTMF = null;
		List<String> avlLanguages = null;
		try 
		{
			languageSettings = (Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");
			if(util.IsNotNUllorEmpty(languageSettings))
			{
				util.addToLog("Language Details : " + languageSettings,util.DEBUGLEVEL);
				avlLanguages = (List<String>) languageSettings.get("LANGUAGES");
				data.addToLog("APCO specific Languages:", "" + avlLanguages);
				String currentLang = (String) data.getSessionData("S_ACTIVE_LANG");
				data.addToLog("Current Languages:", "" + currentLang);
				String strOPCOCode = (String) data.getSessionData("S_OPCO_CODE");
				languageAudios = new ArrayList<>();
				langAdded = new ArrayList<>();
				listDTMF = new ArrayList<>();
				int cntr =1;
				for (String lang : avlLanguages ) 
				{
					if(util.IsNotNUllorEmpty(currentLang) && !currentLang.equalsIgnoreCase(lang))
					{
						if(util.IsNotNUllorEmpty(strOPCOCode)&&(strOPCOCode.equalsIgnoreCase("NG"))||strOPCOCode.equalsIgnoreCase("UG")||strOPCOCode.equalsIgnoreCase("CD"))
						{
							languageAudios.add(lang + ".wav@"+lang+"press" + cntr + ".wav");
						}
						else if(util.IsNotNUllorEmpty(strOPCOCode)&&"MG".equalsIgnoreCase(strOPCOCode))
						{
							languageAudios.add(lang+"press" + cntr + ".wav@"+lang + ".wav");
						}
						else
						{
							languageAudios.add(lang+ ".wav@press" + cntr + ".wav");
						}
						listDTMF.add("" + cntr);
						langAdded.add(lang);
						cntr++;
					}
					else
					{
						data.addToLog("Skip current Languages:", "" + currentLang);
					}
				}
				util.setAudioItemForCVPMenu("S_CHANGE_LANGUAGE_DETAILS", languageAudios);
				util.setMenuDTMF(listDTMF);
				data.setSessionData("LANGUAGE_CHANGE_MAP", langAdded);
				exitstate="SU";
			}
			else
			{
				data.addToLog(elementName, "Error fetching Language settings. Value:"+languageSettings);
			}
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		} 
		finally
		{
			languageSettings = null;
			avlLanguages = null;
			languageAudios = null;
			langAdded = null;
			listDTMF = null;
		}
		return exitstate;
	}
}