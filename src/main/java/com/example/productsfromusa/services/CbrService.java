package com.example.productsfromusa.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class CbrService {

    private static final Logger logger = LoggerFactory.getLogger(CbrService.class);

    @Value("${cbr.api}")
    String api;

    //date_req1=21/05/2024&date_req2=21/05/2024&VAL_NM_RQ=R01235
    public double getCourseUSD() {
        try {
            LocalDate currentDate = LocalDate.now();
            currentDate = adjustToWeekday(currentDate);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = currentDate.format(formatter);
            String requestUrl = api + "date_req1=" + formattedDate + "&date_req2=" + formattedDate + "&VAL_NM_RQ=R01235";

            logger.info("Sending request to CBR API: {}", requestUrl);

            // Send HTTP GET request
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            logger.info("Received response code: {}", responseCode);

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            logger.debug("Received response from CBR API: {}", response.toString());

            // Parse the XML response
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(response.toString()));
            Document doc = builder.parse(is);
            NodeList valueNodes = doc.getElementsByTagName("Value");

            if (valueNodes.getLength() > 0) {
                String valueStr = valueNodes.item(0).getTextContent().replace(',', '.');
                double usdRate = Double.parseDouble(valueStr);
                logger.info("Parsed USD rate: {}", usdRate);
                return usdRate;
            } else {
                logger.warn("No 'Value' nodes found in the response");
            }

        } catch (Exception e) {
            logger.error("Error occurred while getting USD rate from CBR", e);
        }
        logger.warn("Returning default USD rate: 1.0");
        return 1.0;
    }
    private static LocalDate adjustToWeekday(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();

        if (dayOfWeek == DayOfWeek.SATURDAY) {
            return date.plusDays(2);
        } else if (dayOfWeek == DayOfWeek.SUNDAY) {
            return date.plusDays(1);
        } else {
            return date;
        }
    }
}