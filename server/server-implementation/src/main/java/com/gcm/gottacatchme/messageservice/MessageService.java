package com.gcm.gottacatchme.messageservice;

import java.io.*;
import java.util.*;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource; import javax.xml.transform.stream.StreamResult;

class MessageService {
    public static final String URL = "https://sgw01.cm.nl/gateway.ashx";

    public static void sendHintToUser(String hintText) {
        try {
            UUID productToken = UUID.fromString("23fd07b9-66b3-40a1-b3ff-ebdfaabf1ad2");
            String xml = createXml(productToken, "WCC Develo.", "0031617380214", "You have a new hint: " + hintText);
            String response = doHttpPost(URL, xml);
            System.out.println("Response: " + response);
            System.in.read();
        } catch (IOException e) {
            System.err.println(e); // Display the string.
        }
    }

    private static String createXml(UUID productToken, String sender, String recipient, String message) {
        try {
            ByteArrayOutputStream xml = new ByteArrayOutputStream();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);

            // Get the DocumentBuilder
            DocumentBuilder docBuilder = factory.newDocumentBuilder();

            // Create blank DOM Document
            DOMImplementation impl = docBuilder.getDOMImplementation();
            Document doc = impl.createDocument(null, "MESSAGES", null);

            // create the root element
            Element root = doc.getDocumentElement();
            Element authenticationElement = doc.createElement("AUTHENTICATION");
            Element productTokenElement = doc.createElement("PRODUCTTOKEN");
            authenticationElement.appendChild(productTokenElement);
            Text productTokenValue = doc.createTextNode("" + productToken);
            productTokenElement.appendChild(productTokenValue);
            root.appendChild(authenticationElement);

            Element msgElement = doc.createElement("MSG");
            root.appendChild(msgElement);

            Element fromElement = doc.createElement("FROM");
            Text fromValue = doc.createTextNode(sender);
            fromElement.appendChild(fromValue);
            msgElement.appendChild(fromElement);

            Element bodyElement = doc.createElement("BODY");
            Text bodyValue = doc.createTextNode(message);
            bodyElement.appendChild(bodyValue);
            msgElement.appendChild(bodyElement);

            Element toElement = doc.createElement("TO");
            Text toValue = doc.createTextNode(recipient);
            toElement.appendChild(toValue);
            msgElement.appendChild(toElement);

            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            aTransformer.setOutputProperty(OutputKeys.INDENT, "yes");

            Source src = new DOMSource(doc);
            Result dest = new StreamResult(xml);
            aTransformer.transform(src, dest);

            return xml.toString();

        } catch (TransformerException ex) {
            System.err.println(ex);
            return ex.toString();
        } catch (ParserConfigurationException p) {
            System.err.println(p);
            return p.toString();
        }
    }

    private static String doHttpPost(String urlString, String requestString) {
        try {
            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(requestString);
            wr.flush();
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String response = "";
            while ((line = rd.readLine()) != null) {
                response += line;
            }
            wr.close();
            rd.close();

            return response;
        } catch (IOException ex) {
            System.err.println(ex); return ex.toString();
        }
    }
}