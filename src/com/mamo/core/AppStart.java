package com.mamo.core;

import com.airtel.framework.loader.common.ApplicationStartLoader;
import com.audium.server.AudiumException;
import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.proxy.StartApplicationInterface;
import com.common.ATCDataLoader;

public class AppStart implements StartApplicationInterface {


	@Override
	public void onStartApplication(ApplicationStartAPI data) throws AudiumException {
		try {
			ApplicationStartLoader applicationStartLoader = new ApplicationStartLoader(data);
			applicationStartLoader.loadApplication(data, "AA_MAMO_IVR");
			
			ATCDataLoader atcExcelLoader = new ATCDataLoader(data);
			atcExcelLoader.loadData("c:\\airtel\\XL-ATC");
			} catch (Exception ex) {
		}
	}
	
}
