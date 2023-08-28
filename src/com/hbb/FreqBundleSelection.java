package com.hbb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class FreqBundleSelection extends DecisionElementBase 
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception 
	{
		String strExitState = "";
		Utilities util=new Utilities(data);
		Map<String,String> mapBundleData = null;
		Map<String,Set<String>> mapPerioddata=null;
		Map<String,List<Map<String,String>>> mapBundlesConfig=null;
		Map<String,List<Map<String,String>>> mapBundlesPeriodwise=null;
		List<Map<String, String>> listBundlesNew=null;
		List<Map<String, String>> listBundlesTotal=null;
		List<Map<String, String>> listBundlesDup=null;
		List<Map<String,String>> listBundlesConfig=null;
		List<Map<String, String>> listBundlesPeriodwise = null;
		Set<String> setPeriodType= null;
		try
		{
			String strBundleType="HBB";
			String strUserSelect=(String) data.getSessionData("S_USER_SELECTED_PERIOD_TYPE");
			listBundlesTotal=(List<Map<String, String>>) data.getSessionData("S_BUNDLE_TOTAL");
			listBundlesDup= (List<Map<String, String>>) data.getSessionData("S_BUNDLE_REMAINING");
			util.addToLog(strElementName+ "Total Bundle Data: "+listBundlesTotal+", Remaining Bundle Data : "+listBundlesDup,util.DEBUGLEVEL);
			String strSelectedOpt=util.getMenuElementValue();
			data.addToLog(strExitState, "Selected Bundle Type is : "+strBundleType+", User input from Bundle Selection menu: "+strSelectedOpt);
			String strBundleAmount="";
			if("*".equalsIgnoreCase(strSelectedOpt)||"#".equalsIgnoreCase(strSelectedOpt))
			{
				data.setSessionData("S_REPEAT_COUNTER","0");
				strExitState=strSelectedOpt.equals("#")?"HASH":"STAR";	
			}
			else if(util.IsNotNUllorEmpty(strSelectedOpt)&&Integer.parseInt(strSelectedOpt)<=4)
			{
				mapBundleData=listBundlesTotal.get((Integer.parseInt(strSelectedOpt))-1);
				util.addFreqBundle(mapBundleData,strBundleType,data);
				strBundleAmount = String.valueOf(mapBundleData.get("amount"));
				data.setSessionData("S_PRODUCT_CODE", String.valueOf(mapBundleData.get("Product_code")));
				data.addToLog(strElementName, "Selected Bundle Amount: "+strBundleAmount);
				data.setSessionData("S_RECHARGE_AMT", strBundleAmount);
				data.setSessionData("S_REPEAT_COUNTER","0");
				strExitState = ""+strSelectedOpt;
			} 
			else if("5".equalsIgnoreCase(strSelectedOpt))
			{
				String hbbServiceName=(String) data.getSessionData("S_HBB_SERVICE_NAME");
				listBundlesTotal.clear();
				listBundlesNew= new ArrayList<Map<String, String>>();
				mapBundlesConfig= (Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
				if(util.IsNotNUllorEmpty(mapBundlesConfig)&&mapBundlesConfig.containsKey(strBundleType))
				{
					if(mapBundlesConfig.containsKey(hbbServiceName)) 
					{
						listBundlesConfig=mapBundlesConfig.get(hbbServiceName);
					}
					else
					{
						listBundlesConfig=mapBundlesConfig.get(strBundleType);
					}
					if(util.IsNotNUllorEmpty(listBundlesConfig)) 
					{
						for(Map<String,String> otherMapBundle:listBundlesConfig) 
						{
							if("NA".equals(otherMapBundle.get("Product_code")))
							{
								listBundlesNew.addAll(listBundlesConfig);
								strExitState="5_Multi_Choice";
								data.setSessionData("S_REPEAT_COUNTER","0");
								break;
							}

						}
						if(util.IsNotNUllorEmpty(listBundlesNew))
						{
							data.addToLog(strElementName, "Multi Segment found in "+strBundleType+" Bundles. Hence exit state:"+strExitState);
							util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_BUY_"+strUserSelect.toUpperCase()+"_DC", data);
							return strExitState;
						}
						//mapBundlesProcessed=(Map<String, List<Map<String, String>>>) data.getSessionData("S_BUNDLE_PERIODWISE");
						//data.addToLog(strElementName, mapBundlesProcessed.size()+" Bundle Prepaid Details fetched:"+mapBundlesProcessed);
						mapPerioddata=(Map<String, Set<String>>) data.getApplicationAPI().getApplicationData("S_PERIOD_TYPES");
						if(util.IsNotNUllorEmpty(mapPerioddata)) 
						{
							setPeriodType= mapPerioddata.get(strBundleType)!=null&&!mapPerioddata.isEmpty()?mapPerioddata.get(strBundleType):mapPerioddata.get(hbbServiceName);
							mapBundlesPeriodwise = util.splitBundlePeriodwise(setPeriodType, listBundlesConfig);
							data.addToLog(strElementName, "Periodwise data: "+mapBundlesPeriodwise);
							listBundlesPeriodwise = mapBundlesPeriodwise.get(strUserSelect);
							if(util.IsNotNUllorEmpty(listBundlesPeriodwise))
							{
								listBundlesNew.addAll(listBundlesPeriodwise);
							}
						}
						if(util.IsNotNUllorEmpty(listBundlesNew))
						{
							data.addToLog(strElementName, listBundlesNew.size()+" bundles added to Total List:"+listBundlesNew);					
							util.loadBundlesMenu(listBundlesNew, false, "S_BUNDLE_"+strBundleType+"_BUY_"+strUserSelect.toUpperCase()+"_DC", data);
							strExitState = "5";
							data.setSessionData("S_REPEAT_COUNTER","0");
						}
					}
					else
					{
						data.addToLog(strElementName, strBundleType+" Bundles details not found: "+listBundlesConfig);
					}

				}else
				{
					data.addToLog(strElementName, "Bundles Config not fetched: "+mapBundlesConfig);
				}

			}
			else
			{
				data.setSessionData("DTMF_KEYPRESS",data.getSessionData("REPEAT_DTMF_KEYPRESS"));
				strExitState="6";
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			mapBundleData = null;
			mapPerioddata=null;
			mapBundlesConfig=null;
			listBundlesNew=null;
			listBundlesTotal=null;
			listBundlesDup=null;
			setPeriodType= null;
			listBundlesConfig=null;
			util=null;
		}
		return strExitState;
	}
}