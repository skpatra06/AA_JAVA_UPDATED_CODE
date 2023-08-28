package com.enterprise.core;

import com.airtel.framework.loader.common.ApplicationStartLoader;
import com.audium.server.AudiumException;
import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.proxy.StartApplicationInterface;

public class AppStart implements StartApplicationInterface {


	@Override
	public void onStartApplication(ApplicationStartAPI data) throws AudiumException 
	{
		try
		{
			ApplicationStartLoader applicationStartLoader = new ApplicationStartLoader(data);
			applicationStartLoader.loadApplication(data, "AA_ENTERPRISE");
		}
		catch (Exception ex){}
	}

}
