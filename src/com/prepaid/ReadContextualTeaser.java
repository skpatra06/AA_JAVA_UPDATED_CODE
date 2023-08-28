package com.prepaid;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.airtel.framework.loader.bean.data.list;
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
		try {
			Map<String, Map<String,String>> flagMap = (Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("CT_PREPAID");
			data.addToLog(elementName,""+ flagMap);
			List<String> hostIDList=new ArrayList<String>();
			for(int i=1;i<=flagMap.size();i++) {
				Map<String,String> map=flagMap.get(""+i);
				String strFlag=""+map.get("Flag");
				String strMenuWav=""+map.get("Menu to be Offerred");
				String[] arrMenuWav=strMenuWav.split(",");
				StringBuilder strMenuOpt2=new StringBuilder();
				for(int j=2;j<arrMenuWav.length;j++){
					if(!arrMenuWav[j].contains("Press2")){
						strMenuOpt2.append(arrMenuWav[j]);
						strMenuOpt2.append(".wav");
						if(j!=(arrMenuWav.length-1)) {
							strMenuOpt2.append(",");
						}
					}
				}
				data.setSessionData("S_CT_AUDIO",map.get("Annoucement to be Played")+".wav");
				data.setSessionData("S_CT_MENU_OPT1",arrMenuWav[0]+".wav");
				data.setSessionData("S_CT_MENU_OPT2",strMenuOpt2.toString());
				data.setSessionData("S_OT_WITHIN_SLA_FLAG", "N");
				if("Y".equalsIgnoreCase(strFlag.trim())){
					String hostID=""+map.get("Host ID").trim();
					data.addToLog("Host ID", hostID);
					if(!hostIDList.contains(hostID)) {
						hostIDList.add(hostID);
						util.hostCall(data,hostID);
					}
					String descriptionType=map.get("Description Type");
					String conditionParam=""+map.get("Condition Param");
					String condtionValue=String.valueOf(map.get("Condition Value")).trim();
					List<String> responseDataList=new ArrayList<>();
					try {
						Object responseData=null;
						if(null!=conditionParam)
						{
							String split[]=conditionParam.split("\\.");
							responseData=data.getSessionData(split[0]);
							if(null!=responseData&& responseData instanceof List)
							{
								List<Map<String,String>> responseList=(List<Map<String, String>>) responseData;
								String splitCond[]=split[1].split("\\[|\\]");
								if(splitCond[1].contains("=")) {
									split=splitCond[1].split("=");
									String condValues[]=split[1].split(",");
									for(Map<String,String> mapRes:responseList) {
										if(mapRes.containsKey(split[0])&&util.IsNotNUllorEmpty(mapRes.get(split[0]))) {
											for(String condVal:condValues) {
												if(condVal.equalsIgnoreCase(mapRes.get(split[0]))) {
													responseDataList.add(mapRes.get(splitCond[0]));
												}
											}
										}
									}
								}
								else if("DATA".equalsIgnoreCase(descriptionType)||"VOICE".equalsIgnoreCase(descriptionType))
								{
									Map<String,Object> mapDaidConfig=(Map<String, Object>) data.getApplicationAPI().getApplicationData("BundleDaidconfig");
									if(util.IsNotNUllorEmpty(mapDaidConfig)) {
										if(mapDaidConfig.containsKey(descriptionType)&&null!=mapDaidConfig.get(descriptionType)) {
											List<String> diad=null;
											if(mapDaidConfig.get(descriptionType) instanceof List) {
												diad=(List<String>) mapDaidConfig.get(descriptionType);
											}else {
												diad=new ArrayList<String>();
												diad.add(String.valueOf(mapDaidConfig.get(descriptionType)));
											}
											for(Map<String,String> mapRes:responseList)
											{
												if(mapRes.containsKey(splitCond[1])&&util.IsNotNUllorEmpty(String.valueOf(mapRes.get(splitCond[1])))&&diad.contains(String.valueOf(mapRes.get(splitCond[1])))) {
													responseDataList.add(String.valueOf(mapRes.get(splitCond[0])));
												}
											}
										}
									}
									else {
										data.addToLog("Contextual Teaser : ", "Null Value In Map Config : BundleDaidconfig : "+descriptionType);
									}
								} 
							}
							else if(null!=responseData)
							{
								responseDataList.add(String.valueOf(responseData));
							}
						}
					}
					catch (Exception e) {
						util.errorLog(elementName, e);
					}
					String conditionType=String.valueOf(map.get("Condition Type")).trim();
					data.addToLog(elementName,"Condition Param : "+conditionParam);
					data.addToLog(elementName,"Condition type :"+ conditionType+" - RESPONSE DATA : "+descriptionType+" : "+responseDataList);
					if(responseDataList!=null&&!responseDataList.isEmpty()) {
						if("DATA".equalsIgnoreCase(descriptionType)) {
							for(String mapRes:responseDataList) {
								if(isValidExpireTimeHours( mapRes,condtionValue,"ddMMyyyyHHmm",util)){
									data.addToLog(elementName,"Desciption Type :"+descriptionType+ "Expire Time : "+mapRes);
									exitState="Yes";
									data.setSessionData("S_CT_TYPE","Data_Bundle");
									data.setSessionData("S_BUNDLE_TYPE","DATA");
									break;
								}
							}
						} else if("VOICE".equalsIgnoreCase(descriptionType)){
							for(String mapRes:responseDataList) {
								if(isValidExpireTimeHours(mapRes,condtionValue,"ddMMyyyyHHmm",util))
								{
									exitState="Yes";
									data.addToLog(elementName,"Desciption Type :"+descriptionType+ "Expire Time : "+mapRes);
									data.setSessionData("S_CT_TYPE","Voice_Bundle");
									data.setSessionData("S_BUNDLE_TYPE","VOICE");
									break;
								}
							}
						} else if("GSM_BAL".equalsIgnoreCase(descriptionType)) {
							if(isBalanceLow(responseDataList.get(0),condtionValue,util,data))
							{
								data.addToLog(elementName,"Desciption Type :"+descriptionType+ "GSM BALANCE : "+responseDataList.get(0));
								exitState="Yes";
								data.setSessionData("S_CT_TYPE","GSM_Bal");
							}
						} else if("AM_Unlock".equalsIgnoreCase(descriptionType)) {
							if(responseDataList.get(0).equalsIgnoreCase(condtionValue))
							{
								data.addToLog(elementName,"Desciption Type :"+descriptionType+ "AM UNLOCK : "+responseDataList.get(0));
								exitState="Yes";
								data.setSessionData("S_CT_TYPE","AM_Unlock");
							}
						}else if("Open_Ticket".equalsIgnoreCase(descriptionType)) {
							for(String mapRes:responseDataList) {
								Date expectedDate=null;
								try {
									expectedDate=new Date(Long.parseLong(mapRes));
								}catch (Exception e) {
									util.errorLog(elementName, e);
								}
								if(expectedDate!=null&&util.checkBeforeDate(expectedDate,"ddMMyyyyHHmm")){
									data.addToLog(elementName,"Desciption Type :"+descriptionType+ "Expire Time : "+mapRes);
									exitState="Yes";
									data.setSessionData("S_CT_TYPE","Open_Ticket");
								}
							}
							if(!responseDataList.isEmpty()&&"NO".equalsIgnoreCase(exitState)) {
								data.setSessionData("S_OT_WITHIN_SLA_FLAG", "Y");
								exitState="Yes";
							}
						}
					}
					if("Yes".equalsIgnoreCase(exitState)) {
						data.addToLog(elementName, "EXIT MENU : "+""+map.get("Case Description"));
						data.addToLog(elementName,"MENU WAV LOADED :"+ strMenuOpt2.toString());
						break;
					}
				}		
			}
		}catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
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
			if(conditionAmount>=balance) {
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
			long expireValue=diffHoursCheck(expireTime, format,util);
			data.addToLog("Contextual_Teaser_Cond_Chck", "Exxpire Time Value is " +expireValue);
			long givenValue=Long.parseLong(givenTime);
			if(0<expireValue&&givenValue>expireValue) {
				flag=true;
			}else {
				flag=false;
			}
		}catch (Exception e) {
			util.errorLog("CTC : ", e);
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
			util.errorLog("CTC : ", e);
		}
		return flag;
	}
	public long diffHoursCheck(String strDate, String strFormat,Utilities util) throws Exception {
		long diffInDays = 0;
		try 
		{
			SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
			Date dtCurrentDate = sdfSR.parse(sdfSR.format(new Date()));
			Date dtDate = new Date(Long.parseLong(strDate));
			long diff = dtDate.getTime() - dtCurrentDate.getTime();
			diffInDays = diff / ( 60 * 60 * 1000);
		}
		catch (Exception e) 
		{
			util.errorLog("CTC Teaser : ", e);
		}
		return diffInDays;
	}
}
