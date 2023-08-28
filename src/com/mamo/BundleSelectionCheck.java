package com.mamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundleSelectionCheck extends DecisionElementBase
{
	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		String strExitState="";
		Utilities util=new Utilities(data);
		List<Map<String,String>> bundleList=null;
		List<Map<String, String>> remainingBundle=null;
		try
		{
			String userSelectedChoice=util.getMenuElementValue();
			bundleList=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			if(Integer.parseInt(userSelectedChoice)<=4) 
			{
				Map<String,String> mapSelectedBundle=bundleList.get((Integer.parseInt(userSelectedChoice)-1));
				data.setSessionData("S_PRODUCT_CODE",String.valueOf(mapSelectedBundle.get("productCode")));
				data.setSessionData("S_RECHARGE_AMT",String.valueOf(mapSelectedBundle.get("amount")));
				data.setSessionData("S_REPEAT_COUNTER","0");
				strExitState=""+userSelectedChoice;
			}
			else if("5".equalsIgnoreCase(userSelectedChoice))
			{
				remainingBundle=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
				util.addToLog(strElementName+" :  Remaing Bundle Details "+remainingBundle ,util.DEBUGLEVEL);
				util.loadBundlesMenu(remainingBundle, false, "S_BUNDLE_MAMO_DC", data);
				strExitState="5";
			}
			else 
			{
				data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				strExitState="6";
			}
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			bundleList=null;
			remainingBundle=null;
			util=null;
		}
		return strExitState;
	
	}

}
