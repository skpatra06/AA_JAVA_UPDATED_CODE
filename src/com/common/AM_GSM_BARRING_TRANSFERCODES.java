package com.common;

import com.audium.server.session.ActionElementData;
import com.audium.server.session.ReadOnlyList;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class AM_GSM_BARRING_TRANSFERCODES extends ActionElementBase 
{
	@Override
	public void doAction(String elementName, ActionElementData data) throws Exception 
	{
		Utilities util=new Utilities(data);
		try 
		{
			ReadOnlyList listElements = (ReadOnlyList) data.getElementHistory();
			String strDBElementName = "";
			for(int i=listElements.size()-1;i>=0;i--)
			{
				if((listElements.get(i)).endsWith("_DB"))
				{
					strDBElementName= listElements.get(i);
					break;
				}
			}
			data.addToLog(elementName, "Last DB name: "+strDBElementName);
			util.setTransferData(data, strDBElementName);
		}
		catch (Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			util=null;
		}
	}
}