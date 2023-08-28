package com.outbound.core;

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

	public void onStartCall(CallStartAPI callStartAPI) throws AudiumException
	{
		Utilities util = new Utilities(callStartAPI);	
		try
		{
			FileInputStream fileInputStream=null; 
			Properties prop=new Properties(); 
			try{
				fileInputStream=new FileInputStream("C:\\airtel\\PropertyFile\\CLI_MAPPING.properties");
				prop.load(fileInputStream); 
				String	ani=""+callStartAPI.getAni();
				callStartAPI.addToLog("CallStart :","CLI PropertyFile Map : "+prop);
				callStartAPI.addToLog("CallStart ANI :",ani);
				if(prop.containsKey(ani)&&null!=prop.get(ani)) 
				{
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
		}
		catch (Exception e)
		{
			util.errorLog("Call Start : ", e);
		}
		finally
		{
			util=null;	
		}
		initialize(callStartAPI);
	}
	private void initialize(CallStartAPI callStartAPI) throws AudiumException {
		this.callStartAPI = callStartAPI;
		onServionStartCall();
	}
	private void onServionStartCall() throws AudiumException {
		callStartAPI.addToLog("Invoking CallStart Class", "Executing onServionStartCall() method in CallStart Class");
		ServionCallStart servionCallStart = new ServionCallStart();
		servionCallStart.onStartCall(callStartAPI);
	}
}
