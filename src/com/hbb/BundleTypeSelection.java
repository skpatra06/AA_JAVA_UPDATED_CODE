package com.hbb;

import java.util.List;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class BundleTypeSelection extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "";
		Utilities util=new Utilities(data);
		Map<String,String> mapBundleData=null;
		List<Map<String, String>> listBundlesTotal=null;
		List<Map<String, String>> listBundlesDup=null;
		try {
			String strBundleType= "HBB";
			//String selectedOption=data.getElementData("AA_HBB_MN_0008_DM", "value");
			listBundlesTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			data.addToLog(strElementName, "Total Bundle Data: "+listBundlesTotal);
			
			String strBundleAmount="";
			String strUserInput=util.getMenuElementValue();
			data.addToLog(strElementName, "User input from Bundle Selection menu:"+strUserInput);
			if("*".equalsIgnoreCase(strUserInput)||"#".equalsIgnoreCase(strUserInput))
			{
				strExitState=strUserInput.equals("#")?"HASH":"STAR";	
			}else if(util.IsNotNUllorEmpty(strUserInput)&&Integer.parseInt(strUserInput)<=4) {
				mapBundleData=listBundlesTotal.get((Integer.parseInt(strUserInput)-1));
				util.addFreqBundle(mapBundleData,strBundleType,data);
				strBundleAmount = String.valueOf(mapBundleData.get("amount"));
				data.setSessionData("S_CURRENCY_CODE", String.valueOf(mapBundleData.get("currency")));
				data.addToLog(strElementName, "Selected Bundle Amount: "+strBundleAmount);
				data.setSessionData("S_PRODUCT_CODE", String.valueOf(mapBundleData.get("Product_code")));
				data.setSessionData("S_RECHARGE_AMT", strBundleAmount);
				strExitState = "BundleSelected";
			} else if("5".equalsIgnoreCase(strUserInput)){
				String strPeriodType=String.valueOf(data.getSessionData("S_USER_SELECTED_PERIOD_TYPE"));
				data.addToLog(strElementName,"User Selected Bundle  : "+strBundleType+" : "+strPeriodType);
				listBundlesDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
				data.addToLog(strElementName, "Remaining Bundle Data: "+listBundlesDup);
				util.loadBundlesMenu(listBundlesDup, false, "S_BUNDLE_"+strBundleType+"_BUY_"+strPeriodType.toUpperCase()+"_DC", data);
				strExitState = "5";
				data.setSessionData("S_REPEAT_COUNTER","0");
			} else {
				data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				strExitState="6";
			}
		} catch (Exception e) {
			util.errorLog(strElementName, e);
		} finally {
			mapBundleData=null;
			listBundlesTotal=null;
			listBundlesDup=null;
			util=null;
		}
		return strExitState;

	}
}