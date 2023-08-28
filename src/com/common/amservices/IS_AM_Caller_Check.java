package com.common.amservices;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class IS_AM_Caller_Check extends DecisionElementBase
{

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String exitState = "ER";
		Utilities util = new Utilities(data);
		try 
		{
			if (util.IsValidRestAPIResponse()) 
			{
				String strStatusMsg =String.valueOf(data.getSessionData("S_RS_STATUS_CODE"));
				if(util.IsNotNUllorEmpty(strStatusMsg))
				{
					if("1003".equalsIgnoreCase(strStatusMsg))
					{
						exitState="NO";
					}
					else
					{
						exitState="YES";
					}
				}
				else
				{
					exitState="YES";

				}
			}else {
				exitState="ER";
			}	
		}
		catch (Exception e) 
		{
			util.errorLog(strElementName, e);
		} 
		finally
		{
			util = null;
		}
		return exitState;
	}

}
