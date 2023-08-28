package com.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.airtel.framework.loader.common.AppLogging;
import com.audium.server.global.ApplicationStartAPI;
import com.audium.server.session.APIBase;


public class ATCDataLoader{

	public ApplicationStartAPI data;
	public APIBase apiBasedata;

	public ATCDataLoader(ApplicationStartAPI data) { this.data=data; }
	
	public ATCDataLoader(APIBase apiBaseData)  { this.apiBasedata=apiBaseData; }

	public void loadData(String strDirectoryPath) 
	{
		File xlFilePath =null;
		File[] arrXlFiles =null;
		try
		{
			String strATCFileName = (String) data.getApplicationData("S_ATC_FILE_NAME"); 
			AppLogging.InfoLog("ATC File name: "+strATCFileName);
			xlFilePath = new File(strDirectoryPath);	
			if(xlFilePath.isDirectory()) {
				arrXlFiles = xlFilePath.listFiles();
				if(arrXlFiles!=null) {
					for (File objFile : arrXlFiles) {
						AppLogging.ErrorLog("File: "+objFile+" \nFile name: "+objFile.getName());
						if(objFile.getName().contains(strATCFileName))
						{
							AppLogging.InfoLog("XL File Loading :"+objFile.getAbsolutePath());
							loadATCXlInMap(new FileInputStream(objFile));
						}
						else
						{
							AppLogging.InfoLog("XL File Loading :"+objFile.getAbsolutePath());
							loadXlInMap(new FileInputStream(objFile));
						}
					} 
				}else {
					AppLogging.InfoLog("ATC XL File is not available");
				}
			}else {
				AppLogging.InfoLog("ATC XL File Path is not a valid Directory");
			}
		}
		catch (Exception e) 
		{
			AppLogging.ErrorLog(e);;
		}finally {
			xlFilePath=null;
			arrXlFiles=null;
		}
	}


	private void loadXlInMap(FileInputStream file) {
		Map<String,List<Map<String,String>>> objFinalMap = null;
		List<String> objHeader = null;
		List<Map<String,String>> listMap = null;
		try
		{
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			AppLogging.InfoLog("Number of Sheet :"+workbook.getNumberOfSheets());
			objFinalMap = new HashMap<String,List< Map<String,String>>>();
			for(int page=0; page<workbook.getNumberOfSheets(); page++) {
				XSSFSheet sheet = workbook.getSheetAt(page);
				AppLogging.InfoLog("******** Loading Sheet :"+workbook.getSheetName(page)+"**********");
				objHeader = new ArrayList<String>();
				listMap = new ArrayList<Map<String,String>>();
				getHeader(objHeader, sheet);
				AppLogging.InfoLog("******** Column Header :"+objHeader.toString()+"**********");
				for(int j=1;j<=sheet.getLastRowNum();j++)
				{
					Row row=sheet.getRow(j);
					getValue(row, objHeader, listMap);
					AppLogging.InfoLog("Came Out row number:"+j);
				}
				objFinalMap.put(workbook.getSheetName(page), listMap);
			}
			data.setApplicationData("BundleConfigDataMap", objFinalMap);
			AppLogging.InfoLog("*********** Bundle Config Sheet Map : *********** : "+objFinalMap);
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}finally {
			objFinalMap = null;
			objHeader = null;
			listMap = null;
			try {
				if(file!=null)
					file.close();
			} catch (IOException e) {
				AppLogging.ErrorLog(e);
			}
		}

	}


	private void loadATCXlInMap(FileInputStream file) {
		List<String> objHeader = null;
		Map<String,Map<String,String>> objFinalMap = null;
		try
		{	
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			AppLogging.InfoLog("Number of Sheet :"+workbook.getNumberOfSheets());

			XSSFSheet sheet = workbook.getSheetAt(0);
			AppLogging.InfoLog("******** Loading Sheet :"+workbook.getSheetName(0)+"**********");

			objHeader = new ArrayList<String>();
			objFinalMap = new HashMap<String, Map<String,String>>();
			getATCHeader(objHeader, sheet);
			AppLogging.InfoLog("******** Column Header :"+objHeader.toString()+"**********");
			AppLogging.InfoLog("******** Row Count:"+sheet.getLastRowNum()+"**********");

			for(int k=2;k<objHeader.size()+2;k++)
			{
				getATCValue(sheet, objHeader, objFinalMap,k);
				AppLogging.InfoLog("Checked Header:"+objHeader.get(k-2));
			}
			data.setGlobalData("ATC_DATA", objFinalMap);
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}finally {
			objHeader = null;
			objFinalMap = null;
			try {
				if(file!=null)
					file.close();
			} catch (IOException e) {
				AppLogging.ErrorLog(e);
			}
		}

	}

	private void getATCHeader(List<String> header,XSSFSheet sheet) {
		try
		{
			Row row=sheet.getRow(0);
			Cell cell;
			int column=row.getLastCellNum();
			for(int j=2;j<column;j++) {
				cell=row.getCell(j);
				header.add(load(cell));
			}
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}

	}

	private void getATCValue(XSSFSheet sheet,List<String> header,Map<String,Map<String,String>> objFinalMap, int ColumnNum){
		Map<String, String> objMap = null;
		try {
			Cell cell1;
			Cell cell2;
			Row row;
			objMap = new HashMap<String, String>();
			int numOfRows=sheet.getLastRowNum();
			AppLogging.InfoLog("Number of values :" +numOfRows);
			for(int i=2;i<numOfRows;i++) {
				row=sheet.getRow(i);
				cell1=row.getCell(0);
				cell2=row.getCell(1);
				if(cell1!=null && cell1.getCellType()!=cell1.CELL_TYPE_BLANK&&cell2!=null && cell2.getCellType()!=cell2.CELL_TYPE_BLANK)
					objMap.put(load(cell1)+"|"+load(cell2),load(row.getCell(ColumnNum)));
			}
			AppLogging.InfoLog("Key :" +header.get(ColumnNum-2)+"Value :" +objMap.toString());
			objFinalMap.put(header.get(ColumnNum-2), objMap);
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}
		finally
		{
			objMap = null;
		}
	}

	private void getHeader(List<String> header,XSSFSheet sheet) {
		try {
			Row row=sheet.getRow(0);
			Cell cell;
			int column=row.getLastCellNum();
			for(int j=0;j<column;j++) {
				cell=row.getCell(j);
				header.add(load(cell));
			} 
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}

	}

	private void getValue(Row row,List<String> header,List<Map<String,String>> listMap){
		Map<String, String> objMap = null;
		try
		{
			Cell cell;
			objMap = new HashMap<String, String>();
			int column=row.getLastCellNum();
			for(int i=0;i<=column;i++) {
				cell=row.getCell(i);
				if(cell!=null && cell.getCellType()!=cell.CELL_TYPE_BLANK)
				{
					objMap.put(header.get(i).trim(),load(cell).trim());
				}
			} 
			AppLogging.InfoLog("Value :" +objMap.toString());
			listMap.add(objMap);
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}
		finally
		{
			objMap = null;
		}
	}

	private String load(Cell cell) {
		String exitState="";
		try {
			switch (cell.getCellType())               
			{  
			case Cell.CELL_TYPE_STRING:    
				exitState=cell.getStringCellValue(); 
				break;  
			case Cell.CELL_TYPE_NUMERIC:   
				exitState=String.valueOf((int)cell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:   
				exitState=String.valueOf((boolean)cell.getBooleanCellValue());
				break;
			default:
			}  
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}
		return exitState;
	}
	
	public void bundleSheetLoad(String filePath) {
		File xlFilePath =null;
		File[] arrXlFiles =null;
		try
		{
			String strATCFileName = (String) apiBasedata.getApplicationAPI().getApplicationData("S_ATC_FILE_NAME"); 
			AppLogging.InfoLog("ATC File name: "+strATCFileName);
			xlFilePath = new File(filePath);	
			if(xlFilePath.isDirectory()) {
				arrXlFiles = xlFilePath.listFiles();
				if(arrXlFiles!=null) {
					for (File objFile : arrXlFiles) {
						if(!objFile.getName().contains(strATCFileName)){
							AppLogging.InfoLog("File: "+objFile+" \nFile name: "+objFile.getName());
							AppLogging.InfoLog("XL File Loading :"+objFile.getAbsolutePath());
							loadXlInMap(new FileInputStream(objFile),apiBasedata);
						}
					} 
				}else {
					AppLogging.InfoLog("ATC XL File is not available");
				}
			}else {
				AppLogging.InfoLog("ATC XL File Path is not a valid Directory");
			}
		}
		catch (Exception e) 
		{
			AppLogging.ErrorLog(e);;
		}finally {
			xlFilePath=null;
			arrXlFiles=null;
		}
	}
	
	private void loadXlInMap(FileInputStream file,APIBase apiBaseData) {
		Map<String,List<Map<String,String>>> objFinalMap = null;
		List<String> objHeader = null;
		List<Map<String,String>> listMap = null;
		try
		{
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			AppLogging.InfoLog("Number of Sheet :"+workbook.getNumberOfSheets());
			objFinalMap = new HashMap<String,List< Map<String,String>>>();
			for(int page=0; page<workbook.getNumberOfSheets(); page++) {
				XSSFSheet sheet = workbook.getSheetAt(page);
				AppLogging.InfoLog("******** Loading Sheet :"+workbook.getSheetName(page)+"**********");
				objHeader = new ArrayList<String>();
				listMap = new ArrayList<Map<String,String>>();
				getHeader(objHeader, sheet);
				AppLogging.InfoLog("******** Column Header :"+objHeader.toString()+"**********");
				for(int j=1;j<=sheet.getLastRowNum();j++)
				{
					Row row=sheet.getRow(j);
					getValue(row, objHeader, listMap);
					AppLogging.InfoLog("Came Out row number:"+j);
				}
				objFinalMap.put(workbook.getSheetName(page), listMap);
			}
			apiBaseData.getApplicationAPI().setApplicationData("BundleConfigDataMap", objFinalMap);
			AppLogging.InfoLog("*********** Bundle Sheet Loaded Again : *********** : "+objFinalMap);
		}catch (Exception e) {
			AppLogging.ErrorLog(e);
		}finally {
			objFinalMap = null;
			objHeader = null;
			listMap = null;
			try {
				if(file!=null)
					file.close();
			} catch (IOException e) {
				AppLogging.ErrorLog(e);
			}
		}

	}
}