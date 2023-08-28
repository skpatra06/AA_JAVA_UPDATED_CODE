package com.outbound;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class ResolutionScore extends DecisionElementBase 
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		Utilities util = new Utilities(data);
		String issueResolutionScore="";
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
	        Date currDate =new Date();
			data.setSessionData("S_SURVEY_DATE_TIME",formatter.format(currDate));
			issueResolutionScore=data.getElementData("AA_OB_SF_MN_0002_DM", "value");
			data.addToLog(strElementName, "RESOLUTION SCORE : "+issueResolutionScore);
			data.setSessionData("S_ISSUE_RESOLUTION_SCORE", issueResolutionScore);
			data.setSessionData("S_SURVEY_QUESTION_FLAG", "YES");
		}catch(Exception e){
			util.errorLog(strElementName, e);
		}finally {
			util = null;
		}
		return issueResolutionScore;
	}
}
