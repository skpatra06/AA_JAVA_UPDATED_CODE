package com.common.amservices;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class AM_BalanceInquiryCheck extends DecisionElementBase 
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState="ER";
		Utilities util=new Utilities(data);
		List<Map<String,String>> amWalletList=null;
		try 
		{	 
			if(util.IsValidRestAPIResponse()) 
			{
				amWalletList = (List<Map<String, String>>) data.getSessionData("S_AM_WALLETS");
				if( util.IsNotNUllorEmpty(amWalletList) )  {
					util.addToLog(elementName+ "Am wallet list is :"+amWalletList,util.DEBUGLEVEL);
					for( Map<String,String> walletMap:amWalletList )
					{
						if(walletMap.containsKey("balance"))   {
							String activeAMBalance=walletMap.get("balance");
							if( util.IsNotNUllorEmpty(activeAMBalance) )  {
								data.setSessionData( "S_AM_BALANCE",activeAMBalance.replaceAll(",", ""));
								data.addToLog(elementName," AIRTEL MONEY ACTIVE BALANCE : "+activeAMBalance);
								exitState="AMB_AVL";
							} else {
								data.addToLog( elementName, "Null Value Occurs In AM Balance");
								exitState="AMB_NAVL";
							}
						}
					}
				} else {
					data.addToLog(elementName, "Null Value Occurs in AM wallet List  ");	
				}
			}
			else
			{
				exitState="ER";
			}
		} 
		catch (Exception e) 
		{
			util.errorLog(elementName, e);
		}
		finally 
		{
			amWalletList=null;
			util=null;	
		}
		return exitState;
	}
}