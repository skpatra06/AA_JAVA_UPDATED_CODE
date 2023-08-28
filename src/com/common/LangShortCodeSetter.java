package com.common;

import java.util.Map;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class LangShortCodeSetter extends ActionElementBase
{
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception 
	{
		Utilities util=new Utilities(data);
		try
		{
			String shortCode=String.valueOf(data.getSessionData("short_code"));
			Map<String,String> shortCodeMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("LANG_SHORT_CODE");
			util.addToLog("Short Code Config Map : "+shortCodeMap ,util.DEBUGLEVEL);
			data.addToLog(strElementName, "Short Code : "+ shortCode);
			if(util.IsNotNUllorEmpty(shortCodeMap)) 
			{
				if(util.IsNotNUllorEmpty(shortCode)&&shortCodeMap.containsKey(shortCode))
				{
					String langCode=shortCodeMap.get(shortCode);
					if(util.IsNotNUllorEmpty(langCode)) {
						data.setSessionData("S_ACTIVE_LANG",langCode.toUpperCase());
						util.setDefaultAudioPath();	
					}else {
						data.addToLog(strElementName, "Null Value Occurs in Lang Code in Config Data ,So call will be continue On Default Language ...");
					}
				}else {
					data.addToLog(strElementName, "No Short Code Is Found, So Call Will Be Continue On Default Language ....");
				}
			}else {
				data.addToLog(strElementName, "Null Value Occurs In Lang Short Code Config  Data .... So call will Continue On Default Lang");
			}
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);
		}
	}
}
