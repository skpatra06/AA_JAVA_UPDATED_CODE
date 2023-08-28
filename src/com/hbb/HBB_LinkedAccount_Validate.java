package com.hbb;

import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class HBB_LinkedAccount_Validate  extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		List<Map<String,String>> linkedaccount=null;
		try 
		{
			if(util.IsValidRestAPIResponse())
			{
			    linkedaccount=(List<Map<String, String>>) data.getSessionData("S_HBB_LINKED_ACCOUNTS");
				data.addToLog(elementName,"HBB LINKED ACCOUNT LIST : "+ linkedaccount);
				data.addToLog(elementName,"HBB LINKED ACCOUNT LIST SIZE : "+ linkedaccount.size());
				if(util.IsNotNUllorEmpty(linkedaccount))
				{
					if(linkedaccount.size()>0) 
					{
						exitState ="HBB";
					}
					else
					{
						exitState = "NON_HBB";
					}
				}
			}
		}
		catch(Exception e)
		{
			util.errorLog( elementName,e);
		}
		finally
		{
			linkedaccount=null;
			util=null;
		}
		return exitState;
	}
}
