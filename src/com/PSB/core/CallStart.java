package com.PSB.core;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import com.airtel.core.ServionCallStart;
import com.audium.server.AudiumException;
import com.audium.server.proxy.StartCallInterface;
import com.audium.server.session.CallStartAPI;
import com.util.Utilities;

public class CallStart implements StartCallInterface
{

	private CallStartAPI callStartAPI;
	public void onStartCall(CallStartAPI callStartAPI) throws AudiumException {
		Utilities util = new Utilities(callStartAPI);	
		try {
             FileInputStream fileInputStream=null; Properties prop=new Properties(); 
			try {
				fileInputStream=new FileInputStream("C:\\airtel\\PropertyFile\\CLI_MAPPING.properties");
				prop.load(fileInputStream); 
				String	ani=""+callStartAPI.getAni();
				callStartAPI.addToLog("CallStart :","CLI MAPPING VALUES : "+prop);
				callStartAPI.addToLog("CallStart ANI :",ani);
				if(prop.containsKey(ani)) 
				{
					callStartAPI.addToLog("CallStart :","CLI MAPPING VALUE :" +prop.get(ani));
					callStartAPI.setSessionData("S_CLI",prop.get(ani)); 
				} 
			} catch (Exception e) {
				callStartAPI.addToLog("CallStart Exception : ",e.toString()); 
			} finally {
				fileInputStream=null;
				prop = null;
			}
			callStartAPI.setSessionData("S_CALL_STARTDATE", util.getCurrDateInStr("yyyy-MM-dd HH:mm:ss.SSS"));
			initialize(callStartAPI);
			invokingXMLFlags("PSB");
			callStartAPI.setSessionData("S_LAST_DIGIT",util.GetLastDigits(""+callStartAPI.getSessionData("S_CLI")));
			callStartAPI.setSessionData("S_CUSTOMER_SEGMENT",callStartAPI.getSessionData("cust_seg"));
			callStartAPI.setSessionData("S_CONN_TYPE",callStartAPI.getSessionData("line_Type"));
			callStartAPI.setSessionData("S_PREF_LANG",callStartAPI.getSessionData("pref_lang"));
			//loadDNISXl(callStartAPI);
		}
		catch (Exception e)
		{
			util.errorLog("Call Start", e);
		}
		finally
		{
			util = null;
		}
	}

	private void initialize(CallStartAPI callStartAPI) throws AudiumException
	{
		this.callStartAPI = callStartAPI;
		onServionStartCall();
	}
	
	private void invokingXMLFlags(String strFileName)
	{
		Utilities util=new Utilities(callStartAPI);
		try
		{
			util.xltoSessionData(strFileName);
		}
		catch (Exception e)
		{
			util.errorLog("Call Start", e);
		}
		finally
		{
			util = null;
		}
	}

	private void onServionStartCall() throws AudiumException
	{
		callStartAPI.addToLog("Invoking CallStart Class", "Executing onServionStartCall() method in CallStart Class");
		ServionCallStart servionCallStart = new ServionCallStart();
		servionCallStart.onStartCall(callStartAPI);
	}

	private void loadDNISXl(CallStartAPI callStartAPI)
	{
		Utilities util = new Utilities(callStartAPI);	
		try 
		{
			Map<String,Map<String,String>> mapDNIS = (Map<String, Map<String, String>>) callStartAPI.getApplicationAPI().getApplicationData("DNIS");
			if(null!=mapDNIS) 
			{
				Map<String,String> mapDNISValue = mapDNIS.get(callStartAPI.getSessionData("S_DNIS"));
				String strDNIStype = mapDNISValue.get("DNIS_TYPE");
				callStartAPI.addToLog("Loading DNIS excel", "Map data:"+mapDNIS+"\nDNIS Type:"+strDNIStype);
				callStartAPI.setSessionData("S_DNIS_TYPE", strDNIStype);
			}
		} catch (AudiumException e) {
			util.errorLog("Call Start", e);
		}
		finally
		{
			util = null;
		}
	}
}
