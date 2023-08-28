package com.common.start;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CustomerSegmentationCheck extends DecisionElementBase
{
	@Override
	public String doDecision(String elementName, DecisionElementData data) throws Exception 
	{
		String exitState="NO";
		Utilities util=new Utilities(data);
		try 
		{
			String customerSegmentation=(String) data.getSessionData("S_CUSTOMER_SEGMENT");
			data.addToLog(elementName,"---- CUSTOMER SEGMENTATION  ---- :"+customerSegmentation);
			if(util.IsNotNUllorEmpty(customerSegmentation)) 
			{
				String segmentationSetting=(String) data.getApplicationAPI().getApplicationData("SEGMENTATION_SETTING");
				data.addToLog(elementName,"---- SEGMENTATION DETAILS ---- :"+segmentationSetting);
				if(util.IsNotNUllorEmpty(segmentationSetting))
				{
					String segments[]=segmentationSetting.split("\\|");
					for(String value:segments)
					{
						if(value.equalsIgnoreCase(customerSegmentation)) 
						{
							String strDNISType = (String) data.getSessionData("S_DNIS_TYPE");
							if("AIRTEL_MONEY".equals(strDNISType))
							{
								data.setSessionData("S_CALL_FLOW_AUD", "AA_PRP_CP_0024.wav");
							}
							else
							{
								data.setSessionData("S_CALL_FLOW_AUD", "AA_PRP_CP_0023.wav");
							}
							exitState="YES";
						}
					}
				}
				else 
				{
                 data.addToLog(elementName,"Null Value In VIP Segmentation Config Data : "+ segmentationSetting);
				}

			}
		}
		catch(Exception e)
		{
			util.errorLog(elementName, e);
		}
		finally
		{
			util=null;
		}
		return exitState;
	}

}
