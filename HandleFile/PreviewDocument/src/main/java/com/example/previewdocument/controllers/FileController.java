package com.example.previewdocument.controllers;

import com.example.previewdocument.services.IFileService;
import com.example.previewdocument.utils.ConvertObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
public class FileController {

  private final IFileService fileService;

  public FileController(IFileService fileService) {
    this.fileService = fileService;
  }

  @PostMapping("/preview-document")
  public ResponseEntity<?> PreviewDocument(
      @RequestParam(name = "file", required = true) MultipartFile file
  ) throws IOException {
    return ResponseEntity.status(200).body(fileService.previewDocument(ConvertObject.convertMultipartToFile(file)));
  }

}
