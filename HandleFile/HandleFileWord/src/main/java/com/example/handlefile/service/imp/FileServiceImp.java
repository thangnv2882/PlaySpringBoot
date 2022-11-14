package com.example.handlefile.service.imp;

import com.example.handlefile.entity.Document;
import com.example.handlefile.entity.Run;
import com.example.handlefile.entity.Segment;
import com.example.handlefile.exception.NotFoundException;
import com.example.handlefile.repositories.DocumentRepository;
import com.example.handlefile.repositories.RunRepository;
import com.example.handlefile.repositories.SegmentRepository;
import com.example.handlefile.service.IFileService;
import org.docx4j.XmlUtils;
import org.docx4j.docProps.extended.Properties;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import java.io.*;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class FileServiceImp implements IFileService {

    final private DocumentRepository documentRepository;
    final private SegmentRepository segmentRepository;
    final private RunRepository runRepository;

    public FileServiceImp(SegmentRepository segmentRepository, RunRepository runRepository, DocumentRepository documentRepository) {
        this.segmentRepository = segmentRepository;
        this.runRepository = runRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional
    public String UploadFileDocx(File fileDocx) throws IOException, Docx4JException, JAXBException {


        File docx = new File(String.valueOf(fileDocx));

        // Paragraph
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
                .load(docx);
        MainDocumentPart mainDocumentPart1 = wordMLPackage
                .getMainDocumentPart();
        String textNodesSegmentPath = "//w:p";
        List<Object> segments = mainDocumentPart1
                .getJAXBNodesViaXPath(textNodesSegmentPath, true);

        String fileName = fileDocx.getName();
        String ext = fileName.split("\\.")
                    [fileName.split("\\.").length-1];
        String path = fileDocx.getPath();

        DocPropsExtendedPart docPropsExtendedPart =  wordMLPackage.getDocPropsExtendedPart();
        Properties extendedProps = docPropsExtendedPart.getContents();
        final Integer numPages = extendedProps.getPages();

        Document document = new Document();
        document.setFilename(fileName);
        document.setExt(ext);
        document.setPath(path);
        document.setPages(numPages);

        documentRepository.save(document);


        // Run
        WordprocessingMLPackage wordMLPackage2 = WordprocessingMLPackage
                .load(docx);
        MainDocumentPart mainDocumentPart2 = wordMLPackage2
                .getMainDocumentPart();
        String textNodesRunPath = "//w:r";
        List<Object> runs = mainDocumentPart2
                .getJAXBNodesViaXPath(textNodesRunPath, true);


        for (Object objP : segments) {
            P p = (P) objP;
            Segment segment = new Segment();
            segment.setText(String.valueOf(p));
            segment.setDocument(document);
            segmentRepository.save(segment);

        }

        for (Object objR : runs) {
            List<Segment> segmentList = segmentRepository.findSegmentByDocumentId(document.getId());
            Run run = new Run();
            R r = (R) objR;

            P pParent = (P) r.getParent();
            segmentList.forEach(segment -> {
                if (segment.getText().compareTo(String.valueOf(pParent)) == 0) {
                    run.setSegment(segment);
                }
            });

            Text text = (Text) ((JAXBElement) r.getContent().get(0)).getValue();
            run.setText(text.getValue());

            BooleanDefaultTrue isBold = r.getRPr().getB();
            BooleanDefaultTrue isItalic = r.getRPr().getI();
            BooleanDefaultTrue isStrike = r.getRPr().getStrike();
            U isUnderline = r.getRPr().getU();

            if (isBold != null) {
                run.setBold(isBold.isVal());
            }
            if (isItalic != null) {
                run.setItalic(isItalic.isVal());
            }
            if (isStrike != null) {
                run.setStrike(isStrike.isVal());
            }
            if (isUnderline != null)
                run.setUnderlineEnumeration(isUnderline.getVal());
            runRepository.save(run);
        }
        return "Success";
    }

    @Override
    @Transactional
    public String WriteFileDocx(Long idDocument) throws Docx4JException {
        Optional<Document> document = documentRepository.findById(idDocument);
        CheckDocumentExists(document);
        List<Segment> segments = document.get().getSegments();

//        DOCX4J
        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        ObjectFactory factory = Context.getWmlObjectFactory();
        for (Segment segment : segments) {
            P p = factory.createP();
            List<Run> runs = segment.getRuns();
            for (Run run : runs) {
                R r = factory.createR();
                Text t = factory.createText();
                t.setValue(run.getText());
                if(run.getText().startsWith(" ") || run.getText().endsWith(" ")) {
                    t.setSpace("preserve");
                }
                r.getContent().add(t);
                RPr rpr = factory.createRPr();

                BooleanDefaultTrue defaultTrue = new BooleanDefaultTrue();
                if(run.isBold() == true) {
                    rpr.setB(defaultTrue);
                }
                if(run.isItalic() == true) {
                    rpr.setI(defaultTrue);
                }
                if(run.isStrike() == true) {
                    rpr.setStrike(defaultTrue);
                }
                U u = Context.getWmlObjectFactory().createU();
                if(run.getUnderlineEnumeration() != UnderlineEnumeration.NONE) {
                    u.setVal(run.getUnderlineEnumeration());
                    rpr.setU(u);
                }
                r.setRPr(rpr);
                p.getContent().add(r);
            }
            mainDocumentPart.getContent().add(p);
        }

        File exportFile = new File("/home/thang2882/Downloads/file-output.docx");
        wordPackage.save(exportFile);

        return "Write success.";
    }

    public void CheckDocumentExists(Optional<Document> document) {
        if (document.isEmpty()) {
            throw new NotFoundException("Document not exists.");
        }
    }
}

//
//package com.example.handlefile.service.imp;
//
//        import com.example.handlefile.entity.Document;
//        import com.example.handlefile.entity.Run;
//        import com.example.handlefile.entity.Segment;
//        import com.example.handlefile.exception.NotFoundException;
//        import com.example.handlefile.repositories.DocumentRepository;
//        import com.example.handlefile.repositories.RunRepository;
//        import com.example.handlefile.repositories.SegmentRepository;
//        import com.example.handlefile.service.IFileService;
//        import org.docx4j.XmlUtils;
//        import org.docx4j.docProps.extended.Properties;
//        import org.docx4j.jaxb.Context;
//        import org.docx4j.openpackaging.exceptions.Docx4JException;
//        import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
//        import org.docx4j.openpackaging.parts.DocPropsExtendedPart;
//        import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
//        import org.docx4j.wml.*;
//        import org.springframework.stereotype.Service;
//
//        import javax.transaction.Transactional;
//        import javax.xml.bind.JAXBElement;
//        import javax.xml.bind.JAXBException;
//
//        import java.io.*;
//        import java.util.List;
//        import java.util.Optional;
//
//@Transactional
//@Service
//public class FileServiceImp implements IFileService {
//
//    final private DocumentRepository documentRepository;
//    final private SegmentRepository segmentRepository;
//    final private RunRepository runRepository;
//
//    public FileServiceImp(SegmentRepository segmentRepository, RunRepository runRepository, DocumentRepository documentRepository) {
//        this.segmentRepository = segmentRepository;
//        this.runRepository = runRepository;
//        this.documentRepository = documentRepository;
//    }
//
//    @Override
//    @Transactional
//    public String UploadFileDocx(File fileDocx) throws IOException, Docx4JException, JAXBException {
//
//
//        File docx = new File(String.valueOf(fileDocx));
//
//        // Paragraph
//        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage
//                .load(docx);
//        MainDocumentPart mainDocumentPart1 = wordMLPackage
//                .getMainDocumentPart();
//        String textNodesSegmentPath = "//w:p";
//        List<Object> segments = mainDocumentPart1
//                .getJAXBNodesViaXPath(textNodesSegmentPath, true);
//
//        String fileName = fileDocx.getName();
//        String ext = fileName.split("\\.")
//                [fileName.split("\\.").length-1];
//        String path = fileDocx.getPath();
//        DocPropsExtendedPart docPropsExtendedPart =  wordMLPackage.getDocPropsExtendedPart();
//        Properties extendedProps = docPropsExtendedPart.getContents();
//        final Integer numPages = extendedProps.getPages();
//
//        Document document = new Document();
//        document.setFilename(fileName);
//        document.setExt(ext);
//        document.setPath(path);
//        document.setPages(numPages);
//
//        documentRepository.save(document);
//
//
//        // Run
////        WordprocessingMLPackage wordMLPackage2 = WordprocessingMLPackage
////                .load(docx);
////        MainDocumentPart mainDocumentPart2 = wordMLPackage2
////                .getMainDocumentPart();
////        String textNodesRunPath = "//w:r";
////        List<Object> runs = mainDocumentPart2
////                .getJAXBNodesViaXPath(textNodesRunPath, true);
//
//
//        for (Object objP : segments) {
//            P p = ((P) XmlUtils.unwrap(objP));
//            Segment segment = new Segment();
//            segment.setText(String.valueOf(p));
//            segment.setDocument(document);
//            segmentRepository.save(segment);
//
//            for (Object objR : p.getContent()) {
////                List<Segment> segmentList = segmentRepository.findSegmentByDocumentId(document.getId());
//                Run run = new Run();
//                R r = (R) XmlUtils.unwrap(objR);
//
////                P pParent = (P) r.getParent();
////                segmentList.forEach(segment -> {
////                    if (segment.getText().compareTo(String.valueOf(pParent)) == 0) {
////                        run.setSegment(segment);
////                    }
////                });
//                run.setSegment(segment);
//
//                Text text = (Text) ((JAXBElement) r.getContent().get(0)).getValue();
//                run.setText(text.getValue());
//
//                BooleanDefaultTrue isBold = r.getRPr().getB();
//                BooleanDefaultTrue isItalic = r.getRPr().getI();
//                BooleanDefaultTrue isStrike = r.getRPr().getStrike();
//                U isUnderline = r.getRPr().getU();
//
//                if (isBold != null) {
//                    run.setBold(isBold.isVal());
//                }
//                if (isItalic != null) {
//                    run.setItalic(isItalic.isVal());
//                }
//                if (isStrike != null) {
//                    run.setStrike(isStrike.isVal());
//                }
//                if (isUnderline != null)
//                    run.setUnderlineEnumeration(isUnderline.getVal());
//                runRepository.save(run);
//            }
//
//        }
////
////        for (Object objR : runs) {
////            List<Segment> segmentList = segmentRepository.findSegmentByDocumentId(document.getId());
////            Run run = new Run();
////            R r = (R) objR;
////
////            P pParent = (P) r.getParent();
////            segmentList.forEach(segment -> {
////                if (segment.getText().compareTo(String.valueOf(pParent)) == 0) {
////                    run.setSegment(segment);
////                }
////            });
////
////            Text text = (Text) ((JAXBElement) r.getContent().get(0)).getValue();
////            run.setText(text.getValue());
////
////            BooleanDefaultTrue isBold = r.getRPr().getB();
////            BooleanDefaultTrue isItalic = r.getRPr().getI();
////            BooleanDefaultTrue isStrike = r.getRPr().getStrike();
////            U isUnderline = r.getRPr().getU();
////
////            if (isBold != null) {
////                run.setBold(isBold.isVal());
////            }
////            if (isItalic != null) {
////                run.setItalic(isItalic.isVal());
////            }
////            if (isStrike != null) {
////                run.setStrike(isStrike.isVal());
////            }
////            if (isUnderline != null)
////                run.setUnderlineEnumeration(isUnderline.getVal());
////            runRepository.save(run);
////        }
//        return "Success";
//    }
//
//    @Override
//    @Transactional
//    public String WriteFileDocx(Long idDocument) throws Docx4JException {
//        Optional<Document> document = documentRepository.findById(idDocument);
//        CheckDocumentExists(document);
//        List<Segment> segments = document.get().getSegments();
//
////        DOCX4J
//        WordprocessingMLPackage wordPackage = WordprocessingMLPackage.createPackage();
//        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
//        ObjectFactory factory = Context.getWmlObjectFactory();
//        for (Segment segment : segments) {
//            P p = factory.createP();
//            List<Run> runs = segment.getRuns();
//            for (Run run : runs) {
//                R r = factory.createR();
//                Text t = factory.createText();
//                t.setValue(run.getText());
//                if(run.getText().startsWith(" ") || run.getText().endsWith(" ")) {
//                    t.setSpace("preserve");
//                }
//                r.getContent().add(t);
//                RPr rpr = factory.createRPr();
//
//                BooleanDefaultTrue defaultTrue = new BooleanDefaultTrue();
//                if(run.isBold() == true) {
//                    rpr.setB(defaultTrue);
//                }
//                if(run.isItalic() == true) {
//                    rpr.setI(defaultTrue);
//                }
//                if(run.isStrike() == true) {
//                    rpr.setStrike(defaultTrue);
//                }
//                U u = Context.getWmlObjectFactory().createU();
//                if(run.getUnderlineEnumeration() != UnderlineEnumeration.NONE) {
//                    u.setVal(run.getUnderlineEnumeration());
//                    rpr.setU(u);
//                }
//                r.setRPr(rpr);
//                p.getContent().add(r);
//            }
//            mainDocumentPart.getContent().add(p);
//        }
//
//        File exportFile = new File("/home/thang2882/Downloads/file-output.docx");
//        wordPackage.save(exportFile);
//
//        return "Write success.";
//    }
//
//    public void CheckDocumentExists(Optional<Document> document) {
//        if (document.isEmpty()) {
//            throw new NotFoundException("Document not exists.");
//        }
//    }
//}
//
