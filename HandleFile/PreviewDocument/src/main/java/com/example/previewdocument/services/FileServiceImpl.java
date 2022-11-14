package com.example.previewdocument.services;

import com.example.previewdocument.zip.ZipHandle;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Transactional
@Service
public class FileServiceImpl implements IFileService {

  @Override
  public String previewDocument(File docx) throws IOException {

    ZipHandle zipHandle = new ZipHandle();
    File fileDocx = new File(docx.getName());
    InputStream inputStream = zipHandle.getXMLfromDOCX(fileDocx);

    String xml = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    JSONObject jsonObject = XML.toJSONObject(xml);
//    String jsonString = jsonObject.toString(4);

    final JSONArray jsonArray = jsonObject.getJSONObject("w:document").getJSONObject("w:body").getJSONArray("w:p");

    int page = 0;

    while (page <= 1) {
      for(int i = 0; i < jsonArray.length(); i++) {
        JSONObject jo2 = jsonArray.getJSONObject(i);
        if(jo2.has("w:r")) {
          try {
            if(jo2.getJSONObject("w:r") != null) {
              JSONObject jo3 = jsonArray.getJSONObject(i).getJSONObject("w:r");
              if(jo3.has("w:lastRenderedPageBreak")) {
                System.out.println("cong o obj");
                page++;
//                if(page > 1) {
                  System.out.println("xoa 1 thang");
                    jsonArray.remove(i);
//                }
              }
            }
          } catch (Exception ex) {
//          System.out.println(ex.getMessage());
          }
          try {
            if(jo2.getJSONArray("w:r") != null) {
              JSONArray jo3 = jsonArray.getJSONObject(i).getJSONArray("w:r");
              for(int j = 0; j < jo3.length(); j++) {
//              System.out.println("json object cua arr: " + jo3.getJSONObject(j));
                if(jo3.getJSONObject(j).has("w:lastRenderedPageBreak")) {
                  System.out.println("cong o arr");
                  page++;
                }
              }
            }
          } catch (Exception ex) {
//          System.out.println(ex.getMessage());
          }
        }
      }
    }


    System.out.println(page);

    jsonObject.getJSONObject("w:document").getJSONObject("w:body").put("w:p", jsonArray);

    String xmlResult = XML.toString(jsonObject);
    System.out.println(xmlResult);


    return jsonObject.toString();
  }
}
