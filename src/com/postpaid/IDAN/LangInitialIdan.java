package com.postpaid.IDAN;

import com.audium.server.session.ActionElementData;
import com.audium.server.voiceElement.ActionElementBase;
import com.util.Utilities;
import java.util.ArrayList;
import java.util.List;

public class LangInitialIdan extends ActionElementBase 
{
  public void doAction(String elementName, ActionElementData data) 
  {
    Utilities util = null;
    List<String> languageAudios = null;
    List<String> listDTMF = null;
    List<String> languages = null;
    try 
    {
      languageAudios = new ArrayList<>();
      listDTMF = new ArrayList<>();
      util = new Utilities(data);
      languages = (List<String>)data.getApplicationAPI().getApplicationData("IDAN_LANGUAGES");
      if (util.IsNotNUllorEmpty(languages))
      {
        for (int i = 1; i <= languages.size(); i++) 
        {
          languageAudios.add(String.valueOf(languages.get(i - 1)) + ".wav@press" + i + ".wav");
          listDTMF.add(""+i);
        } 
        util.setAudioItemForCVPMenu("S_LIST_INITIAL_LANGUAGES", languageAudios);
        util.setMenuDTMF(listDTMF);
      }
      else
      {
        data.addToLog(elementName, "Error fetching Language settings Map. Value: " + languages);
      } 
    } 
    catch (Exception e)
    {
      util.errorLog(elementName, e);
    } 
    finally
    {
      languages = null;
      languageAudios = null;
      listDTMF = null;
      util=null;
    } 
  }
}
