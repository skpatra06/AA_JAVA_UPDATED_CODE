package com.common.transfer;
import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class UserBarredCheck extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "Failure";
		Utilities util=new Utilities(data);
		try
		{

			String strErrCode = String.valueOf(data.getSessionData("S_ERROR_CODE"));
			String strErrReason = String.valueOf(data.getSessionData("S_ERROR_REASON"));
			if(util.IsNotNUllorEmpty(strErrCode)&&("0").equalsIgnoreCase(strErrCode))
			{
				data.addToLog(strElementName, "Within Daily/Monthly limit. Error code:"+strErrCode+" Message:"+strErrReason);
				strExitState = "SuccessN";
				data.setSessionData("S_BARRED_FLAG", "N");

			}
			else if(util.IsNotNUllorEmpty(strErrReason)&&!"NA".equalsIgnoreCase(strErrReason))
			{
				data.addToLog(strElementName, "Daily/Monthly limit exceeded. Error code:"+strErrCode+" Message:"+strErrReason);
				strExitState = "SuccessY";
				data.setSessionData("S_BARRED_FLAG", "Y");
				data.setSessionData("S_BARRED_REASON",strErrReason);
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
		return strExitState;
	}
}