package com.example.handlefileexcel.service;

import com.example.handlefileexcel.dao.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IUserService {
    List<User> findAllUser();
    List<User> readFileExcel(File file) throws IOException;

    List<User> readFileDocx(File file) throws IOException;

    List<User> writeFileDocx(File file) throws IOException;

    User ConvertXMLtoJSON(File file) throws FileNotFoundException;

}
