package com.example.handlefileexcel.service.imp;

import com.example.handlefileexcel.dao.User;
import com.example.handlefileexcel.repositories.UserRepository;
import com.example.handlefileexcel.service.IUserService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;
import org.xbill.DNS.lookup.LookupSession;

import javax.transaction.Transactional;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Transactional
@Service
public class UserServiceImp implements IUserService {

    int emailIndex, usernameIndex, phoneIndex, fullNameIndex, genderIndex;

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    public List<User> readFileExcel(File file) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(file);

        // Tạo phiên bản Workbook chứa tham chiếu đến tệp .xlsx
        Workbook workbook = new XSSFWorkbook(fileInputStream);

        // Lấy trang tính đầu tiên trong ecxel
        Sheet sheet = workbook.getSheetAt(0);

        // Lặp lại từng hàng một
        Iterator<Row> rowIterator = sheet.iterator();

        List<User> users = new ArrayList<>();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();

            if (row.getRowNum() == 0) {
                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
                    switch (row.getCell(i).getStringCellValue()) {
                        case "email":
                            emailIndex = i;
                            break;
                        case "username":
                            usernameIndex = i;
                            break;
                        case "phone":
                            phoneIndex = i;
                            break;
                        case "fullName":
                            fullNameIndex = i;
                            break;
                        case "gender":
                            genderIndex = i;
                            break;
                    }
                }
            } else {
                User user = new User();
                user.setEmail(row.getCell(emailIndex).getStringCellValue());
                user.setUsername(row.getCell(usernameIndex).getStringCellValue());
                user.setPhone(row.getCell(phoneIndex).getStringCellValue());
                user.setFullName(row.getCell(fullNameIndex).getStringCellValue());
                user.setGender(row.getCell(genderIndex).getStringCellValue());
                userRepository.save(user);
                users.add(user);
            }
        }
        return users;
    }

    @Override
    public List<User> readFileDocx(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);

        XWPFDocument document = new XWPFDocument(fileInputStream);

        List<XWPFParagraph> paragraphs = document.getParagraphs();

//        for(int i = 0; i < 2; i++) {
//            System.out.println(paragraphs.get(i).getText());
//
//        }

        for (XWPFParagraph para : paragraphs) {

            System.out.println(para.getText());


//            System.out.println(para.getText());
        }
        fileInputStream.close();

        return null;
    }

    @Override
    public List<User> writeFileDocx(File file) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(file);

        XWPFDocument documentIn = new XWPFDocument(fileInputStream);

        List<XWPFParagraph> paragraphs = documentIn.getParagraphs();

        // Create Blank document
        XWPFDocument documentOut = new XWPFDocument();

        // Create new Paragraph
        XWPFParagraph paragraph1 = documentOut.createParagraph();
        XWPFRun run = paragraph1.createRun();

        for(XWPFParagraph para: paragraphs) {
            run.setText(para.getText());
            run.setColor("EB4747");
            run.addBreak();
        }

        // Write the Document in file system
        FileOutputStream out = new FileOutputStream(new File("/home/thang2882/Downloads/file-output.docx"));
        documentOut.write(out);
        out.close();
        documentOut.close();
        System.out.println("successully");

        return null;
    }

    @Override
    public User ConvertXMLtoJSON(File file) throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
//        try {
//            JSONObject json = XML.toJSONObject(fileInputStream.toString());
//            String jsonString = json.toString(4);
//            System.out.println(jsonString);
//
//        } catch (JSONException e) {
//            // TODO: handle exception
//            System.out.println(e.toString());
//        }
        System.out.println(fileInputStream.toString());
        return null;
    }

//    public static boolean checkEmailExists(String email) throws TextParseException, ExecutionException, InterruptedException {
//        if (!isValidEmail(email)) {
//            return false;
//        }
//
//        String domain = email.split("[@]")[1].toLowerCase();
//
//        AtomicBoolean isEmailExist = new AtomicBoolean(false);
//
//        LookupSession s = LookupSession.defaultBuilder().build();
//        Name mxLookup = Name.fromString(domain);
//        s.lookupAsync(mxLookup, Type.MX)
//                .whenComplete(
//                        (answers, ex) -> {
//                            if (ex == null) {
//                                int stage = 0;
//                                int priority = 10000;
//                                int lowestPriorityIndex = 0;
//                                for (int i = 0; i < answers.getRecords().size(); i++) {
//                                    MXRecord mx = ((MXRecord) answers.getRecords().get(i));
//                                    if (mx.getPriority() < priority) {
//                                        priority = mx.getPriority();
//                                        lowestPriorityIndex = i;
//                                    }
//                                }
//                                String smtp = ((MXRecord) answers.getRecords().get(lowestPriorityIndex)).getTarget().toString();
//                                try {
//                                    Socket socket = new Socket(smtp, 25);
//                                    if (socket.isConnected()) {
//                                        String cmd1 = "EHLO " + "mail.example.org" + "\r\n";
//                                        String cmd2 = "MAIL FROM:<" + "name@example.org" + ">\r\n";
//                                        String cmd3 = "RCPT TO:<" + email + ">\r\n";
//                                        String cmd4 = "QUIT\r\n";
//
//                                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
//                                        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
//                                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
//
//                                        String bufferData;
//                                        while ((bufferData = bufferedReader.readLine()) != null) {
//                                            switch (stage) {
//                                                case 0:
//                                                    if (bufferData.contains("220")) {
//                                                        try {
//                                                            dataOutputStream.write(cmd1.getBytes());
//                                                            dataOutputStream.flush();
//                                                            stage++;
//                                                        } catch (IOException ignored) {
//                                                            isEmailExist.set(false);
//                                                        }
//                                                    }
//                                                    break;
//                                                case 1:
//                                                    if (bufferData.contains("250")) {
//                                                        try {
//                                                            dataOutputStream.write(cmd2.getBytes());
//                                                            dataOutputStream.flush();
//                                                            stage++;
//                                                        } catch (IOException ignored) {
//                                                            isEmailExist.set(false);
//                                                        }
//                                                    }
//                                                    break;
//                                                case 2:
//                                                    if (bufferData.contains("250")) {
//                                                        try {
//                                                            dataOutputStream.write(cmd3.getBytes());
//                                                            dataOutputStream.flush();
//                                                            stage++;
//                                                        } catch (IOException ignored) {
//                                                            isEmailExist.set(false);
//                                                        }
//                                                    }
//                                                    break;
//                                                case 3:
//                                                    if (bufferData.contains("250") && bufferData.contains("OK")) {
//                                                        isEmailExist.set(true);
//                                                    }
//                                                    if (bufferData.contains("550")) {
//                                                        isEmailExist.set(false);
//                                                    }
//                                                    dataOutputStream.write(cmd4.getBytes());
//                                                    dataOutputStream.flush();
//                                                    break;
//                                            }
//                                        }
//
//                                        bufferedReader.close();
//                                        dataInputStream.close();
//                                        dataOutputStream.close();
//
//                                    }
//
//                                } catch (IOException e) {
//                                    isEmailExist.set(false);
//                                    e.printStackTrace();
//                                }
//                            } else {
//                                isEmailExist.set(false);
//                                ex.printStackTrace();
//                            }
//                        }
//                ).toCompletableFuture().get();
//
//        return isEmailExist.get();
//    }
//
//    public static boolean isValidEmail(String email) {
//        String regex = "^[a-z0-9]+(?!.*(?:\\+{2,}|-{2,}|\\.{2,}))(?:[.+\\-]?[a-z0-9])*@gmail\\.com$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(email);
//        return matcher.find();
//    }

}
