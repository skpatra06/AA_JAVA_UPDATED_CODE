package com.common.datainternetsetttings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Balance_Check_DataInternet_Settings extends DecisionElementBase{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		Utilities util=new Utilities(data);
		List<Map<String,String>> balanceListDaid=null;
		try {
			if(util.IsValidRestAPIResponse()){
				balanceListDaid = new ArrayList<Map<String,String>>();
				String bundleType=String.valueOf(data.getSessionData("S_BUNDLE_TYPE"));
				String opcoCode=String.valueOf(data.getSessionData("S_OPCO_CODE"));
				data.addToLog(elementName, "Bundle Type : "+bundleType +" : OpcoCode : "+opcoCode);
				if (availableDataBalanceCheck(data,util,balanceListDaid,bundleType)) {
					if("MG".equalsIgnoreCase(opcoCode)&&"VOICE".equalsIgnoreCase(bundleType)) {
						/** For MG, we need to play voice expire time for this Voice category */
						util.setAudioItemListForAudio("S_DATA_INT_LIST", balanceListDaid, "S_DATA_INT_VOICE_SETTING_DC");
					}else{
						util.setAudioItemListForAudio("S_DATA_INT_LIST", balanceListDaid, "S_DATA_INT_SETTING_DC");
					}
					exitState="SU_AVL";
				}else{
					exitState="SU_NULL";
				}
			}
		} catch (Exception e) {
			util.errorLog(elementName, e);
		}finally {
			balanceListDaid=null;
			util=null;
		}
		return exitState;
	}
	private boolean availableDataBalanceCheck(DecisionElementData data, Utilities util,List<Map<String,String>> listNew,String bundleType) {
		boolean flag=false;
		List<Map<String,String>> availableDataBalance = (List<Map<String,String>>) data.getSessionData("S_BALANCE_DETAILS");
		Map<String,List<Map<String,String>>> bundleConfigData=(Map<String, List<Map<String, String>>>) data.getApplicationAPI().getApplicationData("BundleConfigData");
		try {
			if(!util.IsNotNUllorEmpty(bundleConfigData)) {
				data.addToLog("BUNDLE BALANCE ", "Null Value In Bundle Map Config ");
			}else {
				List<Map<String,String>> dataBalanceDaid=bundleConfigData.get("DAID");
				if(util.IsNotNUllorEmpty(dataBalanceDaid)) {
					if(util.IsNotNUllorEmpty(availableDataBalance)){
						for(Map<String,String> mapBundleDaidConfig:dataBalanceDaid) {
							String bundleTypeCOnf=mapBundleDaidConfig.get("BUNDLE TYPE");
							String bundleConfigDaid=mapBundleDaidConfig.get("BUNDLE DAID");
							String bundleID=String.valueOf(mapBundleDaidConfig.get("BUNDLE ID"));
							String bundleDivision=String.valueOf(mapBundleDaidConfig.get("BALANCE DIVISION"));
							Boolean balancePlayCheck=Boolean.valueOf(mapBundleDaidConfig.get("WHOLE NUMBER BALANCE FLAG"));
							if(util.IsNotNUllorEmpty(bundleConfigDaid)&&util.IsNotNUllorEmpty(bundleTypeCOnf)&&
									bundleTypeCOnf.equalsIgnoreCase(bundleType)) {
								for(Map<String, String> balanceMap:availableDataBalance)
								{
									if(bundleConfigDaid.equalsIgnoreCase(balanceMap.get("daId")))
									{
										String balance = String.valueOf(balanceMap.get("balance"));
										String unitType=String.valueOf(balanceMap.get("unitType"));
										if(util.IsNotNUllorEmpty(balance)&&0.0<Double.parseDouble(balance)&&util.IsNotNUllorEmpty(mapBundleDaidConfig.get("PHRASE ID"))) 
										{
											if("ZM".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE")))&&"DATA".equalsIgnoreCase(bundleType)){
												double dataBalance=Double.valueOf(balance);
												if((int)dataBalance>=1024) {
													dataBalance=dataBalance/1024;
													balance=String.valueOf(dataBalance);
													unitType="GB";
												}  	
											}
											balance=util.parseAmount(balance);
											if(mapBundleDaidConfig.get("PHRASE ID").endsWith(".wav")) {
												balanceMap.put("planWav",mapBundleDaidConfig.get("PHRASE ID"));	
											}else {
												balanceMap.put("planWav",mapBundleDaidConfig.get("PHRASE ID")+".wav");
											}

											if(util.IsNotNUllorEmpty(bundleID)&&balanceMap.containsKey("bundleType")&&bundleID.equalsIgnoreCase(balanceMap.get("bundleType"))&&
													util.IsNotNUllorEmpty(bundleDivision)&&!"NA".equalsIgnoreCase(bundleDivision)) {
												double balanceDiv=0.0;
												balanceDiv=util.convertToDouble("Balance Check Balance ",bundleDivision );
												if(0.0==balanceDiv)
												{
													balanceDiv=100;
												}
												balance=String.valueOf(Double.parseDouble(balance)/balanceDiv);
												//balanceMap.put("balance",balance);
											}

											if("VOICE".equalsIgnoreCase(bundleType)&&"MG".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE")))
													&&"MGA".equalsIgnoreCase(balanceMap.get("unitType"))) {
												balance=String.valueOf(Double.parseDouble(balance)/100);

											}
											balanceMap.put("balance", balance);
											if(balancePlayCheck)
											{
												double bundleBalanceInfoValue = util.convertToDouble("BUNDLE BALANCE INFO", balanceMap.get("balance"));
												balanceMap.put("balance", ""+(int)bundleBalanceInfoValue);
											}
											String expireTime = String.valueOf(balanceMap.get("expireTime"));
											data.addToLog("Available Internet Balance :",balance+", ExpireTIme : "+expireTime+", Daid : "+bundleConfigDaid +", Unit Type : "+unitType);
											balanceMap.put("unitType",unitType+".wav");
											balanceMap.put("expireTimeDateFormat",util.parseUnixDate(expireTime, "dd/MM/yyyy"));
											balanceMap.put("balanceInt",String.valueOf((int)util.convertToDouble("Balance_Check_DataInternet_Settings : ",balance)));
											listNew.add(balanceMap);
											flag=true;
										}
									}
								}
							}
						}
					}else{
						data.addToLog("Null Value In Airtime Balance Describtion List : ", ""+availableDataBalance);
					}
				}else {
					data.addToLog("BUNDLE BALANCE ", "Null Value In DAID Sheet Details ");
				}
			}
		}catch (Exception e) {
			util.errorLog("Error Fetching Data Internet Settings:", e);
		}
		return flag;
	}
}