package com.example.handlefileexcel.controllers;

import com.example.handlefileexcel.dao.User;
import com.example.handlefileexcel.service.IUserService;
import com.example.handlefileexcel.utils.ConvertObject;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final IUserService userService;
    private final JavaMailSender javaMailSender;


    public FileController(IUserService userService, JavaMailSender javaMailSender) {
        this.userService = userService;
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/import-file-excel")
    public ResponseEntity<?> importExcelFile(
            @RequestParam(name = "file", required = true) MultipartFile file
    ) throws IOException {
        return ResponseEntity.status(200).body(userService.readFileExcel(ConvertObject.convertMultipartToFile(file)));
    }

    @GetMapping("/import-file-word")
    public ResponseEntity<?> ImportWordFile(
            @RequestParam(name = "file", required = true) MultipartFile file
    ) throws IOException {
        return ResponseEntity.status(200).body(userService.readFileDocx(ConvertObject.convertMultipartToFile(file)));
    }

    @GetMapping("/write-file-word")
    public ResponseEntity<?> WriteWordFile(
            @RequestParam(name = "file", required = true) MultipartFile file
    ) throws IOException {
        return ResponseEntity.status(200).body(userService.writeFileDocx(ConvertObject.convertMultipartToFile(file)));
    }

    @GetMapping("/convert-xml-to-json")
    public ResponseEntity<?> ConvertXMLtoJSON(
            @RequestParam(name = "file", required = true) MultipartFile file
    ) throws IOException {
        return ResponseEntity.status(200).body(userService.ConvertXMLtoJSON(ConvertObject.convertMultipartToFile(file)));
    }

    @PostMapping("/send")
    public String DemoSendMail() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        List<User> users = userService.findAllUser();
            users.forEach(user -> {
                try {
                simpleMailMessage.setTo(user.getEmail());
                simpleMailMessage.setSubject("Send to " + user.getFullName());
                simpleMailMessage.setText("Thông tin tài khoản\n" +
                        "\nUsername: " + user.getUsername() +
                        "\nFullname: " + user.getFullName() +
                        "\nPhoneNumber: " + user.getPhone() +
                        "\nEmail: " + user.getEmail());

                //Send mail
                javaMailSender.send(simpleMailMessage);
                user.setDetail("SUCCESS");
                } catch (Exception e) {
                    user.setDetail("FAILED");
                    System.err.println("Error Sending: ");
                    e.printStackTrace();
                }
            });
        return "Send mail successfully";
    }

}
