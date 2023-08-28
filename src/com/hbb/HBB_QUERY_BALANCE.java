
package com.hbb;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class HBB_QUERY_BALANCE extends DecisionElementBase 
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception 
	{
		String exitState="ER";
		Utilities util = new Utilities(data);
		String subBundleDaId="";
		String noSubAud=null;
		Map<String,Object> daId_Config_Details=null;
		List<Map<String,String>> hbbQueryBalanceList=null;
		List<String> listBundleData=null;
		List<Map<String,String>> listNewBundle=new ArrayList<Map<String,String>>();
		try 
		{
			daId_Config_Details=(Map<String,Object>) data.getApplicationAPI().getApplicationData("HBB_BUNDLE_DAID");
			if(util.IsValidRestAPIResponse())
			{
				hbbQueryBalanceList=(List<Map<String, String>>) data.getSessionData("S_HBB_QUERY_BALANCE_LIST");
				if(!util.IsNotNUllorEmpty(daId_Config_Details))
				{
					data.addToLog(strElementName, "NULL Value Occurs in HBB Bundle DaId Configs"); 
					data.setSessionData("S_HBB_DATA_BUNDLE","SILENCE.wav");
				}
				else if(util.IsNotNUllorEmpty(hbbQueryBalanceList)) 
				{
					data.addToLog("HBB BUNDLE DIAD DETAILS : ", daId_Config_Details+", HBB Query List Size : "+hbbQueryBalanceList.size() +", HBB Query Balance List : "+hbbQueryBalanceList);
					if(daId_Config_Details.get("DAID") instanceof String) 
					{
						listBundleData=new ArrayList<String>();
						listBundleData.add(""+daId_Config_Details.get("DAID"));
					}
					else if(daId_Config_Details.get("DAID") instanceof List)
					{
						listBundleData=(List<String>) daId_Config_Details.get("DAID");
					}
					String moreQueryListFlag=String.valueOf(daId_Config_Details.containsKey("HBB_MORE_QLIST_FLAG")?daId_Config_Details.get("HBB_MORE_QLIST_FLAG"):"");
					outer:for(Map<String,String> queryList:hbbQueryBalanceList)
					{
						subBundleDaId=String.valueOf(queryList.get("daId"));
						for(String bundleDaId:listBundleData)
						{
							if(util.IsNotNUllorEmpty(subBundleDaId)&&subBundleDaId.equalsIgnoreCase(String.valueOf(bundleDaId))) 
							{
								String balance=""+queryList.get("balance");
								data.addToLog(strElementName,"Subs Bundle Daid : "+String.valueOf(bundleDaId) +" : HBB Query Balance : "+balance);
								double hbbBalance=util.convertToDouble(strElementName, balance);
								if(0.0<hbbBalance)
								{
									String expireTime=String.valueOf(queryList.get("expireTime"));
									String unitType="";
									String epxireTimeFormat=util.parseUnixDate(expireTime, "yyyy-MM-dd");
									data.addToLog("HBB EXPIRETIME DUE DATE : ",epxireTimeFormat);
									unitType=queryList.get("unitType");
									if("MB".equalsIgnoreCase(unitType))
									{
										if(hbbBalance>=1024)
										{
											hbbBalance=hbbBalance/1024;
											unitType="GB";
										}
									}
									data.setSessionData("S_HBB_QUERY_BALANCE",util.parseAmount(String.valueOf( hbbBalance)));
									data.setSessionData("S_HBB_QUERY_DATE_UNIX",expireTime);
									data.setSessionData("S_HBB_QUERY_DATE", epxireTimeFormat);
									data.setSessionData("S_HBB_QBALANCE_FLAG", "YES");
									data.setSessionData("S_HBB_QBALANCE_UNITTYPE",unitType);
									queryList.put("queryBalanceUnitType", unitType);
									queryList.put("balance",util.parseAmount(String.valueOf( hbbBalance)));
									if(!util.IsNotNUllorEmpty(unitType))
									{
										data.addToLog(strElementName, "Null Value Occurs in HBB Query Balance UnitType : "+ unitType);
										unitType="SILENCE.wav";
									}
									else
									{
										unitType=unitType.toUpperCase()+".wav";
									}
									data.setSessionData("S_HBB_QBALANCE_UNIT_TYPE",unitType);
									queryList.put("unitType",unitType);
									queryList.put("unitTypePlanwav", unitType);
									queryList.put("ExpireTime",epxireTimeFormat);
									listNewBundle.add(queryList);
									exitState = "SuccessBal";
									if(!"TRUE".equalsIgnoreCase(moreQueryListFlag)) {
										break outer;
									}
								}
								else if(listNewBundle.isEmpty()) 
								{
									if("SuccessBal".equalsIgnoreCase(exitState)) {
										continue;
									}
									String noBalanceWav=String.valueOf(daId_Config_Details.get("HBB_NO_BAL"));
									if(util.IsNotNUllorEmpty(noBalanceWav))
									{
										if(!noBalanceWav.endsWith(".wav"))
										{
											noBalanceWav=noBalanceWav+".wav";
										}
									}
									else
									{
										noBalanceWav="SILENCE.wav";
									}
									data.setSessionData("S_HBB_DATA_BUNDLE",noBalanceWav);
									exitState="SuccessNoBal";
								}
							}	
						}
					}
				}
				else 
				{
					data.addToLog(strElementName, "Null Value HBB Query Balance List : "+hbbQueryBalanceList);
				}
				if(util.IsNotNUllorEmpty(listNewBundle)) {
					util.setAudioItemListForAudio("S_HBB_QBALANCE_LIST", listNewBundle, "S_HBB_QBALANCE_LIST_DC");
					data.setSessionData("S_HBB_QBUNDLE_BALANCE_LIST", listNewBundle);
				}
				else if(!"SuccessBal".equalsIgnoreCase(exitState) && !"SuccessNoBal".equalsIgnoreCase(exitState))
				{
					data.addToLog(strElementName, "No HBB Bundle Subscription");
					if(util.IsNotNUllorEmpty(hbbQueryBalanceList))
					{
						noSubAud=String.valueOf(daId_Config_Details.get("HBB_NO_SUB"));
						if(util.IsNotNUllorEmpty(noSubAud)) 
						{
							if(!noSubAud.endsWith(".wav"))
							{
								noSubAud=noSubAud+".wav";
							}
						}
						else {
							noSubAud="SILENCE.wav";
						}
						data.setSessionData("S_HBB_DATA_BUNDLE",noSubAud);
					}else {
						data.setSessionData("S_HBB_DATA_BUNDLE","SILENCE.wav");
					}
					exitState="SuccessNoBal";
				}
			}
			else
			{
				data.addToLog(strElementName, " HOST API Down  .... ");
				if(util.IsNotNUllorEmpty(daId_Config_Details))
				{
					noSubAud=String.valueOf(daId_Config_Details.get("HBB_NO_SUB"));
					if(util.IsNotNUllorEmpty(noSubAud))
					{
						if(!noSubAud.endsWith(".wav"))
						{
							noSubAud=noSubAud+".wav";
						}
					}
					else
					{
						noSubAud="SILENCE.wav";
					}
					data.setSessionData("S_HBB_DATA_BUNDLE",noSubAud);
				}
				else
				{
					data.addToLog(strElementName, "NULL Value Occurs in HBB Bundle DaId Configs");
					data.setSessionData("S_HBB_DATA_BUNDLE", "SILENCE.wav");
				}
			}		
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			listNewBundle=null;
			listBundleData=null;
			daId_Config_Details=null;
			hbbQueryBalanceList=null;
			util=null;
		}
		return exitState;
	}
}