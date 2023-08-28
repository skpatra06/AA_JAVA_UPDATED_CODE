package com.host.rest;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import com.audium.server.session.APIBase;


public class SessionAPIHost {
	private String currentElementName="";
	private APIBase data=null;
	private String customApplicationName;
	private String howCallEnded ="";
	private String strSessionID ="";			
	int intLogLevel=0; 	

	public SessionAPIHost(APIBase data,int logLevel) {
		this.data=data;
		intLogLevel=logLevel;
	}

	public Object getApplicationLevelData(String appDataKey) {
		return data.getApplicationAPI().getApplicationData(appDataKey);
	}

	public void setApplicationLevelData(String appDataKey, Object appDataValue) {
		data.getApplicationAPI().setApplicationData(appDataKey, appDataValue);
	}

	public Object getGlobalLevelData(String appDataKey) {
		return data.getApplicationAPI().getGlobalData(appDataKey);
	}

	public void setGlobalLevelData(String appDataKey, Object appDataValue) {
		data.getApplicationAPI().setGlobalData(appDataKey, appDataValue);
	}

	public String getApplicationName() {
		String result = "";
		if(null!=this.customApplicationName&&!result.isEmpty()) {
			result = this.customApplicationName;
		}else{
			result = data.getApplicationName();
		}
		return result;
	}

	public void setApplicationName(String applicationName) {
		this.customApplicationName = applicationName;
	}

	public Map<String,Object> getAllSessionData(){
		return data.getAllSessionData();
	}

	public String getCurrentElementName() {
		return currentElementName;
	}

	public void setCurrentElement(String currentElementName) {
		this.currentElementName = currentElementName;
	}


	public String getHowCallEnded(){
		return howCallEnded;
	}

	void setHowCallEnded(String howCallEnded){
		this.howCallEnded=howCallEnded;
	}

	public void setAudioPath(String strAudioPath) {
		data.addToLog("Default Audio PAth :", strAudioPath);
		data.setDefaultAudioPath(strAudioPath);
	}

	// Added for Speech integration - 11/08/2017
	public void setDocumentLanguage(String strLang) {
		data.setDocumentLanguage(strLang);
	}

	
	
	public Object getSessionData(String strSessionDataKey) {
		Object objData=data.getSessionData(strSessionDataKey);
		return objData;
	}

	public Object getSessionData(String strSessionDataKey,Object defaultValue) {
		Object objData = null;
		objData=data.getSessionData(strSessionDataKey);
		if(objData==null){
			objData=defaultValue;
		}
		return objData;
	}

	// Added for Speech integration - 16/08/2017
	public Object getElementData(String strElementName, String strElementDataKey) {
		Object objData=data.getElementData(strElementName, strElementDataKey);
		return objData;
	}

	public Object getElementData(String strElementName, String strElementDataKey, Object defaultValue) {
		Object objData = null;
		objData=data.getElementData(strElementName, strElementDataKey);
		if(objData==null){
			objData=defaultValue;
		}
		return objData;
	}

	

	public <X> void setSessionData(String strSessionKey, X strSessionData){
		try{
			data.setSessionData(strSessionKey, strSessionData);	
		}catch(Exception e){
			ErrorLog(e);
		}

	}
	public void addToLog(StringBuilder loggerSb) {
		data.addToLog("", loggerSb.toString());
		loggerSb.setLength(0);
	}

	void setSessionID(String strSessionID) {
		this.strSessionID = strSessionID;
	}

	public String getSessionID() {
		return strSessionID;
	}

	public void addToLog(String infoMessage) {
		data.addToLog("", " :" + infoMessage);
	}

	public void addToLog(String elementName, String infoMessage) {
		data.addToLog("", " :**" + elementName + "**:" + infoMessage);
	}

	public void ErrorLog(Exception e) {
		data.addToLog("", " : Exception Occured @" + e);
		StackTrace(e);
	}

	public void addToLog(String pStrMessage, int pIntLogLevel) {
		if (pIntLogLevel >= this.intLogLevel) {
			addToLog(pStrMessage);
		}
	}
	
	
	public void ErrorLog(String errorMessage, Exception e) {
		StringWriter sw = null;
		PrintWriter pw = null;

		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			data.addToLog("", " : Exception Occured @" + errorMessage+sw.toString());
		} finally {
			sw.flush();
			pw.close();
			sw = null;
			pw = null;
		}
	}
	public  boolean IsNotNUllorEmpty(String input) {
		boolean result = false;
		if (null != input && !"".equals(input)&&!"null".equalsIgnoreCase(input)) {
			result = true;
		}
		return result;
	}

	public void StackTrace(Exception e) {

		StringWriter sw = null;
		PrintWriter pw = null;

		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			data.addToLog("Error occured in :", sw.toString());
		} finally {
			sw.flush();
			pw.close();
			sw = null;
			pw = null;
		}
	}
}
