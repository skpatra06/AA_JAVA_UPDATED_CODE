package com.common.buygiftbundles;

import java.util.Map;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class MamoBundletypeassign extends ActionElementBase
{

	@SuppressWarnings("unchecked")
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception 
	{
		Utilities util=new Utilities(data);
		Map<String,String> mapMamoBundlesConfig = null;
		String strBundleType="";
		try {
			String UserInput=data.getElementData("AA_MAM_MN_0001_DM","value");
			util.addToLog(strElementName+ " User Entered Value :" +UserInput,util.DEBUGLEVEL);
			mapMamoBundlesConfig = (Map<String, String>) data.getApplicationAPI().getApplicationData("MAMO_BUNDLES_MENU");
			util.addToLog(strElementName+" Bundles Menu config: "+mapMamoBundlesConfig,util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(mapMamoBundlesConfig)&&mapMamoBundlesConfig.get("MENU_ID").equals("AA_MAM_MN_0001_DM"));
			{
				strBundleType = mapMamoBundlesConfig.get(UserInput);
				
			}
			data.setSessionData("S_BUNDLE_TYPE",strBundleType);
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);	
		}
		finally
		{
			util=null;
			mapMamoBundlesConfig = null;
		}
		
	}
	

}
