package com.common.language;

import java.util.Map;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class LangShortCodeCheck extends ActionElementBase
{
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception
	{
		Utilities util=new Utilities(data);
		Map<String,String> shortCodeMap=null;
		try 
		{
			shortCodeMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("LANG_SHORT_CODE");
			String dnis=String.valueOf(data.getSessionData("S_DNIS"));
			if(util.IsNotNUllorEmpty(shortCodeMap)) 
			{
				util.addToLog(strElementName+ "Lang Short Code Map : "+shortCodeMap,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(dnis))
				{
					String langCode=null;
					for(String map:shortCodeMap.keySet())
					{
						if(map.contains(dnis)) 
						{
							langCode=shortCodeMap.get(map);
							break;
						}
					}
					if(util.IsNotNUllorEmpty(langCode)) 
					{
						data.setSessionData("S_ACTIVE_LANG",langCode.trim().toUpperCase());
						util.setDefaultAudioPath();
						data.addToLog(strElementName, "DNIS : "+dnis+" Assigning Lang To : "+langCode.trim());
					}
					else
					{
						data.addToLog(strElementName, "Null Value Occurs In Lang Code");	
					}
				}
				else
				{
					data.addToLog(strElementName, "Null Value Occurs In DNIS");
				}
			}
			else 
			{
				util.addToLog("Lang Short Map Is Null");	
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName,e);
		}
		finally
		{
			shortCodeMap=null;
			util=null;
		}
	}
}