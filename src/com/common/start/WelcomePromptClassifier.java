package com.common.start;

import java.util.Map;
import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class WelcomePromptClassifier extends ActionElementBase{

	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception 
	{
		String welMsgPrompt="";
		Utilities util=new Utilities(data);
		Map<String,String> welcomeMsgMap=null;
		try
		{
			welcomeMsgMap=(Map<String, String>) data.getApplicationAPI().getApplicationData("SEG_WEL_MSG");	
			util.addToLog(strElementName+" WEL MSG MAP : "+welcomeMsgMap,util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(welcomeMsgMap))
			{
			String customerSegmentation=(String) data.getSessionData("S_CUSTOMER_SEGMENT");
			if(welcomeMsgMap.containsKey(customerSegmentation))
			{
				welMsgPrompt=welcomeMsgMap.get(customerSegmentation);	
				data.setSessionData("S_WELCOME_MSG",welMsgPrompt);
			}
			if(!util.IsNotNUllorEmpty(welMsgPrompt)) 
			{
				data.setSessionData("S_WELCOME_MSG",welcomeMsgMap.get("DEFAULT"));
			}
			}else
			{
				data.addToLog(strElementName, "Error fetching Welcome msg map. Value:"+welcomeMsgMap);
			}
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util=null;
			welcomeMsgMap=null;
		}
	}

}
