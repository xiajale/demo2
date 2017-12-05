package com.huawei.cloud.util;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Created by zhouyibin on 2017/12/6.
 */
public class FileUtil {

    private static final String ENCODING = "utf-8";

    public static String readFile(String path){
        String content = null;
        try {
            File file = new File(path);
            content = FileUtils.readFileToString(file, ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static Document readDocumentFromFile(String path){
        Document document = null;
        try {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(file);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return document;
    }

    public static void writeFile(String content, String path){
        try {
            File file = new File(path);
            FileUtils.writeStringToFile(file, content, ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeDocumentToFile(Document document, String path){

        try {
            File file = new File(path);
            Result result = new StreamResult(file);
            Source source = new DOMSource(document);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }


    }

}
