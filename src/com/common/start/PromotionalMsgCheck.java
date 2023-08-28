package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class PromotionalMsgCheck  extends DecisionElementBase {

	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception {
		String strExitState = "N";
		Utilities util=new Utilities(data);
		try
		{
			String strPromoMsg = (String) data.getApplicationAPI().getApplicationData("PROMO_MSG");
			if(util.IsNotNUllorEmpty(strPromoMsg))
			{
				if(!".wav".contains(strPromoMsg))
				{
					strPromoMsg+=".wav";
				}
				data.setSessionData("S_PROMO_MSG", strPromoMsg);
				strExitState ="Y";
			}
		}
		catch (Exception e)
		{
			util.errorLog(strElementName, e);
		}
		return strExitState;

	}
}
