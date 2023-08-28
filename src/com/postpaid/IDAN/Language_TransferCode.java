package com.postpaid.IDAN;
import com.audium.server.session.APIBase;
import com.audium.server.session.ActionElementData;
import com.audium.server.session.ElementAPI;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class Language_TransferCode extends ActionElementBase 
{
	public void doAction(String strElementName, ActionElementData data) throws Exception
	{
		Utilities util = new Utilities((APIBase)data);
		try
		{
			util.setTransferData((ElementAPI)data, "IDAN_TRANSFER_CODE");
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
		} 
	}
}