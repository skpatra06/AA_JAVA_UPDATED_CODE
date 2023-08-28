package com.common.faf;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Subscriber_Product_GetFAFListRM_Frnd_UserChoice  extends DecisionElementBase
{
	String FAF_NUMBER="fafNumber";
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "";
		Utilities util=new Utilities(data);
		List<Map<String, String>> listFafBundle= null;
		Map<String,String> mapBundlesData=null;
		List<Map<String, String>> listFafaBundleRemaining= null;
		List<Map<String, String>> listFafBundleDuplicate=null;
		List<Map<String,String>> listFafBundleTotal= null;
		List<Map<String,String>> lastDigitbundleList = null;	
		Map<String,String> moreOption=null;
		Map<String, String> lastFourDigit= null;
		try
		{

			listFafBundle=(List<Map<String, String>>) data.getSessionData("S_FAF_BUNDLE_TOTAL");
			if(util.IsNotNUllorEmpty(listFafBundle))
			{
				int intUserInput= Integer.parseInt(util.getMenuElementValue());
				util.addToLog(strElementName+ "Total Bundle Data: "+listFafBundle,util.DEBUGLEVEL);
				data.addToLog(exitState, "User input from Bundle Selection menu:"+intUserInput);
				if(intUserInput<=4)
				{
					Date date = new Date();//current date in unix formate
					long unixCurrentTime = date.getTime();
					data.setSessionData("S_FAF_START_DATE", unixCurrentTime);
					data.addToLog(strElementName, "Start Date is :"+ String.valueOf(unixCurrentTime));

					String countOfExpirydays =String.valueOf(data.getSessionData("S_EXPIRY_DAYS_COUNT"));
					data.addToLog(strElementName," EXPIRY DAY COUNT : "+countOfExpirydays);
					if(!util.IsNotNUllorEmpty(countOfExpirydays))
						countOfExpirydays="30";
					int count=Integer.parseInt(countOfExpirydays);  
					long unixEndTime = (date.getTime() + TimeUnit.DAYS.toMillis(count));//add 30 days in current date
					data.setSessionData("S_FAF_EXP_DATE",unixEndTime);
					data.addToLog(strElementName, "Expiry Dtae is :"+ String.valueOf(unixEndTime));

					mapBundlesData=listFafBundle.get(intUserInput-1);
					data.setSessionData("S_FAF_NUMBER",  mapBundlesData.get(FAF_NUMBER));
					data.setSessionData("S_FAF_GROUP", mapBundlesData.get("fafGroup"));
					data.setSessionData("S_FAF_OWNER",mapBundlesData.get("owner"));
					data.setSessionData("S_FAF_ACTION", "DELETE");
					//data.setSessionData("S_FAF_STRT_DATE",  mapBundlesData.get("startDate"));
					//data.setSessionData("S_FAF_EXP_DATE",mapBundlesData.get("expiryDate"));
					//data.setSessionData("S_FAF_OFF_ID", mapBundlesData.get("offerID"));
					data.setSessionData("S_REPEAT_COUNTER","0");
					exitState = ""+intUserInput;
				}
				else if(5==intUserInput)
				{
					listFafaBundleRemaining= (List<Map<String, String>>) data.getSessionData("S_FAF_BUNDLE_REMAINING");
					if(util.IsNotNUllorEmpty(listFafaBundleRemaining))
					{
						listFafBundleDuplicate=new ArrayList<Map<String,String>>();
						listFafBundleTotal= new ArrayList<Map<String,String>>();
						lastDigitbundleList = new ArrayList<Map<String,String>>();	
						data.addToLog(strElementName, "Remaining Bundle Data Size: "+listFafaBundleRemaining.size());
						listFafBundleDuplicate.addAll(listFafaBundleRemaining);
						for(int i=0;i<listFafaBundleRemaining.size();i++)
						{
							lastFourDigit=new HashMap<>();
							if(i==4) {
								moreOption=new HashMap<>();
								moreOption.put("MORE_OPTION", "S_REMOVE_OTHERS_MORE_OPTION_DC");
								lastDigitbundleList.add(moreOption);
								break;
							}
							lastFourDigit.put(FAF_NUMBER,util.GetLastDigits(""+listFafaBundleRemaining.get(i).get(FAF_NUMBER)));
							lastDigitbundleList.add(lastFourDigit);
							listFafBundleTotal.add(listFafBundleDuplicate.remove(0));
						}
						util.setAudioItemListForMenu("S_FAF_NUMBER_BUNDLE_DETAILS", lastDigitbundleList,"S_FAF_DETAILS_DC");
						data.setSessionData("S_FAF_BUNDLE_TOTAL", listFafBundleTotal);
						data.setSessionData("S_FAF_BUNDLE_REMAINING", listFafBundleDuplicate);
						exitState = "5";
					}
				}else {
					data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
					exitState = "6";
				}
			}else
			{
				data.addToLog(strElementName, "Error fetchng Faf list Map. Value:"+listFafBundle);
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}finally {
			util=null;
			listFafBundle= null;
			mapBundlesData=null;
			listFafaBundleRemaining= null;
			listFafBundleDuplicate=null;
			listFafBundleTotal= null;
			lastDigitbundleList = null;	
			moreOption=null;
			lastFourDigit= null;
		}
		return exitState;

	}
}
