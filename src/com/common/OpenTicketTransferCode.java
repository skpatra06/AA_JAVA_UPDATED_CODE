package com.common;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class OpenTicketTransferCode extends ActionElementBase
{
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception
	{
		Utilities util=new Utilities(data);
		try
		{
           util.setTransferData(data, "OPEN_TICKET");
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
           util=null;
		}	

	}

}
