package com.host.rest;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;



public class JsonResponseFilter {



	public List<Map<String,String>> getJsonByNonZero (String paramName,List<Map<String,String>> jsonData,SessionAPIHost cData)
	{

		List<Map<String,String>> jsonpacket=null;
		try {
			jsonpacket = new ArrayList<>();

			jsonpacket = jsonData.stream().filter(line -> !"0".equalsIgnoreCase(""+(String) line.get(paramName))&&!"0.0".equalsIgnoreCase(""+line.get(paramName)))      
					.collect(Collectors.toList());
		}catch (Exception e) {


		}
		return jsonpacket;
	}

	public List<Map<String,String>> getJsonByParam (String paramName, String paramValue, List<Map<String,String>> jsonData,SessionAPIHost cData)
	{
		List<Map<String, String>> jsonpacket=null;
		try {
			jsonpacket= new ArrayList<>();
			jsonpacket = jsonData.stream().filter(line -> paramValue.equalsIgnoreCase(String.valueOf(line.get(paramName))))      
					.collect(Collectors.toList());                
		}catch(Exception e){
			cData.ErrorLog(e);
		}
		return jsonpacket;
	}




	public List<Map<String,String>> sortAsecJsonByParam(String sortType,String param, List<Map<String,String>> jsonData,SessionAPIHost cData)
	{
		List<Map<String,String>> jsonpacket = null;
		try 
		{
			jsonpacket =jsonData.parallelStream()
					.sorted(Comparator.comparing((t) -> Double.parseDouble(((Map) t).get(param).toString())))
					.collect(Collectors.toList());
		}catch (Exception e)
		{
			cData.ErrorLog(e);
		}
		return jsonpacket;
	}

	public List<Map<String,String>> sortDescJsonByParam(String sortType,String param, List<Map<String,String>> jsonData,SessionAPIHost cData) {
		List<Map<String,String>> jsonpacket = null;
		try 
		{
			jsonpacket = jsonData.parallelStream()
					.sorted(Comparator.comparing((t) -> Double.parseDouble(((Map) t).get(param).toString())).reversed())
					.collect(Collectors.toList()); 

		}catch (Exception e)
		{
			cData.ErrorLog(e);
		}
		return jsonpacket;
	}
	@SuppressWarnings("unused")
	public Object filterJsonList(String responseFilter,List<Map<String,String>> jsonPacket,SessionAPIHost cData) {
       String[] filterData=responseFilter.split("\\|");
		for(String filerValue : filterData){
			try {
				if(filerValue.contains("contains"))
				{
					String ParamData[]=filerValue.split("\\.")[1].split("=");
					jsonPacket =getJsonByParam(ParamData[0], ParamData[1], jsonPacket,cData);
				}
				else if(filerValue.contains("sort"))
				{  
					String ParamData[]=filerValue.split("\\.");
					if(filerValue.contains("asec")){
						jsonPacket =sortAsecJsonByParam(ParamData[0],ParamData[1], jsonPacket,cData);
					}
					else if(filerValue.contains("desc")){
						jsonPacket = sortDescJsonByParam(ParamData[0],ParamData[1], jsonPacket,cData);
					}
				}else if(filerValue.contains("nonzero")){
					String ParamData[]=filerValue.split("\\.");
					jsonPacket=getJsonByNonZero(ParamData[1], jsonPacket,cData);
				}
			}catch (Exception e) {
				cData.ErrorLog(e);
			}
		}
		return jsonPacket;
	}
}