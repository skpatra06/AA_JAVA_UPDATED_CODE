package com.hybrid.core;

import java.io.FileInputStream;
import java.util.Properties;

import com.airtel.core.ServionCallStart;
import com.audium.server.AudiumException;
import com.audium.server.proxy.StartCallInterface;
import com.audium.server.session.CallStartAPI;
import com.util.Utilities;

public class CallStart implements StartCallInterface
{
	private CallStartAPI callStartAPI;
	public void onStartCall(CallStartAPI callStartAPI) throws AudiumException	{	
		Utilities util=new Utilities(callStartAPI);
		try {
			FileInputStream  fileInputStream=null;
			Properties prop=new Properties();
			try {
				fileInputStream=new FileInputStream("C:\\airtel\\PropertyFile\\CLI_MAPPING.properties");
				prop.load(fileInputStream);
				String ani=""+callStartAPI.getAni();
				callStartAPI.addToLog("CallStart :","CLI PropertyFile Map : "+prop);
				callStartAPI.addToLog("CallStart ANI :",ani);
				if(prop.containsKey(ani)&&null!=prop.get(ani)) {
					callStartAPI.addToLog("CallStart :","CLI MAPPING VALUE :" +prop.get(ani));
					callStartAPI.setSessionData("S_CLI",String.valueOf(prop.get(ani)).trim()); 
				} 
			} catch (Exception e) {
				callStartAPI.addToLog("CallStart Exception : ",e.toString());  
			} finally {
				fileInputStream.close();	
			}
			callStartAPI.setSessionData("S_CALL_STARTDATE", util.getCurrDateInStr("yyyy-MM-dd HH:mm:ss.SSS"));
			initialize(callStartAPI);
			invokingXMLFlags("HYBRID");		
			if("TRUE".equalsIgnoreCase(""+callStartAPI.getApplicationAPI().getApplicationData("ZERO_APPEND_FLAG"))) {
				String cli=String.valueOf(callStartAPI.getSessionData("S_CLI"));
				if(util.IsNotNUllorEmpty(cli)&&!cli.startsWith("0")) {
					callStartAPI.setSessionData("S_CLI","0"+cli);
				}
			}
			callStartAPI.setSessionData("S_LAST_DIGIT",util.GetLastDigits(""+callStartAPI.getSessionData("S_CLI")));
			callStartAPI.setSessionData("S_CUSTOMER_SEGMENT",callStartAPI.getSessionData("cust_seg"));
			callStartAPI.setSessionData("S_CONN_TYPE",callStartAPI.getSessionData("line_Type"));
			callStartAPI.setSessionData("S_PREF_LANG",callStartAPI.getSessionData("pref_lang"));
		} catch (Exception e) {
			callStartAPI.addToLog("Call Start Exception : ",e.toString());
		} finally {
			util=null;
		}
	}
	private void initialize(CallStartAPI callStartAPI) throws AudiumException {
		this.callStartAPI = callStartAPI;
		onServionStartCall();
	}

	private void invokingXMLFlags(String strFileName) {
		Utilities utilities=new Utilities(callStartAPI);
		utilities.xltoSessionData(strFileName);
	}

	private void onServionStartCall() throws AudiumException {
		callStartAPI.addToLog("Invoking CallStart Class", "Executing onServionStartCall() method in CallStart Class");
		ServionCallStart servionCallStart = new ServionCallStart();
		servionCallStart.onStartCall(callStartAPI);
	}
}