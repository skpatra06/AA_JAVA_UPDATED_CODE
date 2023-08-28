package com.common;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;

public class Get_KYC_AuthTokenGen extends ActionElementBase{

	@Override
	public void doAction(String strElementName, ActionElementData data) throws Exception
	{
		Utilities util=new Utilities(data);
		try
		{
			String accessToken=String.valueOf(data.getSessionData("S_KYC_ACCESS_TOKEN"));
			data.setSessionData("S_KYC_ACCESS_TOKEN", "Bearer "+accessToken);
			String strPUKnum =""+data.getSessionData("S_PUK_MOBILENUM");
			if (!util.IsNotNUllorEmpty(strPUKnum)) 
			{
				data.setSessionData("S_PUK_MOBILENUM", (String)data.getSessionData("S_CLI"));
			}
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
