package com.common.buygiftbundles;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class FreqBundlesFetch extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		JSONParser jParser= null;
		JSONObject objJson= null;
		Map<String,List<Map<String, String>>> mapFreqBundlesAll = null;
		try
		{

			if(util.IsValidDBSUResponse())
			{
				String strBundleJson= String.valueOf(data.getSessionData("S_FREQUENT_BUNDLE"));
				if(util.IsNotNUllorEmpty(strBundleJson))
				{
				jParser=new JSONParser();
				objJson=(JSONObject) jParser.parse(strBundleJson);
				util.addToLog(strElementName+ " JSON Object: "+objJson, util.DEBUGLEVEL);
				
				mapFreqBundlesAll = (Map<String, List<Map<String, String>>>)objJson; 			
				data.addToLog(strElementName, "Frequent bundle fetched successfully: "+mapFreqBundlesAll);
				data.setSessionData("S_FREQUENT_BUNDLE", mapFreqBundlesAll);
				strExitState = "Success";
				}
			}
			else
			{
				data.addToLog(strElementName, "Frequent bundle data not fetched");
			}

		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
			jParser= null;
			objJson= null;
			mapFreqBundlesAll = null;
		}
		return strExitState;

	}
}
