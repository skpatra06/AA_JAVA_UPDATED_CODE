package com.hbb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundleSelectionCheck extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "";
		Utilities util=new Utilities(data);
		List<Map<String, String>> listBundlesTotal=null;
		List<Map<String, String>> listBundlesDup= null;
		Map<String,String> mapBundleData=null;
		try
		{
			String strBundleType= "HBB";
			listBundlesTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			data.addToLog(strElementName, "Total Bundle Data: "+listBundlesTotal);
			String strBundleAmount="";
			int intUserInput= Integer.parseInt(util.getMenuElementValue());
			data.addToLog(strExitState, "User input from Bundle Selection menu:"+intUserInput);
			if(intUserInput<=4) {
				mapBundleData=listBundlesTotal.get(intUserInput-1);
				/** Adding FreqBunlde */
				util.addFreqBundle(mapBundleData, strBundleType, data);
				strBundleAmount = String.valueOf(mapBundleData.get("amount"));
				data.setSessionData("S_CURRENCY_CODE", String.valueOf(mapBundleData.get("currency")));
				data.setSessionData("S_PRODUCT_CODE", String.valueOf(mapBundleData.get("productCode")));
				data.addToLog(strElementName, "Selected Bundle Amount: "+strBundleAmount);
				data.setSessionData("S_RECHARGE_AMT", strBundleAmount);
				strExitState =""+intUserInput;
				data.setSessionData("S_REPEAT_COUNTER","0");
			} else if(intUserInput==5) {
				String strPeriodType=String.valueOf(data.getSessionData("S_USER_SELECTED_PERIOD_TYPE"));
				data.addToLog(strElementName,"User Selected Bundle  : "+strBundleType+" : "+strPeriodType);
				listBundlesDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
				data.addToLog(strElementName, "Remaining Bundle Data: "+listBundlesDup);
				util.loadBundlesMenu(listBundlesDup, false,"S_BUNDLE_"+strBundleType+"_BUY_"+strPeriodType.toUpperCase()+"_DC", data);
				data.setSessionData("S_REPEAT_COUNTER","0");
				strExitState = "5";
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
			util=null;
			listBundlesTotal=null;
			listBundlesDup= null;
			mapBundleData=null;
		}
		return strExitState;
	}
}
