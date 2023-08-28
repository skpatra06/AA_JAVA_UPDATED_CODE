package com.common.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class LanguageInitial extends DecisionElementBase
{	
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState = "ER";
		Map<String, Object> languageSettings = null;
		List<String> avlLanguages = null;
		Utilities util = new Utilities(data);
		try 
		{
			String prefLang = (String) data.getSessionData("S_PREF_LANG");
			data.addToLog(elementName," PREFERRED LANGUAGE :"+prefLang);
			if (util.IsNotNUllorEmpty(prefLang)) 
			{
				languageSettings = (Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");
				if(util.IsNotNUllorEmpty(languageSettings))
				{
					util.addToLog(elementName+"LANGUAGE AVAILABLE "+languageSettings,util.DEBUGLEVEL);
					avlLanguages = (List<String>) languageSettings.get("LANGUAGES");
					if(util.IsNotNUllorEmpty(avlLanguages))
					{
						for(int i=0;i<avlLanguages.size();i++) 
						{
							if(null!=avlLanguages.get(i)&&avlLanguages.get(i).equalsIgnoreCase(prefLang)) 
							{
								data.setSessionData("S_ACTIVE_LANG", prefLang.toUpperCase());
								util.setDefaultAudioPath();	
								data.setSessionData("S_LANG_AVAI_FLAG", "Y");
								exitState = "LANG_AVL";
								break;
							}
						}
					}
					if(!exitState.equalsIgnoreCase("LANG_AVL"))
					{
						data.addToLog(elementName,"LANGUAGE NOT AVAILABLE ");
						configureInitialLangauge(elementName,data,util);	
						exitState = "LANG_NAVL";
					}		
				}else
				{
					data.addToLog(elementName, "Error fetching Language settings. Value:"+languageSettings);
				}
			} 
			else
			{
				data.addToLog(elementName,"LANGUAGE NOT AVAILABLE ");
				configureInitialLangauge(elementName,data,util);
				exitState = "LANG_NAVL";
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
			util = null;
		}
		return exitState;
	}
	@SuppressWarnings("unchecked")
	private void configureInitialLangauge(String elementName,APIBase data,Utilities util) 
	{
		Map<String, Object> languageSettings = null;
		List<String> avlLanguages = null;
		List<String> languageAudios = null;
		List<String> listDTMF = null;
		try
		{
			languageSettings = (Map<String, Object>) data.getApplicationAPI().getApplicationData("LANGUAGE_SETTINGS");
			util.addToLog("Language Details : " + languageSettings,util.DEBUGLEVEL);
			String strOPCOCode = (String) data.getSessionData("S_OPCO_CODE");
			if(util.IsNotNUllorEmpty(languageSettings))
			{
				avlLanguages = (List<String>) languageSettings.get("LANGUAGES");
				data.addToLog("APCO specific Languages:", "" + avlLanguages);
				languageAudios = new ArrayList<>();
				listDTMF = new ArrayList<>();
				for (int cntr = 1; cntr <= avlLanguages.size(); cntr++)
				{
					if(util.IsNotNUllorEmpty(strOPCOCode)&&(strOPCOCode.equalsIgnoreCase("NG")||strOPCOCode.equalsIgnoreCase("UG")||strOPCOCode.equalsIgnoreCase("CD"))) // ENG.wav@ENGpress1.wav
					{
						languageAudios.add(avlLanguages.get(cntr - 1) + ".wav@"+avlLanguages.get(cntr - 1)+"press" + cntr + ".wav");
					}
					else if(util.IsNotNUllorEmpty(strOPCOCode)&&"MG".equalsIgnoreCase(strOPCOCode))
					{
						languageAudios.add(avlLanguages.get(cntr - 1)+"press" + cntr + ".wav@"+avlLanguages.get(cntr - 1) + ".wav");	
					}
					else
					{
						languageAudios.add(avlLanguages.get(cntr - 1) + ".wav@press" + cntr + ".wav");
					}
					listDTMF.add(""+ cntr);
				}
				util.setAudioItemForCVPMenu("S_LIST_INITIAL_LANGUAGES", languageAudios);
				util.setMenuDTMF(listDTMF);
			}
			else
			{
				data.addToLog(elementName, "Error fetching Language settings Map. Value: "+languageSettings);
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
			listDTMF = null;
		}
	}
}