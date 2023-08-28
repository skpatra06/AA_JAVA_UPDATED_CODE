package com.host.rest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import com.audium.server.debugger.api.impl.Request;
import com.host.restbean.Data;
import com.host.restbean.Header;
import com.host.restbean.HeadersBean;
import com.host.restbean.Requestbody;
import com.host.restbean.RespList;
import com.host.restbean.RespStr;
import com.host.restbean.ResponseDataInterface;
import com.host.restbean.RestBean;

public class RestClient 
{
	private static final String S_ERROR_MSG = "S_ERROR_MSG";
	private static final String S_ERROR_CODE = "S_ERROR_CODE";
	SessionAPIHost cData;
	String strValue=null;
	String errorCode="-1";
	String errorMsg="NA";
	RestBean restBean;
	String strReqURL = "";
	int intConnectionTimeout;
	int intReadTimeout;
	HttpURLConnection conn;
	JSONObject jsonobj;
	String reqJson = "";
	ResponseDataInterface responseMapData;
	JsonResponseFilter jsonResponseFilter;
	private String HID = "NA";
	public RestClient(SessionAPIHost cData,RestBean restBean) {
		this.cData = cData;
		this.restBean=restBean;
	}

	public RestClient() {
		super();
	}
	public Map<String, Object> sendRequest(String HID) {
		this.HID  = HID;
		Map<String, Object> mapResponse = null;
		try {
			initialize(restBean);
			setJSONInput();
			mapResponse = doHostRequest(restBean);
		} catch (Exception e) {
			StringWriter str = new StringWriter();
			e.printStackTrace(new PrintWriter(str));
			addToLog("EXCEPTION   :" + str.toString());
		} finally {
			conn = null;
		}
		return mapResponse;
	}

	private void initialize(RestBean beanHost) {
		assignInParam(beanHost);
		assignDefaultSettings(beanHost);
	}


	private void assignInParam(RestBean restBean) {

		if(restBean.getRequestBody()!=null){
			reqJson = restBean.getRequestBody().getValue();
		}
	}

	private void assignDefaultSettings(RestBean restBean){
		setHostURL(restBean.getUrl());
		setConnectionTimeOut(restBean.getConntimeout());
		setReadTimeOut(restBean.getReadtimeout());
		setTransactionID();
		addToLog("REQUEST METHOD   : "+restBean.getRestmethod()+" ** CONNECTION TIMEOUT : " + intConnectionTimeout+" ** READ TIMEOUT :" + intReadTimeout);

	}
	private void setTransactionID() 
	{
		try 
		{
			String strTransID ="CISCOIVR"+getCurrDateInStr("yyyyMMddHHmmss")+System.currentTimeMillis();
			cData.setSessionData("S_CONSUMER_TXN_ID",strTransID );
		}
		catch (Exception e)
		{
			cData.ErrorLog(e);
		}
	}

	private void setConnectionTimeOut(String connectTimeout) {
		try {
			intConnectionTimeout = Integer.parseInt(connectTimeout);
		} catch (Exception e) {
			intConnectionTimeout = 5000;
		}
	}

	private void setReadTimeOut(String readTimeout) {
		try {
			intReadTimeout = Integer.parseInt(readTimeout);
		} catch (NumberFormatException e) {
			intReadTimeout = 5000;
		}
	}

	private void setHostURL(String url) {
		String value = url;
		if (value == null)
			value ="";
		strReqURL = strReqURL + value;
	}

	private void setJSONInput() {
		try {
			if(reqJson!=null && !"".equalsIgnoreCase(reqJson))
			{
				jsonobj = (JSONObject) JSONValue.parse(reqJson.trim());
				jsonobj=setRequestBody(jsonobj);
				reqJson = jsonobj.toString();
			}
		}catch (Exception e) {
			addToLog(" Exception Occurs While Forming RerquestBody - "+reqJson);
			cData.ErrorLog(e);
		}
	}
	private Map<String, Object> doHostRequest(RestBean restBean) throws Exception {
		String strRequestUrl="";
		URL url = null;
		Map<String, Object> mapResponse = null;
		HeadersBean headersBean = null;
		Requestbody requestbody=null;
		List<Header> headers = null; 
		try
		{
			disableCertificateValidation();
			strRequestUrl=setURLParamValue(strReqURL);
			if("GET".equalsIgnoreCase(restBean.getRestmethod())||"DELETE".equalsIgnoreCase(restBean.getRestmethod()))
			{

				url = new URL(strRequestUrl);
			}
			else
			{
				url = new URL(strRequestUrl);
			}
			startHostInfo(HID,"API", restBean.getRestmethod(), strRequestUrl,reqJson);
			addToLog("HOST REQUEST URL - "+url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(restBean.getRestmethod());
			conn.setConnectTimeout(intConnectionTimeout);
			conn.setReadTimeout(intReadTimeout);
			/**
			 * Set Header Fields
			 */
			headersBean = restBean.getHeaders();
			headers = headersBean.getHeader();
			StringBuilder headerRequestParam=new StringBuilder();
			headerRequestParam.append("HEADER PARAMETER - ");
			for (Header header : headers)
			{
				Boolean isLogableFlag=Boolean.valueOf(header.getSecure());
				String headerValue=(null!=header.getValue()&&header.getValue().startsWith("$"))?String.valueOf(cData.getSessionData(header.getValue().replace("$",""))):header.getValue();
				conn.setRequestProperty(header.getKey(), headerValue);
				if(isLogableFlag)
				{
					headerRequestParam.append(header.getKey()+" : ********** | ");
				}
				else 
				{
					headerRequestParam.append(header.getKey()+" : "+headerValue+" | ");
				}

			}

			addToLog(headerRequestParam.toString());
			/** 
			 * Send Request for POST Method
			 */
			if(restBean.getRestmethod().equalsIgnoreCase("POST")){
				conn.setDoInput(true);
				conn.setDoOutput(true);
				OutputStreamWriter osr = new OutputStreamWriter(conn.getOutputStream());
				BufferedWriter bw = new BufferedWriter(osr);
				bw.write(reqJson);
				bw.flush();
				bw.close();
				osr.close();	
			}

			requestbody=restBean.getRequestBody();
			try
			{
				if(requestbody!=null&&jsonobj!=null)
				{
					if(requestbody.getSecureField()!=null&&!"".equalsIgnoreCase(requestbody.getSecureField().trim())) 
					{
						String secureFiled=requestbody.getSecureField();
						if(secureFiled.contains(","))
						{
							String secureFiledName[]=secureFiled.split(",");
							for(String secureKey:secureFiledName)
							{
								if(jsonobj.containsKey(secureKey))
								{
									jsonobj.put(secureKey, "*******");
								}
							}   
						}
						else
						{
							if(jsonobj.containsKey(secureFiled))
							{
								jsonobj.put(secureFiled, "*******");
							}
						}
						addToLog("JSON REQUEST BODY -" + jsonobj);
					}
					else
					{
						addToLog("JSON REQUEST BODY -" + jsonobj);
					}
				}
			}
			catch (Exception e)
			{
				cData.ErrorLog(e);
			}

			if(mapResponse == null)
			{
				mapResponse = setResponse(restBean);
			}	

		} catch (IOException ioException) {
			StringWriter str = new StringWriter();
			ioException.printStackTrace(new PrintWriter(str));
			addToLog("IO EXCEPTION  : " + str.toString());
			mapResponse = getErrorResponse("-1","LinkDown");
			cData.setSessionData("S_RS_ERROR_REASON","LinkDown");
			endHostInfo("NA", mapResponse.toString());
		} catch (Exception e) {
			StringWriter str = new StringWriter();
			e.printStackTrace(new PrintWriter(str));
			addToLog("EXCEPTION  : " + str.toString());
			mapResponse = getErrorResponse("-1","Failure");
			endHostInfo("NA", mapResponse.toString());
		} finally {
			headersBean=null;
			headers=null;
			requestbody=null;
		}
		return mapResponse;
	}
	private Map<String, Object> getErrorResponse(String errorCode, String errorMessage)
	{
		Map<String, Object> mapResponse;
		mapResponse = new HashMap<String, Object>();
		mapResponse.put(S_ERROR_CODE, errorCode);
		mapResponse.put(S_ERROR_MSG, errorMessage);
		for (Map.Entry<String,Object> mapResponseStatus : mapResponse.entrySet())
			cData.setSessionData(mapResponseStatus.getKey(),mapResponseStatus.getValue());
		return mapResponse;
	}

	/** Reading Response from the Host 
	 * @return **/
	@SuppressWarnings("unchecked")
	public Map<String, Object> setResponse(RestBean restBean) throws Exception 
	{
		BufferedReader br;
		Map<String, Object> retMap = null;
		Map<String, Object> retStrMap;
		List<RespStr> respStrs = null;
		List<RespList> respList = null;
		retMap = new HashMap<>();
		addToLog("HOST RESPONSE CODE - " + conn.getResponseCode());
		//String responseCode=restBean.getResponsebody().getResponseCode();
		String responseCode="status";
		cData.setSessionData("S_STATUS_CODE",conn.getResponseCode());
		cData.setSessionData("S_RS_ERROR_REASON","NA");
		if(conn.getResponseCode()==200)
		{
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String strOutput;
			String strReadData;
			StringBuffer strResponse = new StringBuffer();
			while ((strReadData = br.readLine()) != null) {
				strResponse.append(strReadData);
			}
			strOutput = strResponse.toString();
			try {
				if(br!=null)
					br.close();
			}
			catch (Exception e) {
				cData.ErrorLog(e);
			}
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(strOutput);
			String statusCode="";
			addToLog("HOST RESPONSE BODY - "+json);
			if(json.containsKey(responseCode)) 
			{
				statusCode=String.valueOf(json.get(responseCode));
				if(cData.IsNotNUllorEmpty(statusCode))
				{
					cData.setSessionData("S_RS_STATUS_CODE",statusCode);
				}
				else
				{
					cData.setSessionData("S_RS_STATUS_CODE","NA");
				}
				if(!"200".equals(statusCode)&&!"Success".equalsIgnoreCase(statusCode)&&cData.IsNotNUllorEmpty(statusCode)) 
				{
					cData.setSessionData("S_HOST_FAILURE_RESPONSE",json);
					String errorReason="";
					if(json.containsKey("error"))
					{
						errorReason = String.valueOf(json.get("error"));
						if(json.containsKey("message"))
						{
							errorReason  = String.valueOf(json.get("message"));
						}
					}
					else if(json.containsKey("message")) 
					{
						errorReason = String.valueOf(json.get("message"));
					} 
					if(cData.IsNotNUllorEmpty(errorReason)) 
					{
						cData.setSessionData("S_RS_ERROR_REASON",errorReason);
						errorMsg=errorReason;
					}
					else
					{
						errorMsg="Failed";
					}

					errorCode="-1";

				} else {
					errorCode="0";
					errorMsg="Success";
				}
			} else {
				errorCode="0";
				errorMsg="Success";
			}
			cData.setSessionData("S_HOST_RS_CODE",errorCode);
			/**
			 * Parse String Json Output Values
			 */
			respStrs = restBean.getResponsebody().getData().getStrVal();
			if(respStrs!=null){
				retStrMap = getResponseDataVal(respStrs, json);
				retMap.putAll(retStrMap);
			}
			/**
			 *   Filtering JSON Packet  
			 */
			String responseFilter=restBean.getResponsebody().getResponseFilter();
			respList = restBean.getResponsebody().getData().getListVal();
			if(null!=responseFilter&&!"".equals(responseFilter)){
				Object jsonpacket=null;
				Object filteredResponseData=null;
				if(null!=respList) {
					jsonResponseFilter=new JsonResponseFilter();
					for(RespList reslist:respList){
						String listName=reslist.getListname();
						//String listVarName=reslist.getListvarname();
						if(listName.contains(":")){
							String key[]=listName.split(":");
							JSONObject jObject=(JSONObject) json.get(key[0]);
							if(jObject.containsKey(key[1])) {
								jsonpacket =jObject.get(key[1]);  
								filteredResponseData=jsonResponseFilter.filterJsonList(responseFilter, (List<Map<String, String>>) jsonpacket,cData);
								cData.setSessionData(reslist.getListvarname(),filteredResponseData);
								retMap.put(reslist.getListvarname(),filteredResponseData);
							}
						}else{
							jsonpacket =json.get(listName);
							filteredResponseData=jsonResponseFilter.filterJsonList(responseFilter, (List<Map<String, String>>) jsonpacket,cData);
							cData.setSessionData(reslist.getListvarname(),filteredResponseData);
							retMap.put(reslist.getListvarname(),filteredResponseData);
						}
					}
				}
			}else {
				if(null!=respList){
					for (RespList respListVal : respList) {
						Map<String, Object> retMapForList = setSessionDataForRespListVal(respListVal, json);
						retMap.putAll(retMapForList);
					}
				}
			}
			/**
			 * Parse List Json Output Values
			 */
			retMap.put(S_ERROR_CODE,errorCode);
			retMap.put(S_ERROR_MSG,errorMsg);
			cData.setSessionData(S_ERROR_CODE,errorCode);
			cData.setSessionData(S_ERROR_MSG,errorMsg);
			addToLog("RESPONSE DATA RETURN MAP - " + retMap.toString());
			endHostInfo("NA", responseCode);
		}
		else
		{
			errorCode="-1";
			errorMsg="Failed";
			br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			String strOutput;
			String strReadData;
			String errorReason="";
			StringBuffer strResponse = new StringBuffer();
			while ((strReadData = br.readLine()) != null) 
			{
				strResponse.append(strReadData);
			}
			strOutput = strResponse.toString();
			cData.setSessionData("S_HOST_FAILURE_RESPONSE",strOutput);
			try
			{
				JSONParser parser = new JSONParser();	
				JSONObject json = (JSONObject) parser.parse(strOutput);
				addToLog("HOST RESPONSE BODY - "+json);
				if(null!=json && json.containsKey(responseCode)) 
				{
					cData.setSessionData("S_RS_STATUS_CODE",json.get(responseCode));
				}
				if(null!=json && json.containsKey("error"))
				{
					errorReason = String.valueOf(json.get("error"));
					if(json.containsKey("message"))
					{
						errorReason  = String.valueOf(json.get("message"));
					}
				}
				else if(null!=json && json.containsKey("message")) 
				{
					errorReason = String.valueOf(json.get("message"));
				}
			}
			catch (Exception e)
			{
				cData.ErrorLog(e);
			}
			finally 
			{
				if(br!=null)
				{
					br.close();
				}
			}

			retMap = getErrorResponse(errorCode, conn.getResponseMessage());
			cData.setSessionData("S_RS_ERROR_REASON", errorReason);	
			if(cData.IsNotNUllorEmpty(errorReason)) {
				errorMsg=errorReason;
			}else {
				errorMsg="Failed";
			}
			retMap.put(S_ERROR_CODE,errorCode);
			retMap.put(S_ERROR_MSG,errorMsg);
			cData.setSessionData("S_HOST_RS_CODE",errorCode);
			cData.setSessionData(S_ERROR_CODE,errorCode);
			cData.setSessionData(S_ERROR_MSG,errorMsg);
			endHostInfo("NA", responseCode);
		}
		return retMap;
	}
	private  <T extends ResponseDataInterface> Map<String, Object> getResponseDataVal(List<T> resp,JSONObject json){
		Map<String, Object> retMap = new HashMap<String, Object>();
		String value;
		if(resp!=null){
			for (T respVal : resp) 
			{
				if(respVal.getName().contains(":")) 
				{
					value=reqMethod(respVal.getName(), json);
					cData.setSessionData(respVal.getVarname(),value);
					retMap.put(respVal.getVarname(),value);
					strValue=null;
				}else {
					cData.setSessionData(respVal.getVarname(), json.get(respVal.getName()));
					retMap.put(respVal.getVarname(), json.get(respVal.getName()));
				}
			}

		}
		return retMap;
	}
	public Map<String, Object> setSessionDataForRespListVal (RespList respListVal, JSONObject json)
	{
		Map<String, Object> retMap = new HashMap<>();
		String strListName = respListVal.getListname();
		List<Data> respData = respListVal.getDataVal();
		JSONArray jsonarray = null;
		Object responseObj=null;
		List<Map<String,String>> respListData =new ArrayList<>();
		try {
			if(strListName.contains(":")) {
				responseObj=jsonResponseFilterData(respListVal.getListname(), json);
			}else {
				responseObj=json.get(respListVal.getListname());
			}

			if(null!=responseObj) {
				if(responseObj instanceof JSONObject) {
					JSONObject dataJsonObj=(JSONObject) responseObj;
					Map<String,String> mappingValues=new HashMap<>();
					for (Data data : respData) {
						mappingValues.put(data.getName(),""+dataJsonObj.get(data.getName()));
					}
					respListData.add(mappingValues);
				}else if(responseObj instanceof JSONArray){
					jsonarray=(JSONArray) responseObj;
					for(int i=0;i<jsonarray.size();i++){
						JSONObject jsonValues=(JSONObject) jsonarray.get(i);
						Map<String,String> mappingValues=new HashMap<>();
						for (Data data : respData) {
							mappingValues.put(data.getName(),""+jsonValues.get(data.getName()));
						}
						respListData.add(mappingValues);
					}
				}
			}
			cData.setSessionData(respListVal.getListvarname(), respListData);
			retMap.put(respListVal.getListvarname(), respListData);
		}catch (Exception e) {
			cData.ErrorLog(e);
		}
		return retMap;
	}

	/**
	 *  List Response Filter
	 */
	private Object jsonResponseFilterData(String strFilter,JSONObject jsonData) {
		String filters[]=strFilter.split(":");
		Object jsonResponseObj=null;
		int i=1; 
		try {
			for(String filter:filters) {
				if(jsonData.containsKey(filter)) {
					if(jsonData.get(filter) instanceof JSONObject) {
						jsonData=(JSONObject) jsonData.get(filter);
						jsonResponseObj=jsonData;
					}else if(jsonData.get(filter) instanceof JSONArray){
						JSONArray array=(JSONArray) jsonData.get(filter);
						if(i==(filters.length)) {
							jsonResponseObj=array;
						}else {
							for(int y=0;y<array.size();y++) {
								JSONObject arr=(JSONObject) array.get(y);
								if(arr.containsKey(filters[i])) {
									jsonData=arr;
									break;
								}
							}
						}
					}
				}
				i++;
			}
		}catch (Exception e) {
			cData.ErrorLog(e);
		}
		return jsonResponseObj;
	}
	public String setURLParamValue(String url) {
		String strURL="";
		try
		{
			String strURlFrm="";
			if(url.contains("[")&&url.contains("$"))
			{
				String split[]=url.split("\\[|\\]");
				for(String splitedParamValues:split) 
				{
					if(splitedParamValues.startsWith("$S_")) {
						strURlFrm+=cData.getSessionData(splitedParamValues.replace("$", ""));
					}else {
						strURlFrm+=splitedParamValues;
					}
				}
				strURL=strURlFrm;
			}
			else
			{
				strURL=url;
			}
			if(strURL.contains("*")) 
			{
				strURL=strURL.replace("*", "&");
			}
		}
		catch (Exception e)
		{
			cData.ErrorLog(e);
		}
		return strURL;
	}

	/**
	 *  Assigning JSON Request Body
	 */
	@SuppressWarnings("unchecked")
	private JSONObject setRequestBody(JSONObject jsonObject)
	{
		for(Object key:jsonObject.keySet())
		{
			Object value=jsonObject.get(key);
			if(value instanceof JSONObject) {
				setRequestBody((JSONObject) value);
			}else if(value instanceof JSONArray) {
				JSONArray array=(JSONArray) value;
				for(int i=0;i<array.size();i++) {
					setRequestBody((JSONObject) array.get(i));
				}
			}else {
				if(value.toString().startsWith("$")){
					jsonObject.put(key,cData.getSessionData(value.toString().replace("$", "")));
				}
			}
		}
		return jsonObject;
	}

	/**
	 *  Str Response Filter
	 */
	private String reqMethod(String responseFilter,JSONObject jsonData){
		if (responseFilter.contains(":")) {
			String split[] = responseFilter.split(":");
			for (String s : split) {
				if (jsonData.containsKey(s)){
					if(jsonData.get(s) instanceof JSONObject) 
					{
						reqMethod(responseFilter, (JSONObject) jsonData.get(s));
					}else if(jsonData.get(s) instanceof JSONArray) {
						JSONArray jsonArray=(JSONArray) jsonData.get(s);
						for(int i=0;i<jsonArray.size();i++) 
						{
							reqMethod(responseFilter, (JSONObject) jsonArray.get(i));
						}
					}else{
						if(null==strValue) 
						{
							strValue=""+jsonData.get(s);
						}
					}
				}
			}
		}
		return strValue;
	}
	public void addToLog(String logMessage) {
		cData.addToLog(logMessage);
	}
	public void startHostInfo(String hostid,String hostType, String hostMethod, String hostURL,String inParam){
		/** Re-assigning Host Resp */
		cData.setSessionData("S_HOST_FAILURE_RESPONSE","NA");
		cData.setSessionData("S_RS_STATUS_CODE","NA");
		cData.setSessionData("S_RS_ERROR_REASON","NA");
		Map<String, String> data= new HashMap<String, String>();
		data.put("HOST_TYPE", hostType);
		data.put("HOST_METHOD", hostMethod);
		data.put("HOST_ID", hostid);
		data.put("HOST_URL", hostURL);
		try {
			data.put("HOST_STARTTIME", getCurrDateInStr("dd/MM/yyyy HH:mm:ss"));
		} catch (Exception e) {
			data.put("HOST_STARTTIME","NA");
		}
		data.put("HOST_IN_PARAMS","NA");
		cData.setSessionData("S_HOST_DETAIL", data);
	}
	public void endHostInfo(String outParam, String hostResponse)
	{
		String rsStatusCode= String.valueOf(cData.getSessionData("S_RS_STATUS_CODE"));
		String rsErrorReason = String.valueOf(cData.getSessionData("S_RS_ERROR_REASON"));
		String hostResponseCode= String.valueOf(cData.getSessionData("S_HOST_RS_CODE"));
		int hostCounter=0;
		Map<String, String> data=(Map<String,String>)cData.getSessionData("S_HOST_DETAIL");
		data.put("HOST_OUT_PARAMS", outParam);
		try 
		{
			data.put("HOST_ENDTIME", getCurrDateInStr("dd/MM/yyyy HH:mm:ss"));
		} catch (Exception e) {
			data.put("HOST_ENDTIME","NA");
		}
		if(null==rsErrorReason||"null".equalsIgnoreCase(rsErrorReason)||"".equalsIgnoreCase(rsErrorReason)) {
			rsErrorReason="NA";
		}
		if(null==hostResponseCode||"null".equalsIgnoreCase(hostResponseCode)||"".equalsIgnoreCase(hostResponseCode)) {
			hostResponseCode="-1";
		}
		
		/** Adding Failure Reason In Host Details Map */
		if("-1".equalsIgnoreCase(hostResponseCode)) {
			String failureResp=String.valueOf(cData.getSessionData("S_HOST_FAILURE_RESPONSE"));
			if(!cData.IsNotNUllorEmpty(failureResp)) {
				failureResp="NA";
			}
			data.put("HOST_OUT_PARAMS", failureResp);
		}
		
		data.put("HOST_RESPONSE",hostResponseCode);
		data.put("HOST_FAILURE_REASON",rsErrorReason);
		data.put("HOST_ERROR_CODE",rsStatusCode);
		hostCounter = (int) cData.getSessionData("S_HOST_COUNTER", 0);
		hostCounter = hostCounter + 1;
		cData.setSessionData("S_HOST_COUNTER", hostCounter);
		data.put("HOST_COUNTER",""+hostCounter);
		data.put("HOST_TRACKID",""+hostCounter);
		List<Map<String,String>> listHostDetails = (ArrayList<Map<String,String>>)cData.getSessionData("S_LIST_HOST_DETAILS");
		if(null==listHostDetails) {
			listHostDetails = new ArrayList<Map<String,String>>();
			listHostDetails.add(data);
		}else {
			listHostDetails.add(data);
		}
		cData.setSessionData("S_LIST_HOST_DETAILS", listHostDetails);
	}
	public String getCurrDateInStr(String strFormat)
			throws Exception
	{
		String dtCurrentDate = null;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		dtCurrentDate = sdfSR.format(Calendar.getInstance().getTime());
		return dtCurrentDate;
	}
	private  void disableCertificateValidation() throws Exception {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] 
				{ 
						new TrustAllManager() 
				};

		// Ignore differences between given hostname and certificate hostname
		HostnameVerifier hv = new TrustAllHostnameVerifier();
		// Install the all-trusting trust manager
		try 
		{
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
		} catch (Exception e) {
			StringWriter str = new StringWriter();
			e.printStackTrace(new PrintWriter(str));
			addToLog("Exception :" + str.toString());
		}
	}
	class TrustAllManager implements X509TrustManager
	{
		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			/**
			 * Default Method needs to be Overriden
			 */
		}
		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
			/**
			 * Default Method needs to be Overriden
			 */
		}
		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[0];
		}
	}
	class TrustAllHostnameVerifier implements HostnameVerifier
	{
		public boolean verify(String hostname, SSLSession session) { return true; }
	}	
}
