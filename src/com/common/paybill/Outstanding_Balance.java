package com.common.paybill;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.audium.server.session.DecisionElementData;

import com.audium.server.voiceElement.DecisionElementBase;
import com.util.Utilities;


public class Outstanding_Balance extends DecisionElementBase
{
	@Override
	public String doDecision(String strElementName, DecisionElementData data) throws Exception
	{
		String exitstate = "Failure";
		Utilities util = new Utilities(data);
		try 
		{
			if(util.IsValidRestAPIResponse()) 
			{
				String outStandingBalance=String.valueOf(data.getSessionData("S_OUTSTANDING_BALANCE"));
				String expireDate=String.valueOf(data.getSessionData("S_DUE_DATE"));
				data.addToLog("Outstanding Balance : ",outStandingBalance+" DUE Date :"+expireDate);
				if(util.IsNotNUllorEmpty(outStandingBalance)&&util.IsNotNUllorEmpty(expireDate)) 
				{
					if(0.0<Double.parseDouble(outStandingBalance))
					{
						long l=Long.parseLong(expireDate);
						SimpleDateFormat dateFormat=null;
						if("MG".equalsIgnoreCase(String.valueOf(data.getSessionData("S_OPCO_CODE"))))
						{
							 dateFormat=new SimpleDateFormat("dd-MM-yyyy");
						}else {
							 dateFormat=new SimpleDateFormat("yyyy-MM-dd");
						} 
						Date date=new Date(l);
						data.addToLog("CONVERTED  DUE DATE : ",dateFormat.format(date));
						data.setSessionData("S_OUTSTANDING_BALANCE_DUEDATE", dateFormat.format(date));
						exitstate ="Success";
					}
					
					else
					{
						exitstate ="NoBalance";
					}
				}
			}		
		}
		catch(Exception e)
		{
			util.errorLog(strElementName, e);
		}
		finally
		{
			util = null;
		}
		return exitstate;
	}

}