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
  public String previewDocument(File docx, int page) throws IOException {

    ZipHandle zipHandle = new ZipHandle();
    File fileDocx = new File(docx.getName());
    InputStream inputStream = zipHandle.getXMLfromDOCX(fileDocx);

    String xml = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        .lines()
        .collect(Collectors.joining("\n"));

    JSONObject jsonObject = XML.toJSONObject(xml);
//    String jsonString = jsonObject.toString(4);
    JSONArray jsonArray = jsonObject.getJSONObject("w:document").getJSONObject("w:body").getJSONArray("w:p");

    int count = 0;

    for (int i = 0; i < jsonArray.length(); i++) {

      JSONObject jsonObjectP = jsonArray.getJSONObject(i);
      if (jsonObjectP.has("w:r")) {
        try {
          JSONObject jsonObjectR = jsonArray.getJSONObject(i).getJSONObject("w:r");
          if (jsonObjectR.has("w:lastRenderedPageBreak")) {
            count++;
            if (count >= page) {
              for(int k = i; k < jsonArray.length(); k++) {
                // code here
                jsonArray.remove(k);
              }
            }
          }
        } catch (Exception ex) {
          System.out.println(ex.getMessage());
        }
        try {
          JSONArray jsonArrayR = jsonArray.getJSONObject(i).getJSONArray("w:r");
          for (int j = 0; j < jsonArrayR.length(); j++) {
            if (jsonArrayR.getJSONObject(j).has("w:lastRenderedPageBreak")) {
              count++;
              if (count >= page) {
                for (int k = j; k < jsonArrayR.length(); k++) {
                  jsonArray.getJSONObject(i).getJSONArray("w:r").remove(k);
                }
              }
            }
          }
        } catch (Exception ex) {
          System.out.println(ex.getMessage());
        }
      }
    }

    jsonObject.getJSONObject("w:document").getJSONObject("w:body").put("w:p", jsonArray);

    String xmlResult = XML.toString(jsonObject);
//    System.out.println("xml: " + xmlResult);

//    System.out.println(xmlResult);

//    WordprocessingMLPackage wordMLPackage = new WordprocessingMLPackage();
//    MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
//
////    String xmlContent= documentPart.getXML();
//
//    Object obj = XmlUtils.unmarshalString(xmlResult);
//    documentPart.setJaxbElement((Document) obj);
//    wordMLPackage.addTargetPart(documentPart);
//    wordMLPackage.save(new File("Preview.docx"));

//    Path pathXMLFile = Paths.get("Previeww.xml");
//    Files.write(pathXMLFile, xmlResult.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.APPEND,
//        StandardOpenOption.CREATE);

    //Create a Document instance
//    Document document = new Document("Previeww.xml");
//
//    //Load an XML sample document
//    document.loadFromFile(xmlResult);
//
//    //Save the document to Word
//    document.saveToFile("Previeww.docx", FileFormat.Docx);
//    final String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"+
//        "<Emp id=\"1\"><name>Pankaj</name><age>25</age>\n"+
//        "<role>Developer</role><gen>Male</gen></Emp>";
//    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//    DocumentBuilder builder;
//    try
//    {
//      builder = factory.newDocumentBuilder();
//      Document doc = builder.parse( new InputSource( new StringReader( xmlStr ) ) );
//      System.out.println("doc: " + doc.getDocumentElement());
//    } catch (Exception e) {
//      e.printStackTrace();
//    }

    return jsonObject.toString();
  }
}
