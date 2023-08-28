package com.common.transfer;

import java.util.Map;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class PreTransferLogic extends ActionElementBase
{
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception 
	{
		Utilities util = new Utilities(data);
		Map<String,String> mapTransferDetails = null;
		Map<String,Map<String,String>> mapTransferData = null;
		Map<String,Map<String,String>> segmentBased = null;
		Map<String,Map<String,String>> DN_Based = null;
		Map<String,String> segmentDetails= null;
		Map<String,String> dnisDetails= null;
		Map<String,String> mapSkillData = null;
		Map<String,String> mapAgentType = null;
		try 
		{	
			mapAgentType =(Map<String,String>) data.getApplicationAPI().getApplicationData("AGENT_TYPE");
			String strTransCode= (String) data.getSessionData("S_TRANSFER_CODE");
			util.addToLog(strElementName+" Transfer code:"+strTransCode+"\tAgent type Map: "+mapAgentType, util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(mapAgentType)&&util.IsNotNUllorEmpty(strTransCode)&&mapAgentType.containsKey(strTransCode))
			{
				String strAgentType = mapAgentType.get(strTransCode);
				if(util.IsNotNUllorEmpty(strAgentType))
				{
					data.addToLog(strElementName, "Transfer code: "+strTransCode+" Setting agent type as "+strAgentType);
					data.setSessionData("S_AGENT_TYPE", strAgentType);
				}
				else
				{
					data.addToLog(strElementName, "Transfer code: "+strTransCode+" agent type not found hence setting as NA");
					data.setSessionData("S_AGENT_TYPE", "NA");
				}
			}

			String appName =data.getApplicationName();
			String strActiveLang = (String) data.getSessionData("S_ACTIVE_LANG");
			String priSkill =(String) data.getSessionData("S_PRI_SKILL_NAME");
			String secSkill =(String) data.getSessionData("S_SEC_SKILL_NAME");
			data.addToLog(strElementName, "PRI_SKILL: "+priSkill+" SEC_SKILL :"+secSkill);
			if(null!=priSkill&&priSkill.contains("segment")) 
			{
				segmentBased = (Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("Trans_segment");
				if(null==segmentBased) 
				{
					segmentBased=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData(appName+"_Trans_segment");
				}
				String segment =String.valueOf(data.getSessionData("S_CUSTOMER_SEGMENT"));
				data.addToLog(strElementName, "Segment:"+segment);
				util.addToLog(strElementName+ "Trans Segment Details Map : "+segmentBased,util.DEBUGLEVEL);
				if(util.IsNotNUllorEmpty(segmentBased)) 
				{
					if(segmentBased.containsKey(segment))
					{
						segmentDetails=segmentBased.get(segment);
						data.addToLog(strElementName, "Segment map:"+segmentDetails);
						priSkill=segmentDetails.get(strActiveLang+"_PRI_SKILL");
						if(null!=secSkill&&secSkill.contains("segment")) {
							secSkill=segmentDetails.get(strActiveLang+"_SEC_SKILL");
						}
					}
				}
				else 
				{
					data.addToLog(strElementName, "Null Value Occurs In Transfer Segment Map ");	
				}
				data.addToLog(strElementName, "Primary Skill name: "+priSkill +" Secondary Skill name :"+secSkill+"\t Active Lang:"+strActiveLang);
			}
			else if(null!=priSkill&&priSkill.contains("DN")) 
			{
				DN_Based=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("DN");
				String dnis=String.valueOf(data.getSessionData("S_DNIS"));
				data.addToLog(strElementName,"DNIS IS : "+dnis);
				if(util.IsNotNUllorEmpty(DN_Based)) {
					if(util.IsNotNUllorEmpty(dnis)&&DN_Based.containsKey(dnis)) {
						dnisDetails=DN_Based.get(dnis);
					} else {
						dnisDetails=DN_Based.get("DEFAULT");	
					}
					if(null!=dnisDetails) {
						priSkill=dnisDetails.get(strActiveLang+"_PRI_SKILL");
						if(null!=secSkill&&secSkill.contains("DN")) {
							secSkill=dnisDetails.get(strActiveLang+"_SEC_SKILL");;
						}
					}
					data.addToLog(strElementName, "Primary Skill name: "+priSkill +" Secondary Skill name :"+secSkill+"\t Active Lang:"+strActiveLang);
				} else {
					data.addToLog(strElementName, "Null Value Occurs In DN Details");
				}
			}

			/** Loading PQ Details */
			mapTransferData = (Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("Trans_PQ");
			if(util.IsNotNUllorEmpty(mapTransferData)) 
			{
				if(null!=priSkill)
				{
					for(String key:mapTransferData.keySet())
					{
						if(util.IsNotNUllorEmpty(key) &&(key.trim().equalsIgnoreCase(priSkill.trim())||key.trim().equalsIgnoreCase(priSkill.trim()+"_"+strActiveLang)))
						{
							mapSkillData = mapTransferData.get(key);
							data.addToLog(strElementName, "Pri Skill Data map: "+mapSkillData);
							data.setSessionData("S_PRI_PQ", mapSkillData.get("PQ ID"));
							data.setSessionData("S_PRI_CALL_TYPE", mapSkillData.get("Call Type ID"));
							data.setSessionData("S_PRI_SKILL_NAME", key);
							break;
						}
					}
					if(null!=secSkill) 
					{
						for(String key:mapTransferData.keySet()) 
						{
							if(util.IsNotNUllorEmpty(key)&&(key.trim().equalsIgnoreCase(secSkill.trim())||key.trim().equalsIgnoreCase(secSkill.trim()+"_"+strActiveLang))) {
								mapSkillData = mapTransferData.get(key);
								data.addToLog(strElementName, "Sec Skill Data map: "+mapSkillData);
								data.setSessionData("S_SEC_PQ", mapSkillData.get("PQ ID"));
								data.setSessionData("S_SEC_CALL_TYPE", mapSkillData.get("Call Type ID"));
								data.setSessionData("S_SEC_SKILL_NAME", key);
								break;
							}
						}
					}
				}
				else
				{
					data.setSessionData("S_PRI_SKILL_NAME",data.getSessionData("DEF_PRI_SKILL"));
					data.setSessionData("S_PRI_CALL_TYPE", data.getSessionData("DEF_PRI_CALL_TYPE"));
					data.setSessionData("S_SEC_SKILL_NAME", data.getSessionData("DEF_SEC_SKILL"));
					data.setSessionData("S_SEC_CALL_TYPE", data.getSessionData("DEF_SEC_CALL_TYPE"));
				}
			}
			else
			{
				data.addToLog(strElementName, "Null Value In Transfer PQ Map Details ... ");
			}

			String strDNISType = (String) data.getSessionData("S_DNIS_TYPE");
			String strOPCOCode = (String) data.getSessionData("S_OPCO_CODE");
			String strIsData = (String) data.getSessionData("S_IS_DATA");
			String strIsAMHit = (String) data.getSessionData("IS_AM_HIT");
			if(util.IsNotNUllorEmpty(strOPCOCode)&&(util.IsNotNUllorEmpty(strDNISType)||util.IsNotNUllorEmpty(strIsAMHit))&&"CG".equalsIgnoreCase(strOPCOCode)&&("AIRTEL_MONEY".equalsIgnoreCase(strDNISType)||"YES".equalsIgnoreCase(strIsAMHit)))
			{
				data.setSessionData("S_CUSTOMER_SEGMENT","AIRTEL_MONEY");
			}
			if(util.IsNotNUllorEmpty(strOPCOCode)&&util.IsNotNUllorEmpty(strIsData)&&"CG".equalsIgnoreCase(strOPCOCode)&&"YES".equalsIgnoreCase(strIsData))
			{
				data.setSessionData("S_CUSTOMER_SEGMENT","INTERNET");
			}
			/**  Assigning EV Variables */			
			mapTransferDetails = (Map<String, String>) data.getApplicationAPI().getApplicationData("AGENT_TRANSFER");
			if(util.IsNotNUllorEmpty(mapTransferDetails))
			{
				for(String evVariables :mapTransferDetails.keySet())
				{
					String evValue = mapTransferDetails.get(evVariables);
					String evFinalValue = "";
					String evValueArr[] = evValue.split("\\|");
					for(String sesName:evValueArr)
					{
						if(sesName.contains("-")) {
							String evVarArr[]=sesName.split("\\-");
							for(String sesVar:evVarArr) {
								String sesValue = (String) data.getSessionData(sesVar);
								if(!util.IsNotNUllorEmpty(sesValue)) {
									sesValue="NA";
								}
								if("".equals(evFinalValue))
									evFinalValue = sesValue;	
								else 
									evFinalValue = evFinalValue+"_"+sesValue;
							}
						}
						else
						{
							String sesValue = (String) data.getSessionData(sesName);
							if(!util.IsNotNUllorEmpty(sesValue)) {
								sesValue="NA";
							}
							if("".equals(evFinalValue))
								evFinalValue = sesValue;	
							else 
								evFinalValue = evFinalValue+"|"+sesValue;
						}
					}
					data.addToLog(strElementName, "Agent Transfer Data : "+evVariables +"="+evFinalValue);
					data.setSessionData("S_"+evVariables, evFinalValue);
					data.setSessionData("IS_AGENT_TRANSFER_FLAG","Y");
				}
			}
			else
			{
				data.addToLog(strElementName, "Null Value Occurs IN Agent Transfer Details Map ... ");
			}			
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			dnisDetails=null;
			mapAgentType=null;
			mapTransferDetails = null;
			mapTransferData = null;
			DN_Based=null;
			segmentBased = null;
			segmentDetails= null;
			mapSkillData = null;
			util=null;
		}
	}
}