package com.example.springaialibaba.service;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

@Service
public class DocumentParserService {

    /**
     * Parse text content from a DOC or DOCX file
     * @param file the uploaded file
     * @return extracted text content
     * @throws IOException if there's an error reading the file
     */
    public String parseDocument(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String contentType = file.getContentType();
        
        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }
        
        // Determine file type and parse accordingly
        if (fileName.toLowerCase().endsWith(".doc")) {
            return parseDocFile(file.getInputStream());
        } else if (fileName.toLowerCase().endsWith(".docx")) {
            return parseDocxFile(file.getInputStream());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }
    
    /**
     * Parse text content from a DOC file
     * @param inputStream input stream of the DOC file
     * @return extracted text content
     * @throws IOException if there's an error reading the file
     */
    private String parseDocFile(InputStream inputStream) throws IOException {
        try (HWPFDocument document = new HWPFDocument(inputStream)) {
            WordExtractor extractor = new WordExtractor(document);
            return extractor.getText();
        }
    }
    
    /**
     * Parse text content from a DOCX file
     * @param inputStream input stream of the DOCX file
     * @return extracted text content
     * @throws IOException if there's an error reading the file
     */
    private String parseDocxFile(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder text = new StringBuilder();
            
            // Extract text from paragraphs
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                text.append(paragraph.getText()).append("\n");
            }
            
            return text.toString();
        }
    }
}