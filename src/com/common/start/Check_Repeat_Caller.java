package com.common.start;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class Check_Repeat_Caller extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "2";
		Utilities util = new Utilities(data);
		try {
			if(util.IsValidDBSUResponse())
			{
				String featureAccessed = (String) data.getSessionData("S_LAST_FEATURE_ACCESSED");
				String featureStatus = (String)data.getSessionData("S_LAST_FEATURED_STATUS");
				if(util.IsNotNUllorEmpty(featureAccessed)&&util.IsNotNUllorEmpty(featureStatus)) {
					String[] feature = featureStatus.split("|");
					if(feature[0].equalsIgnoreCase(featureAccessed)) {
						data.addToLog(strElementName,"Success") ;
					}
					else {
						data.addToLog(strElementName, "Failure");
						exitState="1";
					}
				}
			}
		} catch (Exception e) {
			util.errorLog(strElementName, e);
		}
		finally {
			util = null;
		}
		return exitState;
	}

}
