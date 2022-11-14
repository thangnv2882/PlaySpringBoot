package com.example.previewdocument.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public interface IFileService {
  String previewDocument(File file) throws IOException;

}
