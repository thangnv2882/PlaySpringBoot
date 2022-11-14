package com.example.handlefileexcel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.xbill.DNS.TextParseException;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class HandleFileExcelApplication {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TextParseException {
        SpringApplication.run(HandleFileExcelApplication.class, args);

    }


}
