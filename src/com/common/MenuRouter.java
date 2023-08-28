package com.common;

import java.io.FileInputStream;
import java.util.Properties;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;

public class MenuRouter extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState="ER";
		try 
		{
			String cli=""+data.getAni();
			String mappingValue=null;
			String selectedOption=data.getElementData("RoutingMenu","value");
			data.addToLog(strElementName, "Selected Option : "+selectedOption+" CLI :"+cli);
			String propertyFilePath="c:\\airtel\\PropertyFile\\CLI_DAT.properties";
			FileInputStream fileInputStream=null;
			Properties prop=new Properties();
			
			try 
			{
				fileInputStream=new FileInputStream(propertyFilePath);
				prop.load(fileInputStream);
				fileInputStream.close();
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			finally
			{
				fileInputStream=null;
			}
			if(null!=cli&&!"".equalsIgnoreCase(cli)) 
			{
				String transferCodes=(String) prop.get(cli.trim()+"|"+selectedOption);
				data.addToLog(strElementName, " Transfer Codes Details : "+transferCodes);
				String[] transferCodesVariables=transferCodes.split(",");
				data.setSessionData("S_EV1",transferCodesVariables[0]);
				data.setSessionData("S_EV2",transferCodesVariables[1]);
				data.setSessionData("S_EV3",transferCodesVariables[2]);
				data.setSessionData("S_EV4","NA");
				exitState="AgentTransfer";
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return exitState;
	}

}
