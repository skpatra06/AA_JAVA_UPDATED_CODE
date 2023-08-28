package com.common.faf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Product_GetFAFList_RM_Frnd extends DecisionElementBase
{
	 
	String FAF_NUMBER="fafNumber";
	
	@Override
	public String doDecision(String strElementName, DecisionElementData data)throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		List<Map<String,String>> fafBundleList = null;
		List<Map<String,String>> lastDigitbundleList = null;
		Map<String,String> moreOption=null;
		Map<String, String> lastFourDigit = null;
		List<Map<String, String>> listFafBundles = null;
		List<Map<String, String>> listFafBundleDup = null;
		try{
			if(util.IsValidRestAPIResponse()){
				fafBundleList = (List<Map<String, String>>) data.getSessionData("S_FAF_INFORMATION");
				lastDigitbundleList = new ArrayList<Map<String,String>>();
					
				if(util.IsNotNUllorEmpty(fafBundleList))
				{
					util.addToLog(strElementName+ "FAF INFORMATION FETCHED:"+fafBundleList.toString(),util.DEBUGLEVEL);
					data.addToLog(strElementName, "FAF INFORMATION SIZE   :"+fafBundleList.size());
					if(fafBundleList.size()==1)
					{
						lastFourDigit=new HashMap<>();
						lastFourDigit.put(FAF_NUMBER,util.GetLastDigits(""+fafBundleList.get(0).get(FAF_NUMBER)));
						lastDigitbundleList.add(lastFourDigit);
						util.setAudioItemListForMenu("S_FAF_NUMBER_BUNDLE_DETAILS",lastDigitbundleList,"S_FAF_DETAILS_DC");
						data.setSessionData("S_FAF_BUNDLE_TOTAL", fafBundleList);
						strExitState="Success1Value";
					}else 
					{
						listFafBundles= new ArrayList<>();
						listFafBundleDup= new ArrayList<>();
						listFafBundleDup.addAll(fafBundleList);
						for(int i=0;i<fafBundleList.size();i++)
						{
							lastFourDigit=new HashMap<>();
							if(i==4)
							{
								moreOption=new HashMap<>();
								moreOption.put("MORE_OPTION", "S_REMOVE_OTHERS_MORE_OPTION_DC");
								lastDigitbundleList.add(moreOption);
								break;
							}
							lastFourDigit.put(FAF_NUMBER,util.GetLastDigits(fafBundleList.get(i).get(FAF_NUMBER)));
							lastDigitbundleList.add(lastFourDigit);
							listFafBundles.add(listFafBundleDup.remove(0));
						}
						util.setAudioItemListForMenu("S_FAF_NUMBER_BUNDLE_DETAILS", lastDigitbundleList,"S_FAF_DETAILS_DC");
						data.setSessionData("S_FAF_BUNDLE_TOTAL", listFafBundles);
						data.setSessionData("S_FAF_BUNDLE_REMAINING", listFafBundleDup);
						data.setSessionData("DYNAMIC_KEYPRESS",data.getSessionData("DTMF_KEYPRESS"));
						strExitState = "SuccessMoreThan1";
					}
				}
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util=null;
			fafBundleList = null;
			lastDigitbundleList = null;
			moreOption=null;
			lastFourDigit = null;
			listFafBundles = null;
			listFafBundleDup = null;
		}
		return strExitState;
	}

}
