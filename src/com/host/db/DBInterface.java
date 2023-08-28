package com.host.db;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.host.dbbean.DB_Bean;
import com.host.dbbean.InputParam;
import com.host.dbbean.OutputParam;
import com.host.rest.SessionAPIHost;



public class DBInterface 
{

	SessionAPIHost cData;
	DB_Bean dbconfig=null;
	ServionDBConnection sdb=null;
	int tmpInt=0;
	int Objtype=0;
	int intHost_Counter=0;

	String strHostName = null;

	DateFormat dateFormat = null;
	private static final String elementName = "DBInterface";
	private static final String S_ERROR_MSG = "S_ERROR_MSG";
	private static final String S_ERROR_CODE = "S_ERROR_CODE";
	public DBInterface(String hostName, SessionAPIHost cData,DB_Bean dbconfig)
	{
		this.cData=cData;
		this.strHostName=hostName;
		this.dbconfig=dbconfig;

	}

	public void executeSP() 
	{
		Connection conn = null;
		CallableStatement cs = null;
		String strSPName = dbconfig.getSp();
		String strCSString = "{call " + strSPName+ "(";
		StringBuffer sb=new StringBuffer();
		int intNoOfInputParams = 0, intNoOfOutputParams = 0, intNoOfParams = 0;;
		List<InputParam> inputParamList = null;
		List<OutputParam> outParamList = null;
		String paramType = "";
		String strDSName=dbconfig.getDs();
		String strLocalHostname=strHostName;
		String strSessionid = null;
		ResultSet rsTmp = null;
		try {
			sdb = new ServionDBConnection();
			conn = sdb.getConnection(strDSName,cData);

			intNoOfInputParams = dbconfig.getInputparam().getParam().size();
			intNoOfOutputParams = dbconfig.getOutparam().getParam().size();

			if(dbconfig.getOutparam().getParam().size() == 1 && ("RESULTSET").equalsIgnoreCase(dbconfig.getOutparam().getParam().get(0).getType())){
				intNoOfOutputParams = 0;
			}
			intNoOfParams = intNoOfInputParams + intNoOfOutputParams;
			if(intNoOfParams>0){
				for(int j=1;j<=intNoOfParams;j++) {
					if(j<intNoOfParams){
						strCSString += "?, ";
					}else{
						strCSString += "?)}";
					}
				}
			}else {
				strCSString += ")}";
			}

			addToLog(elementName,"CS String : " + strCSString.toString());
			if(conn==null){
				addToLog(elementName,strDSName+ " : Exception occurred While retrieving Connection Object. Connection Object is null");
				setSessionData(S_ERROR_MSG, strDSName+" : Exception occurred while retrieving Connection Object. Connection Object is null");
				setSessionData(S_ERROR_CODE,"-1");
				return;
			}else{
				cs = conn.prepareCall(strCSString,ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);	
			}
		} catch(Exception e){
			//hostInteraction(strLocalHostname,strDBURL,strSPName,conStart,conEnd,longtimeDiff,intResponse,"NA","NA",strMenuId,intHost_Counter,"Exception occurred while preparing Callable Statement. Connection Object is null");
			System.err.println(strSessionid+"@"+strDSName+" : Exception occurred while preparing Callable Statement. Connection Object is null"+getDate("dd/MM/yyyy HH:mm:ss"));
			PrintStackTrace(cData, elementName, e);
			return;
		}
		int intqueryTimeOut = dbconfig.getReadtimeout();
		/**
		 * Set Input Parameters
		 */
		int intOutParamCount=0;
		int p=0, k=0;
		inputParamList = dbconfig.getInputparam().getParam();
		for(;p<inputParamList.size();p++){
			paramType = inputParamList.get(p).getType();
			try{
				if(paramType.contains("VARCHAR")){
					String strInput=""+cData.getSessionData((String)inputParamList.get(p).getValue());
					if(strInput==null || strInput.equalsIgnoreCase("null")){
						cs.setString(p+1,null);	
					}else{
						cs.setString(p+1,strInput);
					}
					sb.append("Input Param : " + p +"="+strInput+"|");
				}else if (paramType.contains("DATE")){
					Date dateInput=(Date) cData.getSessionData(inputParamList.get(p).getValue());
					cs.setDate(p+1, dateInput);
					sb.append("Input Param : " + p +"="+dateInput+"|");
				}else if (paramType.contains("INTEGER")){
					Integer intInput=(Integer) cData.getSessionData(inputParamList.get(p).getValue());
					cs.setInt(p+1,intInput);
					sb.append("Input Param : " + p +"="+intInput+"|");
				}else if (paramType.contains("FLOAT")){
					Float floatInput=(Float)cData.getSessionData(inputParamList.get(p).getValue());
					cs.setFloat(p+1,floatInput);
					sb.append("Input Param : " + p +"="+floatInput+"|");
				}else if (paramType.contains("CHAR")) {
					String charInput=(String)cData.getSessionData(inputParamList.get(p).getValue());
					cs.setString(p+1,charInput);
					sb.append("Input Param : " + p +"="+charInput+"|");
				}else if (paramType.contains("TIMESTAMP")){
					Timestamp tsInput=(Timestamp)cData.getSessionData(inputParamList.get(p).getValue());
					cs.setTimestamp(p+1,tsInput);
					sb.append("Input Param : " + p +"="+tsInput+"|");
				}else if (paramType.contains("DOUBLE")){
					Double doubleInput=(Double)cData.getSessionData(inputParamList.get(p).getValue());
					cs.setDouble(p+1,doubleInput);
					sb.append("Input Param : " + p +"="+doubleInput+"|");
				}else if (paramType.contains("LONG")){
					Long longInput=(Long)cData.getSessionData(inputParamList.get(p).getValue());
					cs.setLong(p+1,longInput);
					sb.append("Input Param : " + p +"="+longInput+"|");
				}	

				addToLog(elementName,"Input Param List : " + sb.toString());
				startHostInfo(strHostName, "DB", "SP", strCSString, sb.toString());
			}catch(Exception e){
				setSessionData(S_ERROR_CODE, "-1");
				addToLog(elementName,strLocalHostname+ " : Exception occurred while forming procedure Input parameters");
				setSessionData(S_ERROR_MSG,strLocalHostname+ " : Exception occurred while forming procedure Input parameters");
				System.err.println(strLocalHostname+ " : Exception occurred while forming procedure Input parameters");
				PrintStackTrace(cData, elementName, e);
				return;
			}
		}

		/**
		 * Set Output Parameters
		 */
		outParamList = dbconfig.getOutparam().getParam();
		intOutParamCount =p;
		int k1 = 0;
		if(outParamList!=null && outParamList.size()>0)
		{
			for(int j=p;k1<outParamList.size();j++,k1++) {
				try {
					String strParamType =(String)outParamList.get(k1).getType();
					if(strParamType.equalsIgnoreCase("VARCHAR")) {
						cs.registerOutParameter(j+1, Types.VARCHAR);
					} else if(strParamType.equalsIgnoreCase("DATE")){
						cs.registerOutParameter(j+1,Types.DATE);
					}else if(strParamType.equalsIgnoreCase("INTEGER")){
						cs.registerOutParameter(j+1,Types.INTEGER);
					}else if(strParamType.equalsIgnoreCase("FLOAT")){							
						cs.registerOutParameter(j+1,Types.FLOAT);
					}else if(strParamType.equalsIgnoreCase("CHAR")) {
						cs.registerOutParameter(j+1,Types.CHAR);
					}else if(strParamType.equalsIgnoreCase("TIMESTAMP")) {
						cs.registerOutParameter(j+1,Types.TIMESTAMP);
					}else if(strParamType.equalsIgnoreCase("DOUBLE")){
						cs.registerOutParameter(j+1,Types.DOUBLE);
					}else if(strParamType.equalsIgnoreCase("LONG")){
						cs.registerOutParameter(j+1,Types.LONGVARCHAR);
					}else if(strParamType.equalsIgnoreCase("BOOLEAN")){
						cs.registerOutParameter(j+1,Types.BOOLEAN);
					}
				}
				catch(Exception e)
				{
					setSessionData("S_ERROR_CODE", "-1");
					addToLog(elementName,strLocalHostname+" : Exception occurred while forming Procedure ouput parameters");
					setSessionData("S_ERROR_MSG", strLocalHostname+" : Exception occurred while forming Procedure ouput parameters");
					System.err.println(strLocalHostname+" : Exception occurred while forming Procedure ouput parameters");
					PrintStackTrace(cData, elementName, e);
				}
			}
		}

		try{
			cs.setQueryTimeout(intqueryTimeOut);
			cs.execute();
			addToLog(strSPName+" -->SP Executed successfully!! ",elementName);

		}catch(Exception e){
			setSessionData("S_ERROR_CODE", "-1");
			addToLog(elementName,strSPName+" : Exception occurred while Executing Procedure");
			setSessionData("S_ERROR_MSG", strSPName+" : Exception occurred while Executing Procedure");
			//hostInteraction(strLocalHostname,strDBURL,strSPName,conStart,conEnd,longtimeDiff,intResponse,sb.toString(),"NA",strMenuId,intHost_Counter,strSPName+" : Exception occurred while Executing Procedure");
			PrintStackTrace(cData, elementName, e);
			return;
		}


		/**
		 * Assign the output to session data
		 */
		StringBuffer outputString=new StringBuffer();
		Object outputValue = null;
		outParamList = dbconfig.getOutparam().getParam();
		try{
			for(int j=0;j<outParamList.size();j++,intOutParamCount++){
				String strOutputParamType = (String)outParamList.get(j).getType();
				String strOutputParamVarName=String.valueOf(outParamList.get(j).getVarname());
				if("RESULTSET".equalsIgnoreCase(strOutputParamType)){
					addToLog("DBInterface","RESULTSET iteration");
					rsTmp = cs.getResultSet();
					ResultSetMetaData rmd=rsTmp.getMetaData();
					int ColumnCount=rmd.getColumnCount();
					List<Object> outputValList = null;
					HashMap<String,List<Object>> mapOuput=new HashMap<String,List<Object>>();
					int listcntr=0;
					while(rsTmp.next()){
						int k2=0;
						outputValList = new ArrayList<Object>();
						for(int i=1; i<=ColumnCount; i++){
							Object OuputValue=rsTmp.getObject(i);
							outputValList.add(OuputValue);
							k2++;
						}
						mapOuput.put(String.valueOf(listcntr),outputValList);
						listcntr++;
					}
					setSessionData(strOutputParamVarName, mapOuput);
					outputString.append('|');
					outputString.append(strOutputParamVarName+"="+mapOuput);
				}else{
					outputValue = cs.getObject(intOutParamCount+1);
					if(outputValue instanceof Date){
						Date dateValue=(Date)outputValue;
						SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
						String OuputDateValue=sdf.format(dateValue);
						setSessionData(strOutputParamVarName,OuputDateValue);
					}else{
						setSessionData(strOutputParamVarName,outputValue);	
					}
					outputString.append('|');
					outputString.append(strOutputParamVarName+"="+outputValue);
				}	
			}
			addToLog(strLocalHostname, outputString.toString());
			endHostInfo(outputString.toString(),"NA");
		}catch(Exception e){
			setSessionData(S_ERROR_CODE, "-1");
			addToLog(elementName,strLocalHostname+" : "+strDSName+" : "+ "Exception occurred while parsing Output Values from Database");
			setSessionData(S_ERROR_MSG, strLocalHostname+" : "+strDSName+" : "+ "Exception occurred while parsing Output Values from Database");
			System.err.println(strLocalHostname+" : "+strDSName+" : "+ "Exception occurred while parsing Output Values from Database");
			//hostInteraction(strLocalHostname,strDBURL,strSPName,conStart,conEnd,longtimeDiff,intResponse,sb.toString(),"NA",strMenuId,intHost_Counter,"Exception occurred while getting Output Values");
			PrintStackTrace(cData, elementName, e);
			return;
		}finally {
			if(sdb!=null){
				sdb=null;
			}

			if (rsTmp != null) {
				try {
					rsTmp.close();
				}
				catch(Exception e){
					setSessionData(S_ERROR_CODE, "-1");
					addToLog(elementName,strDSName+" : Exception occurred while closing Resultset");
					setSessionData(S_ERROR_MSG, strDSName+" : Exception occurred while closing Resultset");
					//	hostInteraction(strLocalHostname,strDBURL,strSPName,conStart,conEnd,longtimeDiff,intResponse,sb.toString(),"NA",strMenuId,intHost_Counter,"Exception occurred while closing Resultset");
					System.err.println(strDSName+" : Exception occurred while closing Resultset");
					PrintStackTrace(cData, elementName, e);
				}
			}
			if (cs != null) {
				try {
					cs.close();
				}
				catch(Exception e){
					setSessionData(S_ERROR_CODE, "-1");
					addToLog(elementName,strDSName+" : Exception occurred while closing Callable Statement");
					setSessionData(S_ERROR_MSG, strDSName+" : Exception occurred while closing Callable Statement");
					//hostInteraction(strLocalHostname,strDBURL,strSPName,conStart,conEnd,longtimeDiff,intResponse,sb.toString(),"NA",strMenuId,intHost_Counter,"Exception occurred while closing Callable Statement");
					PrintStackTrace(cData, elementName, e);
				}
			}
			if (conn != null) {
				try {
					conn.close();
					addToLog("connection closed successfully", elementName);
				}
				catch(Exception e){
					setSessionData(S_ERROR_CODE, "-1");
					addToLog(elementName,strDSName+" : Exception occurred while closing COnnection Object");
					setSessionData(S_ERROR_MSG,strDSName+" : Exception occurred while closing COnnection Object");
					//	hostInteraction(strLocalHostname,strDBURL,strSPName,conStart,conEnd,longtimeDiff,intResponse,sb.toString(),"NA",strMenuId,intHost_Counter,"Exception occurred while closing COnnection");
					PrintStackTrace(cData, elementName, e);
				}
			}
		}
	}  


	public Map<String,String> executeQuery() 
	{
		Map<String,String> response=new HashMap<String, String>();
		Connection conn = null;
		PreparedStatement ps = null;
		String strDSName=dbconfig.getDs();
		String strQuery = dbconfig.getQuery();
		String queryType=null;
		StringBuffer sb=new StringBuffer();
		int intqueryTimeOut = dbconfig.getReadtimeout();
		int numOfRowsAffeted=0;
		String errorCode="-1";
		String errorMsg="Failed";
		String paramType="";
		Object responseVal=null;
		ResultSet resultSet=null;
		List<InputParam> inputParamList = null;
		List<OutputParam> outputParamList = null;
		try 
		{
			sdb = new ServionDBConnection();
			conn = sdb.getConnection(strDSName,cData);
			if(conn==null)
			{
				addToLog(elementName,strDSName+ " : Exception occurred While retrieving Connection Object. Connection Object is null");
				setSessionData(S_ERROR_MSG, strDSName+" : Exception occurred while retrieving Connection Object. Connection Object is null");
				setSessionData(S_ERROR_CODE, "-1");
				response.put(S_ERROR_CODE,"-1");
				response.put(S_ERROR_MSG,strDSName+"Exception occurred while retrieving Connection Object. Connection Object is null");
				return response;
			}
			else
			{
				ps=conn.prepareStatement(strQuery);	
			}
		}
		catch (Exception e)
		{
			System.err.println(strDSName+" : Exception occurred while preparing Callable Statement. Connection Object is null"+getDate("dd/MM/yyyy HH:mm:ss"));
			PrintStackTrace(cData, elementName, e);
			return response;
		}

		/**
		 * Set Input Parameters
		 */
		int p=0, k=0;
		inputParamList = dbconfig.getInputparam().getParam();
		for(;p<inputParamList.size();p++){
			paramType = inputParamList.get(p).getType();
			try{
				if(paramType.contains("VARCHAR")){
					String sessionVarName=""+inputParamList.get(p).getValue();
					String strInput=""+cData.getSessionData(sessionVarName);
					if(null==strInput||"".equals(strInput)){
						ps.setString(p+1,null);	
					}else{
						ps.setString(p+1,""+strInput);
					}
					sb.append("Input Param : " + p +"="+strInput+"|");
				}else if (paramType.contains("DATE")){
					Date dateInput=(Date) cData.getSessionData(inputParamList.get(p).getValue());
					ps.setDate(p+1, dateInput);
					sb.append("Input Param : " + p +"="+dateInput+"|");
				}else if (paramType.contains("INTEGER")){
					Integer intInput=(Integer) cData.getSessionData(inputParamList.get(p).getValue());
					ps.setInt(p+1,intInput);
					sb.append("Input Param : " + p +"="+intInput+"|");
				}else if (paramType.contains("FLOAT")){
					Float floatInput=(Float)cData.getSessionData(inputParamList.get(p).getValue());
					ps.setFloat(p+1,floatInput);
					sb.append("Input Param : " + p +"="+floatInput+"|");
				}else if (paramType.contains("CHAR")) {
					String charInput=(String)cData.getSessionData(inputParamList.get(p).getValue());
					ps.setString(p+1,charInput);
					sb.append("Input Param : " + p +"="+charInput+"|");
				}else if (paramType.contains("TIMESTAMP")){
					Timestamp tsInput=(Timestamp)cData.getSessionData(inputParamList.get(p).getValue());
					ps.setTimestamp(p+1,tsInput);
					sb.append("Input Param : " + p +"="+tsInput+"|");
				}else if (paramType.contains("DOUBLE")){
					Double doubleInput=(Double)cData.getSessionData(inputParamList.get(p).getValue());
					ps.setDouble(p+1,doubleInput);
					sb.append("Input Param : " + p +"="+doubleInput+"|");
				}else if (paramType.contains("LONG")){
					Long longInput=(Long)cData.getSessionData(inputParamList.get(p).getValue());
					ps.setLong(p+1,longInput);
					sb.append("Input Param : " + p +"="+longInput+"|");
				}
				addToLog(elementName,"Input Param List : " + sb.toString());
			}
			catch(Exception e)
			{
				response.put(S_ERROR_CODE,"-1");
				response.put(S_ERROR_MSG,"Exception occurred while forming procedure Input parameters");
				addToLog(elementName,strHostName+ " : Exception occurred while forming procedure Input parameters");
				setSessionData(S_ERROR_CODE,"-1");
				setSessionData(S_ERROR_MSG, "Exception occurred while forming procedure Input parameters");
				PrintStackTrace(cData, elementName, e);
				addToLog(elementName," DB RESPONSE DATA : " +response.toString());
				return response;
			}
		}
		startHostInfo(strHostName, "DB", "Query", strQuery, sb.toString());
		try 
		{
			ps.setQueryTimeout(intqueryTimeOut);
			if(String.valueOf(strQuery).toUpperCase().contains("SELECT")) {
				resultSet=ps.executeQuery();
				queryType="Select";
			}else if(String.valueOf(strQuery).toUpperCase().contains("UPDATE")) {
				numOfRowsAffeted=ps.executeUpdate();
				queryType="Update";
			}else if(String.valueOf(strQuery).toUpperCase().contains("INSERT")) {
				numOfRowsAffeted=ps.executeUpdate();
				queryType="Insert";
			}
			startHostInfo(strHostName, "DB", "Query", strQuery, "NA");
			// -----> Excecuting Query <-----
			Map<String, String> resultSetMap  = null;
			List<Map<String, String>> listResultSetMap = new ArrayList<Map<String, String>>();
			List<String> columnNames = new ArrayList<String>();
			/**
			 * Set Output Parameters
			 */
			if(null!=resultSet&&!"".equals(resultSet)) 
			{
				outputParamList=dbconfig.getOutparam().getParam();
				ResultSetMetaData rsmd =resultSet.getMetaData();
				int i=0;
				while (i < rsmd.getColumnCount()) 
				{
					i++;
					System.out.print(rsmd.getColumnName(i) + "\t");
					columnNames.add(rsmd.getColumnName(i));
				}
				while(resultSet.next()) 
				{
					resultSetMap  = new HashMap<String, String>();

					for (i = 0; i < columnNames.size(); i++) 
					{
						responseVal=resultSet.getString(columnNames.get(i));
						if(responseVal instanceof Date)
						{
							Date dateValue=(Date)responseVal;
							SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
							String OuputDateValue=sdf.format(dateValue);
							resultSetMap.put(columnNames.get(i),OuputDateValue);
						}
						else
						{
							resultSetMap.put(columnNames.get(i),""+responseVal);
						}
					}
					listResultSetMap.add(resultSetMap);
				}
				addToLog(elementName, "Result Set Data : "+listResultSetMap);
				if(outputParamList.get(0).getType().equals("list")) {
					setSessionData(outputParamList.get(0).getVarname(), listResultSetMap);
				}
				errorCode="0";
				errorMsg="Success";
				addToLog(elementName,queryType+" Query Executed Successfully ");
			}
			else 
			{
				if (0!=numOfRowsAffeted)
				{
					errorCode="0";
					errorMsg="Success";
					addToLog(elementName,queryType+" Query Executed Successfully ");
				}

			}
			setSessionData(S_ERROR_CODE,errorCode);
			setSessionData(S_ERROR_MSG,errorMsg);
			response.put(S_ERROR_CODE,errorCode);
			response.put(S_ERROR_MSG,errorMsg);
			endHostInfo("NA","NA");

		}
		catch (Exception e)
		{
			setSessionData(S_ERROR_CODE, "-1");
			setSessionData(S_ERROR_MSG,"Exception occurred while Executing Query");
			addToLog(elementName,"Exception occurred while Executing Query");
			response.put(S_ERROR_CODE,"-1");
			response.put(S_ERROR_MSG,"Exception occurred while Executing Query");
			PrintStackTrace(cData, elementName, e);
		}
		finally
		{
			if (ps != null) 
			{
				try
				{
					ps.close();
				}catch(Exception e){
					setSessionData(S_ERROR_CODE, "-1");
					addToLog(elementName,strDSName+" : Exception occurred while closing Prepare Statement");
					setSessionData(S_ERROR_MSG, strDSName+" : Exception occurred while closing Prepare Statement");
					PrintStackTrace(cData, elementName, e);
				}
			}
			if (conn != null)
			{
				try 
				{
					conn.close();
					addToLog("connection closed successfully", elementName);
				}
				catch(Exception e)
				{
					setSessionData(S_ERROR_CODE, "-1");
					addToLog(elementName,strDSName+" : Exception occurred while closing  Object");
					setSessionData(S_ERROR_MSG,strDSName+" : Exception occurred while closing COnnection Object");
					PrintStackTrace(cData, elementName, e);
				}
			}
		}
		addToLog(elementName," DB RESPONSE DATA : " +response.toString());
		return response;
	}

	public Object getSessionData(String key) {
		return cData.getSessionData(key);
	}
	public void setSessionData(String key, Object value) {
		cData.setSessionData(key, value);
	}
	public void addToLog(String elementname, String logMessage) {
		cData.addToLog(elementname, logMessage);
	}

	public Object getApplicationData(String key) {
		return cData.getApplicationLevelData(key);
	}

	public String getDate(String format){
		java.util.Date date = Calendar.getInstance().getTime();  
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);  
		String strDate = dateFormat.format(date);
		return strDate;  
	}

	/**
	 *  to be modified
	 * @param cData
	 * @param elementName
	 * @param exception
	 */
	public void PrintStackTrace(SessionAPIHost cData, String elementName, Exception exception)
	{
		StringWriter sw = null;
		PrintWriter pw = null;
		try
		{
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			exception.printStackTrace(pw);
			addToLog(elementName,sw.toString());
		}
		finally
		{
			sw.flush();
			pw.close();
			sw = null;
			pw = null;
		}
	}
	public void startHostInfo(String hostid,String hostType, String hostMethod, String hostURL,String inParam)
	{
		Map<String, String> data= new HashMap<String, String>();
		data.put("HOST_TYPE", hostType);
		data.put("HOST_METHOD", hostMethod);
		data.put("HOST_ID", hostid);
		data.put("HOST_URL", hostURL);
		try 
		{
			data.put("HOST_STARTTIME", getCurrDateInStr("dd/MM/yyyy HH:mm:ss"));
		}
		catch (Exception e)
		{
			data.put("HOST_STARTTIME","NA");
		}
		data.put("HOST_IN_PARAMS",inParam);
		cData.setSessionData("S_HOST_DETAIL", data);
	}
	public void endHostInfo(String outParam, String hostResponse)
	{
		String rsStatusCode= String.valueOf(cData.getSessionData(S_ERROR_CODE));
		String rsErrorReason = String.valueOf(cData.getSessionData(S_ERROR_MSG));
		int hostCounter=0;
		Map<String, String> data=(Map<String,String>)cData.getSessionData("S_HOST_DETAIL");
		data.put("HOST_OUT_PARAMS", outParam);
		try 
		{
			data.put("HOST_ENDTIME", getCurrDateInStr("dd/MM/yyyy HH:mm:ss"));
		}
		catch (Exception e) 
		{
			data.put("HOST_ENDTIME", "NA");
		} 
		if("NA".equalsIgnoreCase(rsStatusCode)||null==rsStatusCode||"null".equalsIgnoreCase(rsStatusCode)||"".equalsIgnoreCase(rsStatusCode)) {
			rsStatusCode="-1";
		}
		
		try 
		{
			if(!"-1".equalsIgnoreCase(rsStatusCode)&&0<Integer.parseInt(rsStatusCode))
			{
				rsStatusCode="0";
			}
		}
		catch (Exception e) 
		{
			PrintStackTrace(cData, elementName, e);	
		}
		
		if(null==rsErrorReason||"null".equalsIgnoreCase(rsErrorReason)||"".equalsIgnoreCase(rsErrorReason)) {
			rsErrorReason="NA";
		}
		
		data.put("HOST_RESPONSE", rsStatusCode);
		data.put("HOST_FAILURE_REASON", rsErrorReason);
		data.put("HOST_ERROR_CODE", rsStatusCode);
		hostCounter = ((Integer)this.cData.getSessionData("S_HOST_COUNTER", Integer.valueOf(0))).intValue();
		hostCounter++;
		this.cData.setSessionData("S_HOST_COUNTER", Integer.valueOf(hostCounter));
		data.put("HOST_COUNTER",""+hostCounter);
		data.put("HOST_TRACKID",""+hostCounter);
		List<Map<String,String>> listHostDetails = (ArrayList<Map<String,String>>)cData.getSessionData("S_LIST_HOST_DETAILS");
		if(null==listHostDetails) 
		{
			listHostDetails = new ArrayList<Map<String,String>>();
			listHostDetails.add(data);
		}
		else
		{
			listHostDetails.add(data);
		}
		cData.setSessionData("S_LIST_HOST_DETAILS", listHostDetails);
	}
	public String getCurrDateInStr(String strFormat) throws Exception
	{
		String dtCurrentDate = null;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		dtCurrentDate = sdfSR.format(Calendar.getInstance().getTime());
		return dtCurrentDate;
	}
}
