package com.prepaid.core;

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

public class CallStart implements StartCallInterface {

	private CallStartAPI callStartAPI;
	public void onStartCall(CallStartAPI callStartAPI) throws AudiumException {
		Utilities util = new Utilities(callStartAPI);	
		try{
			FileInputStream fileInputStream=null; 
			Properties prop=new Properties(); 
			try{
				fileInputStream=new FileInputStream("C:\\airtel\\PropertyFile\\CLI_MAPPING.properties");
				prop.load(fileInputStream); 
				String	ani=""+callStartAPI.getAni();
				callStartAPI.addToLog("CallStart :","CLI PropertyFile Map : "+prop);
				callStartAPI.addToLog("CallStart ANI :",ani);
				if(prop.containsKey(ani)&&null!=prop.get(ani)) {
					callStartAPI.addToLog("CallStart :","CLI MAPPING VALUE :" +prop.get(ani));
					callStartAPI.setSessionData("S_CLI",String.valueOf(prop.get(ani)).trim()); 
				} 
			} 
			catch (Exception e)
			{
				callStartAPI.addToLog("CallStart Exception : ",e.toString()); 
			}
			finally
			{
				fileInputStream=null;
				prop = null;
			}
			callStartAPI.setSessionData("S_CALL_STARTDATE", util.getCurrDateInStr("yyyy-MM-dd HH:mm:ss.SSS"));
			initialize(callStartAPI);
			invokingXMLFlags("PREPAID");
			if("TRUE".equalsIgnoreCase(""+callStartAPI.getApplicationAPI().getApplicationData("ZERO_APPEND_FLAG"))) {
				String cli=String.valueOf(callStartAPI.getSessionData("S_CLI"));
				if(util.IsNotNUllorEmpty(cli)&&!cli.startsWith("0")) {
					callStartAPI.setSessionData("S_CLI","0"+cli);	
				}
			}
			// PRODUCT BUNDLE KE - From OUTBOUND IVR call transfer to DataBundle Purchase
			String bundlePurchase=String.valueOf(callStartAPI.getSessionData((String) callStartAPI.getApplicationAPI().getApplicationData("S_PRODUCT_BUNDLE")));
			callStartAPI.setSessionData("S_PRODUCT_BUNDLE",bundlePurchase);
			if("DATA".equalsIgnoreCase(bundlePurchase))
			{
				callStartAPI.addToLog("CallStart :"," From Outbound IVR to Prepaid Bundle  Menu : Assigning AppType To : OUTBOUND ");
				callStartAPI.setSessionData("S_APP_TYPE", "OUTBOUND");	
				callStartAPI.setSessionData("S_APPNAME", "Bundle_Purchase_IVR");
			}
			
			//PREP MN FLAG GA - From MAMO IVR call transfer to Prepaid Mainmenu
			callStartAPI.setSessionData("S_PREP_MN_FLAG", callStartAPI.getSessionData((String)callStartAPI.getApplicationAPI().getApplicationData("S_PREP_MN_FLAG")));
			
			callStartAPI.setSessionData("S_CUSTOMER_SEGMENT",callStartAPI.getSessionData("cust_seg"));
			callStartAPI.setSessionData("S_CONN_TYPE",callStartAPI.getSessionData("line_Type"));
			callStartAPI.setSessionData("S_PREF_LANG",callStartAPI.getSessionData("pref_lang"));
			callStartAPI.setSessionData("S_LAST_DIGIT",util.GetLastDigits(""+callStartAPI.getSessionData("S_CLI")));
			loadDNISXl(callStartAPI);
		} catch (Exception e) {
			util.errorLog("Call Start", e);
		} finally {
			util = null;
		}
	}
	private void initialize(CallStartAPI callStartAPI) throws AudiumException {
		this.callStartAPI = callStartAPI;
		onServionStartCall();
	}
	private void invokingXMLFlags(String strFileName) {
		Utilities util=new Utilities(callStartAPI);	
		try {
			util.xltoSessionData(strFileName);
		} catch (Exception e) {
			util.errorLog("Call Start", e);
		} finally {
			util = null;
		}
	}
	private void onServionStartCall() throws AudiumException {
		callStartAPI.addToLog("Invoking CallStart Class", "Executing onServionStartCall() method in CallStart Class");
		ServionCallStart servionCallStart = new ServionCallStart();
		servionCallStart.onStartCall(callStartAPI);
	}

	private void loadDNISXl(CallStartAPI callStartAPI)
	{
		String strDNISType = "AA_PREPAID_IVR";
		String strMAMODNIS = "";
		String strAMDNIS = "";
		Utilities util = new Utilities(callStartAPI);	
		try 
		{
			strAMDNIS = (String) callStartAPI.getApplicationAPI().getApplicationData("AM_DNIS");
			strMAMODNIS=(String) callStartAPI.getApplicationAPI().getApplicationData("S_MAMO_DNIS");
			util.addToLog("Call Start  MAMO DNIS 1 : "+strMAMODNIS);
			if(util.IsNotNUllorEmpty(strAMDNIS) && strAMDNIS.contains(String.valueOf(callStartAPI.getSessionData("S_DNIS"))))
			{
				strDNISType = "AIRTEL_MONEY";
			}
			else if(util.IsNotNUllorEmpty(strMAMODNIS) && strMAMODNIS.contains(String.valueOf(callStartAPI.getSessionData("S_DNIS"))))
			{
				util.addToLog("Call Start  MAMO DNIS 2 : "+strMAMODNIS);
				strDNISType = "MAMO";
			}
			/*String[] arrAMDNIS = strAMDNIS.split(",");
			for(String strDNIS : arrAMDNIS)
			{
				if(strDNIS.equalsIgnoreCase(String.valueOf(callStartAPI.getSessionData("S_DNIS"))))
				{
					strDNISType = "AIRTEL_MONEY";
					break;
				}
			} */
			callStartAPI.addToLog("Loading DNIS", "AM DNIS data:"+strAMDNIS+"\nDNIS Type:"+strDNISType);
			callStartAPI.setSessionData("S_DNIS_TYPE", strDNISType);
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
}
