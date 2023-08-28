package com.hbb;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class HBB_Linked_Accounts extends DecisionElementBase{

	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception{
		String exitState = "ER";
		Utilities util = new Utilities(data);
		List<Map<String,String>> linkedaccount=null;
		Map<String,String> linkedAccountMap=null;
		List<Map<String,String>> hbbLinkedList=new ArrayList<Map<String,String>>();
		try {
			linkedaccount=(List<Map<String, String>>) data.getSessionData("S_HBB_LINKED_ACCOUNTS");
			util.addToLog(elementName+" HBB LINKED ACCOUNT LIST : "+ linkedaccount,util.DEBUGLEVEL);
			if(util.IsNotNUllorEmpty(linkedaccount))
			{
				data.addToLog(elementName,"HBB LINKED ACCOUNT LIST SIZE : "+ linkedaccount.size());
				if(linkedaccount.size()==1)
				{
					linkedAccountMap=linkedaccount.get(0);
					data.addToLog(elementName, "LINKED ACCOUNT NUMB : "+linkedAccountMap.get("linkedAccountMsisdn"));
					data.setSessionData("S_HBB_NUMBER",linkedAccountMap.get("linkedAccountMsisdn"));
					exitState ="YES";
				}
				else
				{
					if(5<linkedaccount.size())
					{
						for(int i=0;i<5;i++)
						{
                           hbbLinkedList.add(linkedaccount.get(i));  
						}
						util.setAudioItemListForMenu("S_LINKED_ACCOUNT", hbbLinkedList, "S_LINKED_ACCOUNT_DC");
					}
					else 
					{
						util.setAudioItemListForMenu("S_LINKED_ACCOUNT", linkedaccount, "S_LINKED_ACCOUNT_DC");
					}
					exitState ="NO";
				}
			}
		}catch(Exception e){
			util.errorLog( elementName,e);
		}
		finally{
			linkedAccountMap=null;
			linkedaccount=null;
			hbbLinkedList=null;
			util=null;
		}
		return exitState;
	}

}
