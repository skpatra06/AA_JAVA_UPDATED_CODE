package com.postpaid.IDAN;


import java.util.List;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class PrefLangCheck extends ActionElementBase{

	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception
	{
		Utilities util=new Utilities(data);
		List<String> languageSetting=null;
		try
		{
			String prefLang=(String) data.getSessionData("S_PREF_LANG");
			languageSetting=(List<String>) data.getApplicationAPI().getApplicationData("IDAN_LANGUAGES");
			util.addToLog(strElementName+"Lang Setting : "+languageSetting, util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(languageSetting)) 
			{
				if(util.IsNotNUllorEmpty(prefLang)) 
				{
					if(languageSetting.contains(prefLang))
					{
						data.setSessionData("S_ACTIVE_LANG",prefLang);
						util.setDefaultAudioPath();	 
					}
				}
				else
				{
					data.addToLog(strElementName, "No Pref Lang is found,So call will be continue on default lang");
				}
			}
			else
			{
				data.addToLog(strElementName, "Null Value Occurs In Lang Setting, So we continue with default lang.");
			}

		}
		catch (Exception e) {
			util.errorLog(strElementName, e);
		}
		finally
		{
			languageSetting=null;
			util=null;
		}

	}

}
