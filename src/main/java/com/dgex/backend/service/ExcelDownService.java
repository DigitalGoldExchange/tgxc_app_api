package com.dgex.backend.service;

import com.dgex.backend.entity.Exchange;
import com.dgex.backend.entity.UserExchangeImage;
import com.dgex.backend.repository.ExchangeRepository;
import com.dgex.backend.repository.UserExchangeImageRepository;
import com.itextpdf.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ExcelDownService {

    @Value("${upload.url}")
    private String uploadDir;

    private final UserExchangeImageRepository userExchangeImageRepository;
    private final ExchangeRepository exchangeRepository;
    private final Configuration configuration;


    public ResponseEntity<Resource> excelDown(Integer exchangeId) throws Exception {

        Exchange exchange = exchangeRepository.findById(exchangeId).get();
        UserExchangeImage userExchangeImage = userExchangeImageRepository.findByDeleteDatetimeIsNullAndExchange(exchange);

        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String tradeTime = dateFormat.format(exchange.getUpdateDatetime());
        String nowTime = dateFormat.format(nowDate);

        configuration.setClassForTemplateLoading(this.getClass(), "/templates");
        final Template t = configuration.getTemplate("exchange_order.ftl");
        final Map<String, Object> mavObject = new HashMap<>();
        mavObject.put("exchange",exchange);
        mavObject.put("uploadDir",uploadDir);
        mavObject.put("userExchangeImage",userExchangeImage);
        mavObject.put("tradeTime",tradeTime);
        mavObject.put("nowTime",nowTime);

        String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, mavObject);
        InputStream targetStream = new ByteArrayInputStream(content.getBytes());

        String cleanHTMLFile = "exchange_order_temp.ftl";
        OutputStream os = new FileOutputStream(cleanHTMLFile);

        Tidy htmlCleaner = new Tidy();
        htmlCleaner.setInputEncoding("UTF-8");
        htmlCleaner.setOutputEncoding("UTF-8");
        htmlCleaner.setXHTML(true);
        htmlCleaner.parse(targetStream, os);

        String url = new File(cleanHTMLFile).toURI().toURL().toString();

//        String fileName = UUID.randomUUID().toString();
        String filePath = "exchange_form.pdf";
        OutputStream outputStream = new FileOutputStream(filePath);
        String fontPath = "fonts";

        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = renderer.getFontResolver();

        fontResolver.addFont(fontPath + "/NanumGothic-Bold.ttf", BaseFont.IDENTITY_H, true);


        renderer.setDocument(url);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.flush();
        outputStream.close();
        os.flush();
        os.close();

        File file = new File("exchange_form.pdf");
        String fileName = "exchange_form.pdf";
        InputStreamResource resource = new InputStreamResource(new FileInputStream("exchange_form.pdf"));

        File tempFile = new File("exchange_order_temp.ftl");
        tempFile.delete();

//        File tempFile1 = new File("exchange_form.pdf");
//        tempFile1.delete();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+fileName+"\"")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);

    }

    public void excelDown1(Integer exchangeId) throws Exception {

        Exchange exchange = exchangeRepository.findById(exchangeId).get();
        UserExchangeImage userExchangeImage = userExchangeImageRepository.findByDeleteDatetimeIsNullAndExchange(exchange);

        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String tradeTime = dateFormat.format(exchange.getUpdateDatetime());
        String nowTime = dateFormat.format(nowDate);

        configuration.setClassForTemplateLoading(this.getClass(), "/templates/");
        final Template t = configuration.getTemplate("pdf/exchange_order.ftl");
        final Map<String, Object> mavObject = new HashMap<>();
        mavObject.put("exchange",exchange);
        mavObject.put("uploadDir",uploadDir);
        mavObject.put("userExchangeImage",userExchangeImage);
        mavObject.put("tradeTime",tradeTime);
        mavObject.put("nowTime",nowTime);

        String content = FreeMarkerTemplateUtils.processTemplateIntoString(t, mavObject);
        InputStream targetStream = new ByteArrayInputStream(content.getBytes());

        String cleanHTMLFile = "exchange_order_temp.ftl";
        OutputStream os = new FileOutputStream(cleanHTMLFile);

        Tidy htmlCleaner = new Tidy();
        htmlCleaner.setInputEncoding("UTF-8");
        htmlCleaner.setOutputEncoding("UTF-8");
        htmlCleaner.setXHTML(true);
        htmlCleaner.parse(targetStream, os);

        String url = new File(cleanHTMLFile).toURI().toURL().toString();

//        String fileName = UUID.randomUUID().toString();
        String filePath = "exchange_form.pdf";
        OutputStream outputStream = new FileOutputStream(filePath);
        String fontPath = "fonts";

        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = renderer.getFontResolver();

        fontResolver.addFont(fontPath + "/NanumGothic-Bold.ttf", BaseFont.IDENTITY_H, true);


        renderer.setDocument(url);
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.flush();
        outputStream.close();
        os.flush();
        os.close();

        File tempFile = new File("exchange_order_temp.ftl");
        tempFile.delete();


    }
}
