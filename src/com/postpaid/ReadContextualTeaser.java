package com.postpaid;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.host.rest.RestClient;
import com.host.rest.SessionAPIHost;
import com.host.restbean.RestBean;
import com.util.Utilities;

public class ReadContextualTeaser extends DecisionElementBase{
	DecisionElementData data;
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		this.data=data;
		String exitState = "No";
		Utilities util=new Utilities(data);
		Map<String, Map<String,String>> flagMap=null;
		List<String> hostIDList=null;
		try {
			flagMap = (Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("CT_POSTPAID");
			data.addToLog(elementName,""+ flagMap);
			hostIDList=new ArrayList<String>();
			if(util.IsNotNUllorEmpty(flagMap)) {
				for(int i=1;i<=flagMap.size();i++) 
				{
					Map<String,String> map=flagMap.get(""+i);
					if(util.IsNotNUllorEmpty(map)) {
						String strFlag=String.valueOf(map.get("Flag"));
						String strMenuWav=""+map.get("Menu to be Offerred");
						String[] arrMenuWav=strMenuWav.split(",");
						StringBuilder strMenuOpt2=new StringBuilder();
						for(int j=2;j<arrMenuWav.length;j++)
						{
							if(!arrMenuWav[j].contains("Press2"))
							{
								strMenuOpt2.append(arrMenuWav[j]);
								strMenuOpt2.append(".wav");
								if(j!=(arrMenuWav.length-1)) 
								{
									strMenuOpt2.append(",");
								}
							}
						}
						data.addToLog(elementName,"MENU WAV LOADED :"+ strMenuOpt2.toString());
						data.setSessionData("S_CT_AUDIO",map.get("Annoucement to be Played")+".wav");
						data.setSessionData("S_CT_MENU_OPT1",arrMenuWav[0]+".wav");
						data.setSessionData("S_CT_MENU_OPT2",strMenuOpt2.toString());
						if("Y".equalsIgnoreCase(strFlag.trim()))
						{
							String hostID=""+map.get("Host ID").trim();
							data.addToLog("Host ID", hostID);
							if(!hostIDList.contains(hostID)) {
								hostIDList.add(hostID);
								hostCall(data,hostID, util);
							}
							String conditionParam=""+map.get("Condition Param");
							String responseValue=getResponseData(conditionParam.trim(), data);
							String conditionType=String.valueOf(map.get("Condition Type")).trim();
							data.addToLog(elementName, "RESPONSE DATA :"+responseValue+" : Condition Type : "+conditionType+" : Condition Value : "+map.get("Condition Value"));
							switch(i)
							{
							case 1:
								if(util.IsNotNUllorEmpty(responseValue)&&responseValue.equalsIgnoreCase(String.valueOf(map.get("Condition Value")).trim()))
								{
									data.addToLog(elementName, "INDEX :"+i);
									exitState="Yes";
									data.setSessionData("S_CT_TYPE","AM_Unlock");
								}
								break;
							}
							if("Yes".equalsIgnoreCase(exitState)) {
								data.addToLog(elementName, "EXIT MENU :"+""+map.get("Case Description"));
								break;
							}
						}
					}
				}
			}
			else
			{
				data.addToLog(elementName,"Null Value Occurs In CTC Flag Map Details");
			}
		}catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			hostIDList=null;
			flagMap=null;
			util=null;
		}
		return exitState;
	}

	public boolean isBalanceLow(String availableAmount,String givenAmount,Utilities util,APIBase data) {
		boolean flag=false;
		try {
			double balance=Double.parseDouble(availableAmount);
			double conditionAmount=Double.parseDouble(givenAmount);
			data.addToLog("Available Balance :", balance+" Given COND AMT :"+conditionAmount);
			if(conditionAmount>balance) {
				flag=true;
			}else {
				flag=false;
			}
		}catch (Exception e) {
			util.errorLog("Contextual Teaser", e);
		}
		return flag;
	}
	public boolean isValidExpireTimeHours(String expireTime,String givenTime,String format,Utilities util) {///1600076890,24,HH
		boolean flag=false;
		try {
			long expireValue=util.diffInHours(expireTime, format);
			data.addToLog("Contextual_Teaser_Cond_Chck", "Exxpire Time Value is " +expireValue);
			long givenValue=Long.parseLong(givenTime);
			if(givenValue>expireValue) {
				flag=true;
			}else {
				flag=false;
			}
		}catch (Exception e) {
		}
		return flag;
	}
	public boolean isValidExpireTimeDays(String expireTime,String givenTime,String format,Utilities util) {///1600076890,24,HH
		boolean flag=false;
		try {
			long expireValue=util.dateDiffInDays(expireTime, format);
			long givenValue=Long.parseLong(givenTime);
			if(givenValue<expireValue) {
				flag=false;
			}else {
				flag=true;
			}
		}catch (Exception e) {
		}
		return flag;
	}
	private String getResponseData(String conditionParam,APIBase data)
	{
		String responseValue=null;
		String split[]=conditionParam.split("\\.");
		Object listDetails =data.getSessionData(split[0]); 
		if(listDetails instanceof List)
		{
			List<Map<String,String>> list=(List<Map<String, String>>) listDetails;
			for(Map<String,String> map:list) 
				responseValue=String.valueOf(map.get(split[1]));
		}else 
		{
			responseValue=String.valueOf(listDetails);
		}
		return responseValue;

	}
	private boolean hostCall(APIBase data,String hostName,Utilities util) {
		boolean flag=false;
		int logLevel=0;
		SessionAPIHost sessionApi=null;
		try {
			sessionApi=new SessionAPIHost(data, logLevel);
			Object objBean=data.getApplicationAPI().getGlobalData(hostName);
			System.out.println(objBean);
			RestClient restClient=new RestClient(sessionApi,(RestBean) objBean);
			restClient.sendRequest(hostName);
			if(util.IsValidRestAPIResponse()) 
			{
				flag=true;
			}
		}catch(Exception e) {
			util.errorLog("REST CLIENT :", e);
		}
		return flag;
	}

}
