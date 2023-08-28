package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.airtel.framework.creator.voice.VoiceElementsInterface;
import com.airtel.framework.loader.bean.voice.Audioitem;
import com.audium.server.AudiumException;
import com.audium.server.session.APIBase;
import com.audium.server.session.DecisionElementData;
import com.audium.server.session.ElementAPI;
import com.host.db.DBInterface;
import com.host.dbbean.DB_Bean;
import com.host.rest.RestClient;
import com.host.rest.SessionAPIHost;
import com.host.restbean.RestBean;

public class Utilities {
	
	public static final String XL_ATC_PATH="c:\\airtel\\XL-ATC";
	private APIBase cData = null;
	public int DEBUGLEVEL = VoiceElementsInterface.DEBUGLEVEL; 	
	public int INFOLEVEL = VoiceElementsInterface.INFOLEVEL; 
	private int logLevel = 0;
	public Utilities(APIBase cData) {
		String logLevel = String.valueOf(cData.getApplicationAPI().getApplicationData(("CUSTOM_LOGLEVEL")));
		int loggingLevel=0;
		if(IsNotNUllorEmpty(logLevel)) 
			loggingLevel=Integer.parseInt(logLevel);
		this.logLevel = loggingLevel;
		this.cData = cData;
	}

	public static String getCurrentTime(SimpleDateFormat presentDateformat) {
		return presentDateformat.format(new Date());
	}
	public String getMenuElementValue(){
		String menuElementName=cData.getElementHistory().get(cData.getElementHistory().size()-2);
		String menuElementValue = cData.getElementData(menuElementName, "value");
		return menuElementValue;
	}
	public boolean checkTimeRange(String startDateTime, String endDateTime, String dateFormat)
			throws Exception {
		Date dateStartTime = null;
		Date dateEndTime = null;
		Date currDate = getCurrDateInDate(dateFormat);
		dateStartTime = parseToDate(startDateTime, dateFormat);
		dateEndTime = parseToDate(endDateTime, dateFormat);
		if ((currDate.after(dateStartTime) && currDate.before(dateEndTime))
				|| (currDate.equals(dateStartTime) || currDate.equals(dateEndTime))) {
			return true;
		} else {
			return false;
		}
	}

	public Date parseDate(String strDate, String strFormat) throws Exception {
		Date dtDate = null;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		sdfSR.setLenient(false);
		dtDate = sdfSR.parse(strDate);
		return dtDate;
	}

	public String parseUnixDate(String strDate,String strFormat) throws Exception
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
		Date date=new Date(Long.parseLong(strDate));
		return dateFormat.format(date);
	}
	public boolean compareDate(String Date, String startTime, String endTime, String dateFormat) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		if (sdf.format(new Date()).equals(Date)) {
			if (validateDateTime(startTime, endTime, "HHmm")) {
				return true;
			} else {
				return false;
			}
		} else {
			return true;
		}
	}

	public boolean validateDateTime(String startDateTime, String endDateTime, String dateFormat) throws Exception {
		Date dateStartTime = null;
		Date dateEndTime = null;
		Date currDate = getCurrDateInDate(dateFormat);
		dateStartTime = parseToDate(startDateTime, dateFormat);
		dateEndTime = parseToDate(endDateTime, dateFormat);
		cData.addToLog("","StartTime : " + startDateTime + " EndTime: " + endDateTime + " Current Time : " + currDate);
		if ((currDate.after(dateStartTime) && currDate.before(dateEndTime))
				|| (currDate.equals(dateStartTime) || currDate.equals(dateEndTime))) {
			cData.addToLog("","Current time falls under business hour");
			return true;
		} else {
			cData.addToLog("","Current time falls under non business hour");
			return false;
		}
	}


	public Date parseToDate(String strDate, String strFormat) throws Exception {
		Date dtDate = null;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		sdfSR.setLenient(false);
		dtDate = sdfSR.parse(strDate);
		return dtDate;
	}

	public String dateConversion(String strDate, String dateFormat, String convertedDateFormat) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		SimpleDateFormat ivrformat = new SimpleDateFormat(convertedDateFormat);
		Date df = sdf.parse(strDate);
		return ivrformat.format(df);
	}

	public Calendar addDaysToDate(String date, int daysToAdd, String strFormat) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
		Date parsedDate = null;
		Calendar now = Calendar.getInstance();
		parsedDate = sdf.parse(date);
		now.setTime(parsedDate);
		now.add(Calendar.DAY_OF_MONTH, daysToAdd);
		return now;
	}

	public long dateDiffInDays(String strDate, String strFormat) throws Exception {
		long diffInDays = 0;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		Date dtCurrentDate = sdfSR.parse(sdfSR.format(new Date()));
		Date dtDate = sdfSR.parse(strDate);
		long diff = dtDate.getTime() - dtCurrentDate.getTime();
		diffInDays = diff / (24 * 60 * 60 * 1000);
		return diffInDays;
	}

	public long diffInHours(String strDate, String strFormat) throws Exception {
		long diffInHours = 0;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		Date dtCurrentDate = sdfSR.parse(sdfSR.format(new Date()));
		Date dtDate = sdfSR.parse(strDate);
		long diff = dtDate.getTime() - dtCurrentDate.getTime();
		diffInHours = diff / (60 * 60 * 1000) % 24;
		return diffInHours;
	}

	public long diffInMinutes(String strDate, String strFormat) throws Exception {
		long diffInMinutes = 0;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		Date dtCurrentDate = sdfSR.parse(sdfSR.format(new Date()));
		Date dtDate = sdfSR.parse(strDate);
		long diff = dtDate.getTime() - dtCurrentDate.getTime();
		diffInMinutes = diff / (60 * 1000) % 60;
		return diffInMinutes;
	}

	public boolean checkBeforeDate(Date dtDate, String strFormat) throws Exception {
		boolean boolReturn = false;
		Date dtCurrentDate = getCurrDateInDate(strFormat);
		if (dtCurrentDate.compareTo(dtDate) >= 0) {
			boolReturn = true;
		}
		return boolReturn;
	}


	public Date getCurrDateInDate(String strFormat) throws Exception {
		Date dtCurrentDate = null;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		sdfSR.setLenient(false);
		dtCurrentDate = sdfSR.parse(sdfSR.format(Calendar.getInstance().getTime()));
		return dtCurrentDate;
	}

	public String getCurrDateInStr(String strFormat) throws Exception {
		String dtCurrentDate = null;
		SimpleDateFormat sdfSR = new SimpleDateFormat(strFormat);
		dtCurrentDate = sdfSR.format(Calendar.getInstance().getTime());
		return dtCurrentDate;
	}
	public String parseAmount(String amount) {
		if (amount.contains(".") && amount.split("\\.")[1].length() >= 2) {
			amount = amount.substring(0, amount.indexOf(".") + 3);
		} else if (amount.contains(".") && amount.split("\\.")[1].length() == 1) {
			amount = amount.substring(0, amount.indexOf(".") + 2);
		} 
		return amount;
	}

	public String getValidString(String strValue) {
		String tempString = "";
		if (strValue == null || "".equalsIgnoreCase(strValue) || strValue.equals("null")) {
			tempString = "NA";
		} else {
			tempString = strValue;
		}
		return tempString;
	}

	public String convertXmlToString(String strXmlFilePath) throws Exception {
		String strConvertedString = "";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = null;
		Document doc;
		Transformer transformer = null;

		dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(strXmlFilePath);

		transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc);
		transformer.transform(source, result);
		strConvertedString = sw.toString();

		return strConvertedString;
	}

	public Document stringToDom(String xmlSource) throws SAXException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xmlSource)));
	}

	public String generateRandomNumber(String arrTones[]) {
		Random random = new Random();
		String strTonePicked = arrTones[random.nextInt(arrTones.length)];
		return strTonePicked;
	}


	public String CardNumberMask(String cardNumber, int minRange, int maxRange) throws Exception {
		String maskedNumber = "";
		char charCard;
		if (cardNumber != null) {
			for (int i = 0; i < cardNumber.length(); i++) {
				charCard = cardNumber.charAt(i);
				if (i < minRange || i > maxRange) {
					maskedNumber += charCard;
				} else {
					maskedNumber += "X";
				}
			}
		} else {
			maskedNumber = cardNumber;
		}
		return maskedNumber;
	}

	public static boolean checkConsecutiveDigits(String inputNo) throws Exception {
		boolean result = false;
		inputNo = inputNo.toLowerCase();
		if (inputNo.length() == 1) {
			result = true;
		} else {
			for (int i = 1; i < inputNo.length(); i++) {
				String first = inputNo.substring(i, i + 1);
				String beforeFirst = inputNo.substring(i - 1, i);

				if (beforeFirst.compareTo(first) > 0) {
					result = false;
				}
			}
		}
		return result;
	}

	public class PropertyLoader {

		private Properties ivrconfigProp = new Properties();
		private long previousLastModifiedTimeivrconfigProp = 0;
		private File fileIVRConfigProperty = null;

		public String loadProperties(String ivrconfigPropertyFileName) {
			StringBuffer sb = new StringBuffer();
			LoadFilePath(ivrconfigPropertyFileName);
			loadivrconfigProperty(sb);
			return sb.toString();
		}

		public void LoadFilePath(String ivrconfigPropertyFileName) {
			fileIVRConfigProperty = new File(ivrconfigPropertyFileName);
		}

		public String getivrconfigProp(String ivrKey) throws Exception {
			StringBuffer sb = new StringBuffer();
			reloadivrconfigPropertyIfChanged(sb);
			return sb.toString();
		}

		private synchronized StringBuffer reloadivrconfigPropertyIfChanged(StringBuffer sb) {
			long currentLastModifiedTime = fileIVRConfigProperty.lastModified();
			if (currentLastModifiedTime > previousLastModifiedTimeivrconfigProp && currentLastModifiedTime != 0) {
				previousLastModifiedTimeivrconfigProp = currentLastModifiedTime;
				ivrconfigProp.clear();
				return loadivrconfigProperty(sb);
			} else {
				return sb;
			}
		}

		private StringBuffer loadivrconfigProperty(StringBuffer sb) {
			FileInputStream ivrconfigStream = null;
			try {
				// Reading the ivrconfig properties
				ivrconfigStream = new FileInputStream(fileIVRConfigProperty);
				ivrconfigProp.load(ivrconfigStream);
			} catch (Exception e) {
				sb.append("AppProperties :: " + "Error in Loading the IVR Property File.Exception:" + e.getMessage());
			} finally {
				try {
					// Closing the properties
					if (ivrconfigStream != null) {
						ivrconfigStream.close();
					}
				} catch (Exception e) {
					sb.append(
							"AppProperties :: " + "Error in closing the IVR Property File.Exception=" + e.getMessage());
				}
			}
			return sb;
		}
	}


	public void makeFileHiddenReadOnly(String filePath) throws Exception {
		String strWin32command = "attrib +h +r " + filePath;
		Runtime.getRuntime().exec(strWin32command);
	}

	public void makeFileUnHiddenEditable(String filePath) throws Exception {
		String strWin32command = "attrib -h -r " + filePath;
		Runtime.getRuntime().exec(strWin32command);
	}

	public String getMaskedNumber(String strCCNumber) throws Exception {
		String strMaskedCCNum = "";
		int length = 0;

		length = strCCNumber.length();
		for (int i = 7; i <= length - 4; i++) {
			strMaskedCCNum += "X";
		}
		strMaskedCCNum = strCCNumber.substring(0, 6) + strMaskedCCNum + strCCNumber.substring(length - 4, length);

		return strMaskedCCNum;
	}

	public  boolean IsNotNUllorEmpty(String input) {
		boolean result = false;
		if (null != input && !"".equals(input)&&!"null".equalsIgnoreCase(input)) {
			result = true;
		}
		return result;
	}
	public int convertToInt(String sessionVaraibleName) {
		int convertedValue = 0;
		String activeBalance=(String) cData.getSessionData(sessionVaraibleName);
		activeBalance = activeBalance.replaceAll(",", "");
		try {
			cData.setSessionData(sessionVaraibleName, activeBalance);
		} catch (AudiumException e) {
			cData.addToLog("Exception in double convertion",""+e);
		}
		if(IsNotNUllorEmpty(activeBalance)) {
			try {
				convertedValue = Integer.parseInt(activeBalance);
			} catch (Exception e) {
				// TODO: handle exception
				cData.addToLog("Exception in int convertion",""+e);
				convertedValue = 0;
			}
		}else {
			convertedValue = 0;
		}
		cData.addToLog("", "Session Variable Name : "+sessionVaraibleName+" Coverted Value : "+convertedValue);
		return convertedValue;

	}
	
	public int convertToInt(String strElementName,String value) {	
		int i=0;
		try {
			i=(int)Double.parseDouble(value);
		}catch (Exception e) {
          errorLog(strElementName, e);
		}
		return i;
	}

	private boolean isPressAtBeginning(){
		return false;
	}
	public void setAudioItemListForAudio(String listName, List<Map<String, String>>hostDynamicValues, String dynamicConf) throws AudiumException {

		Map<String,String> mapDynamicConfWavfiles = (Map<String,String>) cData.getApplicationAPI().getApplicationData(dynamicConf); 
		cData.addToLog( cData.getCurrentElement(), "map Dynamic audio Conf Wavfiles :"+mapDynamicConfWavfiles);
		if (IsNotNUllorEmpty(mapDynamicConfWavfiles) && IsNotNUllorEmpty(hostDynamicValues)) {
			String wavfiles = getValueFromDynamicConf(mapDynamicConfWavfiles);
			cData.addToLog( cData.getCurrentElement(), "Dynamic wave file :"+wavfiles);
			List<Audioitem> listAudioItems =new ArrayList<Audioitem>(); 

			for (Map<String,String> dynaValue:hostDynamicValues) {
				setAudioGroup(listAudioItems, wavfiles, dynaValue);
			}
			cData.setSessionData(listName, listAudioItems);
		}


	}
	public  boolean IsNotNUllorEmpty(Map input) {
		boolean result = false;
		if (null != input && !input.isEmpty()) {
			result = true;
		}
		return result;
	}
	public boolean IsNotNUllorEmpty(Collection obj) {
		boolean result = false;
		if (null != obj && !obj.isEmpty()) {
			result = true;
		}
		return result;

	}
	private String getValueFromDynamicConf(Map<String,String> mapDynamicConf) {
		String activeLang = (String) cData.getSessionData("S_ACTIVE_LANG");
		if(mapDynamicConf.containsKey(activeLang)) {
			return mapDynamicConf.get(activeLang);
		}else {
			return mapDynamicConf.get("default");
		}
	}
	public boolean setAudioItemListForMenu (String listName, List<Map<String, String>>hostDynamicValues, String dynamicConf) throws AudiumException {
		boolean status = false;
		cData.addToLog( cData.getCurrentElement(), "Dynamic Conf Key :"+dynamicConf);
		Map<String,String> mapDynamicConfWavfiles = (Map<String,String>) cData.getApplicationAPI().getApplicationData(dynamicConf); 
		cData.addToLog( cData.getCurrentElement(), "map Dynamic Conf Wavfiles :"+mapDynamicConfWavfiles);
		cData.addToLog( cData.getCurrentElement(), "map host Value :"+hostDynamicValues);
		if (IsNotNUllorEmpty(mapDynamicConfWavfiles) && IsNotNUllorEmpty(hostDynamicValues)) {
			int prescnt = 1;
			List<String> listDTMF = new ArrayList<>();
			String wavfiles = getValueFromDynamicConf(mapDynamicConfWavfiles);
			cData.addToLog( cData.getCurrentElement(), "Dynamic wave file :"+wavfiles);
			List<Audioitem> listAudioItems =new ArrayList<Audioitem>(); 

			for (Map<String,String> dynaValue:hostDynamicValues) {
				if(dynaValue!=null && dynaValue.containsKey(VoiceElementsInterface.MORE_OPTION)) {
					addprvMainMoreOption(dynaValue.get(VoiceElementsInterface.MORE_OPTION), listAudioItems, listDTMF);
				}else if(dynaValue!=null && dynaValue.containsKey(VoiceElementsInterface.PREVIOUS_MENU)) {
					addprvMainMoreOption(dynaValue.get(VoiceElementsInterface.PREVIOUS_MENU), listAudioItems, listDTMF);
				}else if(dynaValue!=null && dynaValue.containsKey(VoiceElementsInterface.MAIN_MENU)) {
					addprvMainMoreOption(dynaValue.get(VoiceElementsInterface.MAIN_MENU), listAudioItems, listDTMF);
				}else {
					String tempWavfiles ="";
					if("MG".equalsIgnoreCase(String.valueOf(cData.getSessionData( "S_OPCO_CODE")))) {
						tempWavfiles="press"+prescnt+".wav,"+wavfiles;
					}else {
						tempWavfiles = wavfiles+"@press"+prescnt+".wav";
					}
					setAudioGroup(listAudioItems, tempWavfiles, dynaValue);
					listDTMF.add(""+prescnt);
					prescnt++;
				}
			}

			for (Audioitem audioitem : listAudioItems) {
				cData.addToLog(cData.getCurrentElement(), "Audio ITEM Value :"+audioitem.getValue());
			}

			setMenuDTMF(listDTMF);
			cData.setSessionData(listName, listAudioItems);
			status =true;
		}
		return status;

	}

	public void addprvMainMoreOption(String keyValue,List<Audioitem> listAudioItems,List<String> listDTMF) {

		Map<String,String> mapConfWavfiles = (Map<String,String>) cData.getApplicationAPI().getApplicationData(keyValue); 
		cData.addToLog( cData.getCurrentElement(), "mapadditionConfWavfiles :"+mapConfWavfiles);
		if(mapConfWavfiles!=null) {
			String moreWavfiles = getValueFromDynamicConf(mapConfWavfiles);
			cData.addToLog( cData.getCurrentElement(), "More Wave file :"+moreWavfiles);
			String[] arrMoreWavfiles=moreWavfiles.split("\\|");
			if(arrMoreWavfiles.length>1) {
				setAudioGroup(listAudioItems, arrMoreWavfiles[0], null);
				listDTMF.add(arrMoreWavfiles[1]);
			}else 
				cData.addToLog( cData.getCurrentElement(), "DTMF Key Press Not Available");
		}else 
			cData.addToLog( cData.getCurrentElement(), keyValue+" Value not in dynamic Config");



	}

	public boolean setAudioItemForCVPMenu(String listName, List<String> wavfileslist) throws AudiumException {
		//setting audioitems 
		boolean status =false;
		List<Audioitem> listAudioItems =  new ArrayList<Audioitem>();
		if(IsNotNUllorEmpty(wavfileslist))
		{
			cData.addToLog("Util Class", "Wav file list: "+wavfileslist);
			for(String audio : wavfileslist) {
				if(audio.contains("@")) {
					String audios[]=audio.split("@");
					String multiAudio[]= audios[0].split(",");

					if(isPressAtBeginning()) {
						Audioitem press = new Audioitem();
						press.setValue(audios[1]);
						listAudioItems.add(press);
						for(String audion:multiAudio) {
							Audioitem audioItem = new Audioitem();
							audioItem.setValue(audion);
							listAudioItems.add(audioItem);
						}	
					}else {
						for(String audion:multiAudio) {
							Audioitem audioItem = new Audioitem();
							audioItem.setValue(audion);
							listAudioItems.add(audioItem);
						}
						Audioitem press = new Audioitem();
						press.setValue(audios[1]);
						listAudioItems.add(press);	
					}
				}else {
					Audioitem audioItem = new Audioitem();
					audioItem.setValue(audio);	
					listAudioItems.add(audioItem);
				}

			}
			cData.setSessionData(listName, listAudioItems);
			status=true;
		}
		return false;
	}
	public void setMenuDTMF(List<String> listDtmf) throws AudiumException {
		cData.setSessionData("DTMF_KEYPRESS", listDtmf);
	}
	public boolean IsValidRestAPIResponse() {
		String errorCode = String.valueOf(cData.getSessionData("S_ERROR_CODE"));
		String statuSCode = String.valueOf(cData.getSessionData("S_STATUS_CODE"));
		cData.addToLog("Util check", "Err Code:"+errorCode+" || Status Code:"+statuSCode);
		if("0".equals(errorCode) && "200".equals(statuSCode)) {
			return true;
		}else{
			return false;
		}
	}
	public boolean IsValidDBSUResponse() {
		String errorCode = (String)cData.getSessionData("S_ERROR_CODE");
		if("0".equals(errorCode)) {
			return true;
		}else {
			return false;
		}
	}
	public boolean IsValidDBNRResponse() {
		String errorCode = (String)cData.getSessionData("S_ERROR_CODE");
		if("-1".equals(errorCode)) {
			return true;
		}else {
			return false;
		}
	}
	public void errorLog(String elementName,Exception e) {

		StringWriter sw = null;
		PrintWriter pw = null;

		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			cData.addToLog("Error occured in :", sw.toString());
		} finally {
			sw.flush();
			pw.close();
			sw = null;
			pw = null;
		}
	}
	public long convertToLong(String sessionVaraibleName) {
		long convertedValue = 0;
		String activeBalance=(String)cData.getSessionData(sessionVaraibleName);
		activeBalance = activeBalance.replaceAll(",", "");
		try {
			cData.setSessionData(sessionVaraibleName, activeBalance);
		} catch (AudiumException e) {
			cData.addToLog("Exception in double convertion",""+e);
		}
		if(IsNotNUllorEmpty(activeBalance)) {
			try{
				convertedValue = Long.parseLong(activeBalance);
			} catch (Exception e) {
				cData.addToLog("Exception in long convertion",""+e);
				convertedValue = 0;
			}
		}else {
			convertedValue = 0;
		}
		cData.addToLog("", "Session Variable Name : "+sessionVaraibleName+" Coverted Value : "+convertedValue);
		return convertedValue;
	}
	public void setDefaultAudioPath() throws AudiumException {
		String vxmlIP = "http://"+cData.getSessionData(VoiceElementsInterface.S_SERVERIP);
		String strAudioPath = (String)cData.getApplicationAPI().getApplicationData(VoiceElementsInterface.AUDIOPATH);
		strAudioPath = vxmlIP+strAudioPath;
		String activeLang = (String) cData.getSessionData(VoiceElementsInterface.ACTIVELANG);
		String strFullAudioPath=VoiceElementsInterface.EMPTY;
		if(null!=activeLang){
			strFullAudioPath = strAudioPath + VoiceElementsInterface.SLASH	+ activeLang + VoiceElementsInterface.SLASH;
			cData.addToLog(cData.getCurrentElement(),"Setting AudioPath with active Language : " + strFullAudioPath);
		}else{
			cData.addToLog(cData.getCurrentElement(),"Setting AudioPath : "+ strAudioPath);
			strFullAudioPath = strAudioPath + VoiceElementsInterface.SLASH;
		}
		cData.setSessionData(VoiceElementsInterface.DEFAULT_AUDIOPATH, strFullAudioPath);
		cData.setDefaultAudioPath(strFullAudioPath);
	}


	protected List<Audioitem> setAudioGroup(List<Audioitem> listAudioItems,String completeWaveFile,Map<String,String> dynaValue) {
		String position = (String)cData.getApplicationAPI().getApplicationData(VoiceElementsInterface.PRESS_WAVE_POSITION);
		cData.addToLog(cData.getCurrentElement(),"Audio Item Content : "+completeWaveFile);
		if(completeWaveFile!=null && completeWaveFile.contains(",") || completeWaveFile.contains("$") || completeWaveFile.contains("@")) {
			String[] arrWaveFile = completeWaveFile.split(",");
			for (String waveFile : arrWaveFile) {
				if(waveFile.contains("@")) {
					String[] arrMenuWaveFile = waveFile.split("@"); 
					if("FIRST".equalsIgnoreCase(position)) {
						Audioitem pressWave = new Audioitem();
						pressWave.setValue(arrMenuWaveFile[1]);
						listAudioItems.add(pressWave);
						addSayItSmart(listAudioItems, arrMenuWaveFile[0],dynaValue);
					}else {
						addSayItSmart(listAudioItems, arrMenuWaveFile[0],dynaValue);
						Audioitem pressWave = new Audioitem();
						pressWave.setValue(arrMenuWaveFile[1]);
						listAudioItems.add(pressWave);
					}
				}else {
					addSayItSmart(listAudioItems, waveFile, dynaValue);
				}
			}
		}else {
			addSayItSmart(listAudioItems, completeWaveFile, dynaValue);
		}

		return listAudioItems;
	}


	private void addSayItSmart(List<Audioitem> sisAudioItems, String waveFile,Map<String,String> dynaValue){
		Audioitem subAudioitem = new Audioitem();
		if(waveFile.contains("$")) {
			String orjWaveFile =parternMatcher("\\{(.*?)\\}", waveFile);
			cData.addToLog(cData.getCurrentElement(),"Orj :"+orjWaveFile);
			String format = parternMatcher("\\[(.*?)\\]", orjWaveFile);
			String sayAs = parternMatcher("\\-(.*?)\\[", orjWaveFile);
			String value = parternMatcher("\\$(.*?)\\-", orjWaveFile);
			cData.addToLog(cData.getCurrentElement(),"Format:"+format+"\t SayAs:"+sayAs+"\t Value:"+value);
			cData.addToLog(cData.getCurrentElement(),"Map fetched:"+dynaValue);
			String type = "static";

			if((value !=null && dynaValue.containsKey(value)) ||((value ==null&&orjWaveFile!=null) && dynaValue.containsKey(orjWaveFile.replace("$","")))) {
				type="static";
				cData.addToLog(cData.getCurrentElement(),"Just to check");
				//convertToString(dynaValue.get(value));
				if(value==null)
				{
					orjWaveFile=orjWaveFile.replace("$","");
					value=String.valueOf(dynaValue.get(orjWaveFile));
				}
				else
				{
					value=String.valueOf(dynaValue.get(value));
				}
				cData.addToLog(cData.getCurrentElement(),"Value to be placed:"+value);
			}
			if(sayAs==null) {
				sayAs = parternMatcher("\\-(.*?)\\}", waveFile);
			}
			if(sayAs !=null && value !=null) {
				subAudioitem.setFormat(format);
				subAudioitem.setSayAs(sayAs);;
				subAudioitem.setType(type);
				subAudioitem.setValue(value);
				sisAudioItems.add(subAudioitem);
			}else {
				if(null!=value&&value.endsWith(".wav"))
				{
					subAudioitem.setValue(value);
					sisAudioItems.add(subAudioitem);
				}
				else
				{
					cData.addToLog(cData.getCurrentElement()," Audio Item NOT in correct Format"+waveFile);
				}
			}

		}else{
			subAudioitem.setValue(waveFile);
			sisAudioItems.add(subAudioitem);
		}

	}

	private String parternMatcher(String partern,String waveFile) {
		String value =null;
		Pattern formatPattern = Pattern.compile(partern);
		Matcher formatMatcher = formatPattern.matcher(waveFile);
		if (formatMatcher.find()) {
			value = formatMatcher.group(1);
		}
		return value;
	}

	private List<String> multiParternMatcher(String partern,String waveFile) {
		List<String> value =null;
		Pattern formatPattern = Pattern.compile(partern);
		Matcher formatMatcher = formatPattern.matcher(waveFile);
		while (formatMatcher.find()) {
			if(value==null)
				value = new ArrayList<String>();

			value.add(formatMatcher.group(1));
		}
		return value;
	}


	public String formSMSTemplate(String strSMSTemplate,List<Map<String,String>> objListDataMap){
		String smsText = strSMSTemplate;

		String parternRepeat = parternMatcher("\\<(.*?)\\>", strSMSTemplate);
		String repeatData = "NA";
		if(parternRepeat!=null) {
			for (Map<String, String> objDatamap : objListDataMap) {
				String tempDynData = insertSMSData(objDatamap, parternRepeat);
				if("NA".equalsIgnoreCase(repeatData)) 
					repeatData=tempDynData;
				else 
					repeatData =repeatData+",\n"+tempDynData;
				cData.addToLog(cData.getCurrentElement(),"repeatData"+repeatData);
			}
			smsText= smsText.replace("<"+parternRepeat+">",repeatData );
		}else{
			if(objListDataMap!=null) 
				smsText = insertSMSData(objListDataMap.get(0), strSMSTemplate);
			else
				smsText = insertSMSData(null, strSMSTemplate);
		}

		if(smsText.contains("[")){
			smsText=insertSMSData(objListDataMap.get(0), smsText);
		}

		return smsText;
	}


	private String insertSMSData (Map<String, String> objDatamap,String templateText) 
	{
		List<String> keyList = null;
		String value = "";
		keyList = multiParternMatcher("\\[(.*?)\\]", templateText);
		if(keyList!=null) 
		{
			for (String key : keyList)
			{
				if(key!=null && key.startsWith("$"))
				{
					String tempKey = key.replace("$", "");
					value = (String)cData.getSessionData(tempKey);
				}
				else if(key!=null && key.startsWith("@")) 
				{
					value =key.replace("@", "");
				}
				else 
				{
					value=(objDatamap.get(key)!=null) ? String.valueOf(objDatamap.get(key)) : ""; 
				}
				templateText = templateText.replace("["+key+"]", value);
			}
		}

		return templateText;
	}

	public double convertToDouble(String strElmentName,String doubleValue) {
		double result=0;
		try {
			result=Double.parseDouble(doubleValue);
		}catch (Exception e) {
			errorLog(strElmentName, e);
			result=0.0;	
		}
		return result;
	} 

	public double convertToDouble(String sessionVaraibleName) 
	{
		double convertedValue = 0;
		String activeBalance=""+cData.getSessionData(sessionVaraibleName);
		activeBalance = activeBalance.replaceAll(",", "");
		try
		{
			cData.setSessionData(sessionVaraibleName, activeBalance);
		} 
		catch (AudiumException e)
		{
			cData.addToLog("Exception in double convertion",""+e);
		}
		if(IsNotNUllorEmpty(activeBalance)) 
		{
			try
			{
				convertedValue = Double.parseDouble(activeBalance);
			}
			catch (Exception e)
			{
				cData.addToLog("Exception in double convertion",""+e);
				convertedValue = 0;
			}
		}
		else
		{
			convertedValue = 0;
		}
		cData.addToLog("", "Session Variable Name : "+sessionVaraibleName+" Coverted Value : "+convertedValue);
		return convertedValue;
	}

	public String GetLastDigits(String number)
	{
		if(number!=null && number.length()>4)
			return number.substring(number.length()-4);
		else
			return number;
	}

	@SuppressWarnings("unchecked")
	public Map<String,List<Map<String,String>>> splitJsonData(List<Map<String,String>> jsonData,String filterList,String configFileName)
	{
		Map<String,List<Map<String,String>>> filteredData=null;
		Map<String, Object> productCodeDetails= (HashMap<String, Object>) cData.getApplicationAPI().getApplicationData(configFileName);
		List<String> productCode=null;
		try
		{
			cData.addToLog(" Product Code Deatils  : ",""+productCodeDetails);
			if(null!=productCodeDetails) 
			{
				if(filterList.contains("split"))
				{
					filteredData=new HashMap<>();
					String split[]=filterList.split("\\.")[1].split("\\[|\\]");
					String key=split[0];
					String productTypeList[]=split[1].split(",");//Bundle Category eg.SMS,DATA,VOICE
					List<Map<String,String>> dataBundle=null;
					for(String productType:productTypeList)
					{
						productType=productType.trim();
						dataBundle=new ArrayList<>();
						if(productCodeDetails.get(productType) instanceof List) 
						{
							productCode=(List<String>) productCodeDetails.get(productType);
						}
						else
						{
							productCode=new ArrayList<String>();
							productCode.add(String.valueOf(productCodeDetails.get(productType)));
						}
						cData.addToLog(productType," : "+productCode);
						for(String product:productCode)
						{
							for(Map<String,String> responseBundle:jsonData)
							{
								if(responseBundle.containsKey(key))
								{
									if(null!=responseBundle.get(key)&&product.trim().equalsIgnoreCase(String.valueOf(responseBundle.get(key))))
									{
										dataBundle.add(responseBundle);
										break;
									}
								}
							}
						}
						cData.addToLog(productType,"Total Matched Product Count : "+dataBundle.size());
						filteredData.put(productType, dataBundle);
						dataBundle=null;
					}
				}
			}else {
				cData.addToLog("","Null Values Occurs In Product Code Details");
			}
		}
		catch (Exception e)
		{
			errorLog("Exception", e);
		}
		return filteredData;
	}

	public Map<String,List<Map<String, String>>> splitBundlePeriodwise(Set<String> setPeriodData, List<Map<String, String>> listBundleConfig)
	{
		Map<String,List<Map<String, String>>> mapFilteredValue = new HashMap<String,List<Map<String, String>>>();

		try {
			for(String periodValue:setPeriodData)
			{
				List<Map<String, String>> listPeriodwise = new ArrayList<Map<String,String>>();

				for(Map<String, String> mapConfigValue : listBundleConfig)
				{

					String strProductCode=mapConfigValue.get("Product_code");
					if(IsNotNUllorEmpty(strProductCode))
					{
						String wavfile = mapConfigValue.get("Wave ID");
						if(IsNotNUllorEmpty(wavfile))
						{
							mapConfigValue.put("planWav",wavfile);
						}
					}
					if(IsNotNUllorEmpty(mapConfigValue.get("period_type"))&&(mapConfigValue.get("period_type").toLowerCase()).equals(periodValue))
					{
						listPeriodwise.add(mapConfigValue);
					}
				}
				mapFilteredValue.put(periodValue, listPeriodwise);
			}
		}catch (Exception e) {
			errorLog("Exception", e);
		}
		return mapFilteredValue;
	}
	@SuppressWarnings("unchecked")
	public void xltoSessionData(String sheetName) 
	{
		Map<String, Map<String,String>> flagMap = (Map<String, Map<String, String>>) cData.getApplicationAPI().getGlobalData(sheetName);
		Map.Entry<String, Map<String,String>> entry =null;
		try
		{
			if(flagMap!=null)
			{
				for (Iterator<Map.Entry<String, Map<String,String>>> it = flagMap.entrySet().iterator(); it.hasNext();)
				{
					entry=(Map.Entry<String,Map<String,String>>) it.next();
					String key = entry.getKey();
					Map<String,String> valueMap = entry.getValue();
					String value= valueMap.entrySet().iterator().next().getValue();
					cData.setSessionData(key, value);
					cData.addToLog(sheetName,key+" : " +value);
				}
			}
			else
			{
				cData.addToLog(sheetName,"Xl Sheet not available  :" +sheetName);
			}
		} 
		catch (Exception e) 
		{
			cData.addToLog(sheetName,"loading value Error: "+e);
		}

	}
	public boolean validDate(String strDate, String strFormat) throws Exception 
	{
		SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
		sdf.setLenient(false);
		try
		{
			sdf.parse(strDate);
			return true;
		}
		catch (ParseException e)
		{
			errorLog("Exception", e);
			return false;
		}
	}
	@SuppressWarnings("unchecked")
	public void addPreviousMenu(DecisionElementData data) throws AudiumException 
	{
		String menuName =(String) data.getSessionData(VoiceElementsInterface.S_MENU_ACCESSED);
		data.addToLog(data.getCurrentElement(), "Adding to previous menu"+menuName);
		List<String> menuList = (List<String>)data.getSessionData(VoiceElementsInterface.S_MENU_ACCESSED_LIST);
		if(null==menuList)
			menuList = new ArrayList<String>();

		menuList.add(menuName);
		data.setSessionData(VoiceElementsInterface.S_MENU_ACCESSED_LIST,menuList);

	}

	@SuppressWarnings("unchecked")
	public boolean getChargeApplicable(DecisionElementData data, String elementName) throws AudiumException 
	{
		Map<String, List<String>> mapChargeInfo =(Map<String, List<String>>) data.getApplicationAPI().getApplicationData("AGENT_CHARGING_ENABLED");
		data.addToLog(elementName, " mapChargeInfo :"+mapChargeInfo);
		boolean chargeFlag=false;
		int i=0;
		if(null!=mapChargeInfo)
		{
			data.addToLog(elementName, "Setting Bargin Flag");
			for (Iterator<Map.Entry<String,List<String>>> it = mapChargeInfo.entrySet().iterator(); it.hasNext();) 
			{
				Map.Entry<String, List<String>> entry=(Map.Entry<String,List<String>>) it.next();
				String key = entry.getKey();
				List<String> value = entry.getValue();
				String sessionValue = (String) data.getSessionData(key);
				data.addToLog(elementName, "key :"+key+"Value :"+value+ "Session Value :"+sessionValue);
				if(sessionValue!=null && value.contains(sessionValue))
				{
					chargeFlag=true;
					i++;
				}
				if(i==mapChargeInfo.size()) {
					data.addToLog(elementName, "Charge Applicable");
					return chargeFlag;
				}else{
					chargeFlag=false;
				}
			}
		}
		return chargeFlag;
	}

	public void setTransferData(ElementAPI data, String elementName) throws AudiumException 
	{
		String appName =data.getApplicationName();
		Map<String, Map<String,String>> mapTransferInfo =(Map<String, Map<String,String>>) data.getApplicationAPI().getGlobalData(appName+VoiceElementsInterface.TRANSFER_DETAIL);
		data.addToLog("TRANSFER NODE : ","APP NAME : "+appName);
		if(null!=mapTransferInfo)
		{
			Map<String,String> objTransferInfo = mapTransferInfo.get(elementName);
			String activeLang = (String) data.getSessionData("S_ACTIVE_LANG");
			if(objTransferInfo!=null)
			{
				data.setSessionData("S_PRI_SKILL_NAME", objTransferInfo.get(activeLang+"_PRI_SKILL"));
				data.setSessionData("S_SEC_SKILL_NAME", objTransferInfo.get(activeLang+"_SEC_SKILL"));
				data.setSessionData(VoiceElementsInterface.S_TRANSFER_CODE, objTransferInfo.get("Transcode"));
				data.addToLog(elementName,"Primary Skill :"+objTransferInfo.get(activeLang+"_PRI_SKILL")+" and Secondary Skill"+objTransferInfo.get(activeLang+"_SEC_SKILL")
				+ " S_TRANSFER_CODE :"+objTransferInfo.get("Transcode"));
			}
		}
	}
	public boolean hostCall(APIBase data,String hostName) 
	{
		boolean flag=false;
		int logLevel=0;
		SessionAPIHost sessionApi=null;
		RestClient restClient=null;
		Object objBean=null;
		try 
		{
			data.setSessionData("S_ERROR_MSG","NA");
			data.setSessionData("S_ERROR_CODE","NA");
			data.setSessionData("S_STATUS_CODE","NA");
			data.setSessionData("S_RS_STATUS_CODE","NA");
			data.setSessionData("S_RS_ERROR_REASON","NA");
			data.setSessionData("S_HOST_RS_CODE","-1");
			sessionApi=new SessionAPIHost(data, logLevel);
			objBean=data.getApplicationAPI().getGlobalData(hostName);
			restClient=new RestClient(sessionApi,(RestBean) objBean);
			restClient.sendRequest(hostName);
			if(IsValidRestAPIResponse()) 
			{
				flag=true;
			}
		}
		catch(Exception e)
		{
			errorLog("REST CLIENT :", e);
		}
		finally
		{
			objBean=null;
			restClient=null;
			sessionApi=null;	
		}
		return flag;
	}


	public boolean hostCallDB(APIBase data,String strElementName,String dbName) {
		boolean flag=false;
		int logLevel=0;
		Object objBean=null;
		SessionAPIHost sessionApi=null;
		try {
			data.setSessionData("S_ERROR_MSG","NA");
			data.setSessionData("S_ERROR_CODE","NA");
			data.setSessionData("S_STATUS_CODE","NA");
			data.setSessionData("S_RS_STATUS_CODE","NA");
			data.setSessionData("S_RS_ERROR_REASON","NA");
			data.setSessionData("S_HOST_RS_CODE","-1");
			sessionApi=new SessionAPIHost(data, logLevel);
			objBean=data.getApplicationAPI().getGlobalData(dbName);
			if(objBean!=null) 
			{
				DBInterface dbInterface=new DBInterface(dbName, sessionApi,(DB_Bean) objBean );
				dbInterface.executeSP();
				if(IsValidDBSUResponse()) 
				{
					flag=true;
				}

			}
			else 
			{
				data.addToLog(strElementName, "Null Value Occurs For the DB Instance Name : "+dbName);	
			}
		}
		catch(Exception e)
		{
			errorLog(strElementName, e);
		}
		finally
		{
			sessionApi=null;	
			objBean=null;
		}
		return flag;
	}

	public void addToLog(String infoMessage)
	{
		if(DEBUGLEVEL==logLevel)
			cData.addToLog("", " :" + infoMessage);
		if(INFOLEVEL==logLevel)
			cData.addToLog("", " :" + infoMessage);

	}

	public void addToLog(String infoMessage, int logLevel) 
	{
		if(this.logLevel==logLevel)
			cData.addToLog("", " :" + infoMessage);
	}

	public void loadBundlesMenu(List<Map<String, String>> listBundleMain, boolean DefaultMoreOption, String strDynamicConfig, DecisionElementData data) throws AudiumException
	{
		List<Map<String, String>> listBundleDup= null;
		List<Map<String,String>> listBundleDetails= null;
		Map<String,String> mapMoreOption = null;
		try
		{
			if(IsNotNUllorEmpty(listBundleMain))
			{
				listBundleDup= new ArrayList<>();
				listBundleDup.addAll(listBundleMain);
				addToLog( "Bundle Menu Loader- Total List size:"+listBundleMain.size(),DEBUGLEVEL);
				listBundleDetails= new ArrayList<Map<String,String>>();
				int pressCount = 1;
				for(;pressCount<=listBundleMain.size();)
				{
					if(pressCount==5) 
					{
						break;
					}
					listBundleDetails.add(listBundleDup.remove(0));
					pressCount++;
				}
				if(DefaultMoreOption||pressCount<=listBundleMain.size())
				{
					mapMoreOption =  new HashMap<String, String>();
					mapMoreOption.put("MORE_OPTION", "S_BUNDLE_MORE_OPTION_DC");
					listBundleDetails.add(mapMoreOption);
				}
				data.addToLog("Bundle Menu Loader","Bundle details to be loaded : "+listBundleDetails);
				setAudioItemListForMenu("S_BUNDLE_DETAILS", listBundleDetails,strDynamicConfig);
				data.setSessionData("S_BUNDLE_TOTAL", listBundleMain);
				data.setSessionData("S_BUNDLE_REMAINING", listBundleDup);
			}else{
				data.addToLog("Bundle Menu Loader", "No list to be loaded "+listBundleMain);
			}
		}catch (Exception e)
		{
			errorLog("Bundle Menu Loader", e);
		}finally
		{
			listBundleDup= null;
			listBundleDetails= null;
			mapMoreOption = null;
		}
	}

	public void addFreqBundle(Map<String, String> mapBundleData, String strBundleType, DecisionElementData data) 
	{
		Map<String,List<Map<String, String>>> mapFreqBundlesAll = null;
		List<Map<String, String>> listBundleFrequentDup= null;
		List<Map<String, String>> listBundleFrequent=null;
		Map<String, String> mapBundlesFrequentNew = null;
		List<Map<String, String>> listFrequentBundlesNew = null;
		try
		{
			mapFreqBundlesAll = (Map<String, List<Map<String, String>>>) data.getSessionData("S_FREQUENT_BUNDLE");
			if(IsNotNUllorEmpty(mapFreqBundlesAll)) {

				listBundleFrequent= mapFreqBundlesAll.get(strBundleType);
				if(IsNotNUllorEmpty(listBundleFrequent)) {
					data.addToLog("Freqent Bundle update", "Old "+strBundleType+" frequent bundle:"+listBundleFrequent);
					int matchCounter=0;
					listBundleFrequentDup= new ArrayList<Map<String,String>>();
					listBundleFrequentDup.addAll(listBundleFrequent);
					for(Map<String,String> mapBundlesFrequent : listBundleFrequentDup)
					{	
						if(mapBundleData.get("Product_code").equalsIgnoreCase((mapBundlesFrequent).get("productCode")))
						{
							int intCount = (Integer.parseInt(mapBundlesFrequent.get("Count")));
							data.addToLog("Freqent Bundle update","Old count:"+intCount);
							mapBundlesFrequent.put("Count",""+(++intCount));
							data.addToLog("Freqent Bundle update","New count:"+intCount);
							data.addToLog("Freqent Bundle update", "Selected bundle exists in Frequent bundle, so incrementing counter. New "+strBundleType+" frequent bundle:"+listBundleFrequent);
							break;
						}
						else 
						{
							matchCounter++;

						}
						if(matchCounter==listBundleFrequent.size())
						{
							String freqBundleMaxLimit = (String) data.getSessionData("S_FREQ_BUNDLE_MAX_LIMIT");
							Collections.sort(listBundleFrequent, (i,j)-> Integer.parseInt(i.get("Count"))<Integer.parseInt(j.get("Count"))?1:-1);
							if(!IsNotNUllorEmpty(freqBundleMaxLimit))
							{
								freqBundleMaxLimit="3";
								data.addToLog("Freqent Bundle update", "Max frequent bundles limit not available, hence setting "+freqBundleMaxLimit+" by default");
							}
							data.addToLog("Freqent Bundle update", "Maximum Frequent Bundles allowed : " +freqBundleMaxLimit);
							data.addToLog("Freqent Bundle update", "Current Frequent Bundle :" +listBundleFrequent);
							if(Integer.parseInt(freqBundleMaxLimit) == listBundleFrequent.size())
							{
								listBundleFrequent.remove(listBundleFrequent.size()-1);
							}
							mapBundlesFrequentNew = new HashMap<String, String>();
							mapBundlesFrequentNew.put("productCode", mapBundleData.get("Product_code"));
							mapBundlesFrequentNew.put("Count", "1");
							listBundleFrequent.add(mapBundlesFrequentNew);
							data.addToLog("Freqent Bundle update","Selected bundle doesn't exist in Frequent bundle, so replacing last bundle. New "+strBundleType+" frequent bundle:"+listBundleFrequent);							
						}
					}
					mapFreqBundlesAll.put(strBundleType, listBundleFrequent);
					data.setSessionData("S_FREQUENT_BUNDLE", mapFreqBundlesAll);
				}else
				{
					data.addToLog("Freqent Bundle update", strBundleType+" Frequent Bundles not available: "+listBundleFrequent);
					listFrequentBundlesNew = new ArrayList<Map<String,String>>();
					mapBundlesFrequentNew = new HashMap<String, String>();
					mapBundlesFrequentNew.put("productCode", mapBundleData.get("Product_code"));
					mapBundlesFrequentNew.put("Count", "1");
					listFrequentBundlesNew.add(mapBundlesFrequentNew);
					mapFreqBundlesAll.put(strBundleType, listFrequentBundlesNew);
					data.setSessionData("S_FREQUENT_BUNDLE", mapFreqBundlesAll);					}
			}
			else
			{
				data.addToLog("Freqent Bundle update", "Error fetching frequent bundles "+mapFreqBundlesAll);
				mapFreqBundlesAll = new HashMap<String, List<Map<String,String>>>();
				listFrequentBundlesNew = new ArrayList<Map<String,String>>();
				mapBundlesFrequentNew = new HashMap<String, String>();
				mapBundlesFrequentNew.put("productCode", mapBundleData.get("Product_code"));
				mapBundlesFrequentNew.put("Count", "1");
				listFrequentBundlesNew.add(mapBundlesFrequentNew);
				data.addToLog("Freqent Bundle update","Frequent bundle not retrieved, so adding New "+strBundleType+" frequent bundle:"+listBundleFrequent);
				mapFreqBundlesAll.put(strBundleType, listFrequentBundlesNew);
				data.setSessionData("S_FREQUENT_BUNDLE", mapFreqBundlesAll);
			}
		}
		catch (Exception e)
		{
			errorLog("Freqent Bundle update", e);
		}
		finally
		{
			mapFreqBundlesAll = null;
			listBundleFrequentDup= null;
			listBundleFrequent=null;
			mapBundlesFrequentNew = null;
			listFrequentBundlesNew = null;
		}

	}
}