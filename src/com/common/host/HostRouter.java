	package com.common.host;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.host.db.DBInterface;
import com.host.dbbean.DB_Bean;
import com.host.rest.RestClient;
import com.host.rest.SessionAPIHost;
import com.host.restbean.RestBean;


public class HostRouter extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		SessionAPIHost sessionAPI=null;
		RestClient restClient=null;
		DBInterface dbInterface=null;
		Object beanObj=null;
		int logLevel=0;
		try { 
			/** Assiging Default Values */
			data.setSessionData("S_ERROR_MSG","NA");
			data.setSessionData("S_ERROR_CODE","NA");
			data.setSessionData("S_STATUS_CODE","NA");
			data.setSessionData("S_RS_STATUS_CODE","NA");
			data.setSessionData("S_RS_ERROR_REASON","NA");
			data.setSessionData("S_HOST_RS_CODE","-1");
			String HID=(String) data.getElementData("HOST_INTEGRATION","HID");
			data.addToLog(elementName,"The Host Access Host :"+HID);
			if(null!=HID) {
				beanObj=data.getApplicationAPI().getGlobalData(HID);
				if(null!=beanObj) {
					sessionAPI=new SessionAPIHost(data, logLevel);
					if(beanObj instanceof RestBean) {
						//RestBean
						restClient=new RestClient(sessionAPI,(RestBean) beanObj);
						restClient.sendRequest(HID);
						exitState="SU";
					} else if(beanObj instanceof DB_Bean) {    
						//DB Bean
						dbInterface=new DBInterface(HID, sessionAPI,(DB_Bean) beanObj);
						String sp=""+((DB_Bean) beanObj).getSp();
						if(null!=sp&&!"".equals(sp)&&!"null".equalsIgnoreCase(sp)) {
							data.addToLog(elementName," ---------  EXECUTE SP ---------");
							dbInterface.executeSP();
						} else {
							data.addToLog(elementName," ---------  EXECUTE QUERY  ---------");
							dbInterface.executeQuery();
						}
						exitState="SU";
					} else{
						data.addToLog(elementName," No Bean is initialized ");
					}
				} else {
					data.addToLog(elementName," Null Value Occurs In Bean Object :"+beanObj);
				}
			} else {
				data.addToLog(elementName," No Such Host config Is Found With the Given Argument Name : "+HID);
			}
		} catch(Exception e) {
			sessionAPI.ErrorLog(e);
		} finally {
			dbInterface=null;
			restClient=null;
			sessionAPI=null;
			beanObj=null;
		}
		return exitState;
	}
}
