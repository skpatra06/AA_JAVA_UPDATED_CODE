package com.common.amservices;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;



public class AM_Reversal_Last5Tran extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception
	{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		List<Map<String, String>> reversalTransactionDetails=null;
		List<Map<String,String>> transactionList=null;
		List<Map<String,String>> dubTransactionList=null;
		try
		{
			String strErrorCode =String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
			if (util.IsValidRestAPIResponse())
			{
				reversalTransactionDetails = (List<Map<String, String>>) data.getSessionData("S_TRADE_AM_REVERSE_TRANSACTION_DETAILS");
				util.addToLog(elementName+" LAST REVERSAL TRANS :"+reversalTransactionDetails,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(reversalTransactionDetails))
				{
					data.addToLog(elementName," LAST REVERSAL TRANS SIZE :"+reversalTransactionDetails.size());
					transactionList =new ArrayList<>();
					dubTransactionList=new ArrayList<Map<String,String>>();
					dubTransactionList.addAll(reversalTransactionDetails);
					int count=0;

					/** ZM we are rev order in desc */
					if("ZM".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE")))
							||"Y".equalsIgnoreCase(String.valueOf(data.getSessionData("S_AM_REV_DESC_FLAG")))) {
						for(int i=(reversalTransactionDetails.size()-1);i>=0;i--) {
							Map<String,String> reversalTranscationMap=reversalTransactionDetails.get(i);
							if(reversalTranscationMap.containsKey("txnType")&&"DR".equalsIgnoreCase(reversalTranscationMap.get("txnType"))){
								transactionList.add(reversalTranscationMap);
								count++;
							}
							if(count==3)
								break;
						}
					}
					else
					{
						for(Map<String, String> reversalTranscationMap:reversalTransactionDetails)
						{
							if(reversalTranscationMap.containsKey("txnType")&&"DR".equalsIgnoreCase(reversalTranscationMap.get("txnType")))
							{
								transactionList.add(reversalTranscationMap);
								count++;
							}
							if(count==3)
								break;
						}
					}
					if(0<count)
					{
						data.addToLog(elementName,"TRANS LOADED LIST : "+transactionList.size());
						util.setAudioItemListForMenu("S_AM_REVERSE_TRANS_LIST",transactionList,"S_REVERSE_AM_TRANSACTION_DC");
						data.setSessionData("S_LOADED_REVERSE_TRANS_LIST", transactionList);
						data.setSessionData("S_REMAINING_REVERSE_TRANS_LIST",dubTransactionList);
						exitState="YES";
					}
					else
					{
						data.addToLog(elementName,"No P2P Reverse Transaction List ");
						exitState="NO";
					}
				}
				else
				{
					if("1111".equalsIgnoreCase(strErrorCode)) {
						data.addToLog(elementName,"No P2P Reverse Transaction List ");
						exitState="NO";
					}
				}	
			}
			else 
			{

				if("1111".equalsIgnoreCase(strErrorCode)) {
					data.addToLog(elementName,"No P2P Reverse Transaction List ");
					exitState="NO";
				}
			}	
		} 
		catch (Exception e) 
		{
			util.errorLog(elementName, e);
		} 
		finally
		{			
			transactionList=null;
			dubTransactionList=null;
			reversalTransactionDetails=null;
			util=null;
		}
		return exitState;
	}
}