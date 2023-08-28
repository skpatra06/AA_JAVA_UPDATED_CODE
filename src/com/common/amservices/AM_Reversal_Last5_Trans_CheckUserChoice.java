package com.common.amservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_Reversal_Last5_Trans_CheckUserChoice extends DecisionElementBase {
	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="4";
		Utilities util = new Utilities(data);
		List<Map<String, String>> transactionList =null;
		Map<String,String> moreOption=null;
		List<Map<String, String>> transactionListDetails = null;
		List<Map<String, String>> transactionRemainingList = null;
		List<Map<String, String>> dupTransactionList = null;
		Map<String,String> mapAvailable = null;
		try
		{
			transactionList=(List<Map<String, String>>) data.getSessionData("S_LOADED_REVERSE_TRANS_LIST");
			String previousMenuElement=util.getMenuElementValue();
			data.addToLog("--- REVERSE TRANS SELECTED OPTION : ",""+previousMenuElement);
			int transcationSelectedOption=Integer.parseInt(previousMenuElement);
			if(util.IsNotNUllorEmpty(transactionList))
			{
				if(transcationSelectedOption<=3) 
				{
					mapAvailable=transactionList.get(transcationSelectedOption-1);
					if(util.IsNotNUllorEmpty(mapAvailable))
					{
						data.setSessionData("S_AMR_TXNID",mapAvailable.get("txnId"));
                     	data.setSessionData("S_SEC_PARTY_MSISDN",mapAvailable.get("secondPartId"));
						data.setSessionData("S_REPEAT_COUNTER","0");
						exitState =String.valueOf(transcationSelectedOption);
					}
					else
					{
						data.addToLog(elementName, "Error fetching selected transaction Map. Value:"+mapAvailable);
					}
				}
				else if(5==transcationSelectedOption)
				{
					transactionRemainingList=(List<Map<String, String>>) data.getSessionData("S_REMAINING_REVERSE_TRANS_LIST");
					transactionListDetails=new ArrayList<>();
					dupTransactionList=new ArrayList<Map<String,String>>();
					dupTransactionList.addAll(transactionRemainingList);
					data.addToLog(elementName, " Remaining Reverse Transaction List Size : "+transactionRemainingList.size());
					for(int i=0;i<transactionRemainingList.size();i++)
					{
						if(i==4) {
							moreOption=new HashMap<String, String>();;
							moreOption.put("MORE_OPTION", "S_REVERSE_TRANS_MORE_OPTION_DC");
							transactionListDetails.add(moreOption);
							break;
						}
						transactionListDetails.add(dupTransactionList.remove(0));
					}
					util.setAudioItemListForMenu("S_AM_REVERSE_TRANS_LIST",transactionListDetails, "S_REVERSE_AM_TRANSACTION_DC");
					data.setSessionData("S_LOADED_REVERSE_TRANS_LIST", transactionListDetails);
					data.setSessionData("S_REMAINING_REVERSE_TRANS_LIST",dupTransactionList);
					exitState="5";
				}
				else 
				{
					data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				}
			}else
			{
				data.addToLog(elementName, "No Transactions fetched "+transactionList);
			}

		}catch(Exception e) {
			util.errorLog(elementName, e);
		}finally {
			transactionRemainingList = null;
			transactionListDetails = null;
			dupTransactionList = null;
			transactionList=null;
			mapAvailable = null;
			moreOption=null;
			util=null;
		}
		return exitState;
	}

}
