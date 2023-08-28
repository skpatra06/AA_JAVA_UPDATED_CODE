package com.common.start;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.audium.server.session.DecisionElementData;
import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;

public class CustomerTreatmentClassifier extends DecisionElementBase{

	@Override
	@SuppressWarnings("unchecked")
	public String doDecision(String elementName, DecisionElementData data) throws Exception {

		String exitState="T2";
		Utilities util =new Utilities(data);
		String customerSegmentation="";
		Map<String,Map<String,String>> customerSegType= null;
		Map<String,String> treatmentType= null;
		String opcoCode=(String) data.getSessionData("S_OPCO_CODE");
		try
		{
			customerSegType=(Map<String, Map<String, String>>) data.getApplicationAPI().getGlobalData("CUSTOMER SEGMENTATION");
			if(util.IsNotNUllorEmpty(customerSegType))
			{
			util.addToLog(elementName+ " "+customerSegType,util.DEBUGLEVEL);
			customerSegmentation=(String) data.getSessionData("S_CUSTOMER_SEGMENT");
			if(customerSegType.containsKey(customerSegmentation))
			{
				treatmentType=customerSegType.get(customerSegmentation);
				String treatment=treatmentType.get("Treatment").toString().trim();
				data.addToLog(elementName, "TREATMENT TYPE :"+treatment);
				if("T1".equalsIgnoreCase(treatment))
				{
					exitState="T1";
				}else if("T3".equalsIgnoreCase(treatment)){
					util.setTransferData(data, "Customer_Treatment");
					exitState="T3"; 
				}else if("RW".equalsIgnoreCase(opcoCode)&& "T4".equalsIgnoreCase(treatment)) {
					exitState="T4";
				}
				else{
					exitState="T2";
				}
			}
			}else
			{
				data.addToLog(elementName, "Error fetching Customer Segmentation. Value:"+customerSegType);
			}
		} catch (Exception e)
		{
			util.errorLog(elementName, e);
		}finally {
			util=null;
			customerSegType = null;
			treatmentType = null;
		}
		return exitState;
	}

	
}
