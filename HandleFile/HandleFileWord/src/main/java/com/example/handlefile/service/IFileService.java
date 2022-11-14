package com.example.handlefile.service;


import org.docx4j.openpackaging.exceptions.Docx4JException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

public interface IFileService {

    String UploadFileDocx(File fileDocx) throws IOException, Docx4JException, JAXBException;

    String WriteFileDocx(Long idDocument) throws Docx4JException;

}
