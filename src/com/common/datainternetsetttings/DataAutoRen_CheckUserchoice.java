package com.common.datainternetsetttings;

import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataAutoRen_CheckUserchoice extends DecisionElementBase {
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="";
		Utilities util = new Utilities((APIBase)data);
		List<Map<String, String>> listNewVas = null;
		List<Map<String, String>> dataAutoRenewList = null;
		List<Map<String, String>> autoRenewRemainingList = null;
		List<Map<String, String>> totalAutoRenewLoadedList = null;
		List<Map<String, String>> dupAutoRenewlist = null;
		Map<String, String> dataAutoRenew = null;
		Map<String, String> moreOption = null;
		try
		{
			String slectedOpt = util.getMenuElementValue();
			dataAutoRenewList = (List<Map<String, String>>)data.getSessionData("S_LOADED_DATA_AUTO_RENEWAL_LIST");
			data.addToLog("DATA AUTO RENEWAL SELECTED OPTION ", slectedOpt);
			if (util.IsNotNUllorEmpty(dataAutoRenewList))
			{
				int userChoice = Integer.parseInt(slectedOpt);
				if (userChoice <= 4) 
				{
					dataAutoRenew = dataAutoRenewList.get(userChoice - 1);
					if (util.IsNotNUllorEmpty(dataAutoRenewList)) 
					{
						data.setSessionData("S_CHARGED_MSISDN", dataAutoRenew.get("chargedMsisdn"));
						data.setSessionData("S_PROVISION_MSISDN", dataAutoRenew.get("provisionMsisdn"));
						data.setSessionData("S_PRODUCT_ID", dataAutoRenew.get("productId"));
						data.setSessionData("S_PRODUCT_GROUP_ID", dataAutoRenew.get("productGroupId"));
						data.setSessionData("S_TRANSACTION_ID", dataAutoRenew.get("srcTransactionId"));
						exitState = String.valueOf(userChoice);
					} 
				} 
				else if (5 == userChoice)
				{
					autoRenewRemainingList = (List<Map<String, String>>)data.getSessionData("S_DATA_AUTO_RENEWAL_REMAINING_LIST");
					totalAutoRenewLoadedList = new ArrayList<>();
					dupAutoRenewlist = new ArrayList<>();
					dupAutoRenewlist.addAll(autoRenewRemainingList);
					data.addToLog(elementName, " Remaining Available Vas UnSubscribe List Size : " + autoRenewRemainingList.size());
					for (int i = 0; i < autoRenewRemainingList.size(); i++) 
					{
						if (i == 4)
						{
							moreOption = new HashMap<>();
							moreOption.put("MORE_OPTION", "S_VAS_MORE_OPTION_DC");
							totalAutoRenewLoadedList.add(moreOption);
							break;
						} 
						totalAutoRenewLoadedList.add(dupAutoRenewlist.remove(0));
					} 
					util.setAudioItemListForMenu("S_DATA_AUTO_RENEWAL_DETAILS", totalAutoRenewLoadedList, "S_DATA_AUTO_RENEWAL_DC");
					data.setSessionData("S_LOADED_DATA_AUTO_RENEWAL_LIST", totalAutoRenewLoadedList);
					data.setSessionData("S_DATA_AUTO_RENEWAL_REMAINING_LIST", dupAutoRenewlist);
					exitState = "5";
				} 
				else 
				{
					data.setSessionData("DTMF_KEYPRESS", data.getSessionData("REPEAT_DTMF_KEYPRESS"));
					exitState = "6";
				} 
			} 
			else
			{
				data.addToLog(elementName, "Null Values In Vas Unsubscribe List");
			} 
		} 
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			dupAutoRenewlist = null;
			dataAutoRenew = null;
			moreOption = null;	
			listNewVas = null;
			dataAutoRenewList = null;
			autoRenewRemainingList = null;
			totalAutoRenewLoadedList = null;
			util=null;
		}
		return exitState;
	}
}