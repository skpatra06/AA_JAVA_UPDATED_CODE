package com.common;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class PSB_Trade_Transfercode extends ActionElementBase{
	
	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception
	{
		Utilities util=new Utilities(data);
		try
		{
	           util.setTransferData(data, "PSB_TRADE_TRANSFER");
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
