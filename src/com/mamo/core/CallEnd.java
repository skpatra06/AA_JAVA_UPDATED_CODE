package com.mamo.core;

import com.airtel.core.ServionCallEnd;
/*
import com.audium.core.vfc.VException;
import com.audium.core.vfc.VPreference;
import com.audium.core.vfc.form.VBlock;
import com.audium.core.vfc.form.VForm;
import com.audium.core.vfc.util.VAction;
import com.audium.core.vfc.util.VMain;
*/
import com.audium.server.AudiumException;
import com.audium.server.proxy.EndCallInterface;
import com.audium.server.session.CallEndAPI;

public class CallEnd implements EndCallInterface {

	public void onEndCall(CallEndAPI callEndAPI) throws AudiumException {
		ServionCallEnd servionCallEnd = new ServionCallEnd();
		servionCallEnd.onCallEnd(callEndAPI);		
	}
}
