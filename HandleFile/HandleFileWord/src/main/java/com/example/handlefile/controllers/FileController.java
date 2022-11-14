package com.example.handlefile.controllers;

import com.example.handlefile.service.IFileService;
import com.example.handlefile.utils.ConvertObject;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final IFileService fileService;

    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload-file-docx")
    public ResponseEntity<?> UploadFileDocx(
            @RequestParam(name = "file", required = true) MultipartFile file
    ) throws IOException, JAXBException, Docx4JException {
        return ResponseEntity.status(200).body(fileService.UploadFileDocx(ConvertObject.convertMultipartToFile(file)));
    }

    @GetMapping("/write-file-docx/{idDocument}")
    public ResponseEntity<?> WriteFileDocx(
            @PathVariable("idDocument") Long idDocument
    ) throws Docx4JException {
        return ResponseEntity.status(200).body(fileService.WriteFileDocx(idDocument));
    }

}
