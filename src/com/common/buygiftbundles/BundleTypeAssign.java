package com.common.buygiftbundles;

import java.util.Map;

import com.audium.server.session.ActionElementData;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class BundleTypeAssign extends ActionElementBase 
{	
	@Override
	@SuppressWarnings("unchecked")
	public void doAction(String strElementName, ActionElementData data) throws Exception 
	{
		Utilities util=new Utilities(data);
		String strBundleType="";
		String strPurchaseType="";
		Map<String,String> mapOtherBundlesConfig = null;
		try
		{
			String strUserInput= data.getElementData("AA_PRP_XX_MN_0049_DM", "value");
			util.addToLog(strElementName+ " User Entered Value :" +strUserInput,util.DEBUGLEVEL);
			mapOtherBundlesConfig = (Map<String, String>) data.getApplicationAPI().getApplicationData("OTHER_BUNDLES_MENU");
			util.addToLog(strElementName+" Bundles Menu config: "+mapOtherBundlesConfig,util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(mapOtherBundlesConfig)&&mapOtherBundlesConfig.get("MENU_ID").equals("AA_PRP_XX_MN_0049_DM"))
			{
				String strBundleDetail = mapOtherBundlesConfig.get(strUserInput);
				String[] arrBundleDetails = strBundleDetail.split("\\|");
				strBundleType = arrBundleDetails[0];
				strPurchaseType = arrBundleDetails[1];
			}
			data.addToLog(strElementName, "Setting bundle type as "+strBundleType);
			data.setSessionData("S_BUNDLE_TYPE",strBundleType);
			data.setSessionData("S_PURCHASE_TYPE",strPurchaseType);
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			mapOtherBundlesConfig = null;
			util=null;
			
		}
	}
}