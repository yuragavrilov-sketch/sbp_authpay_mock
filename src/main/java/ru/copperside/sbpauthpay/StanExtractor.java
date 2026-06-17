package ru.copperside.sbpauthpay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;

/** Reads the {@code stan} attribute of the root {@code <Document>} (echoed back on the response). */
@Component
public class StanExtractor {

    private static final Logger log = LoggerFactory.getLogger(StanExtractor.class);
    private static final XMLInputFactory XML = XMLInputFactory.newInstance();

    static {
        XML.setProperty(XMLInputFactory.IS_COALESCING, true);
        XML.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        XML.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
    }

    public String extract(byte[] xmlBytes) {
        if (xmlBytes == null || xmlBytes.length == 0) {
            return null;
        }
        XMLStreamReader reader = null;
        try {
            reader = XML.createXMLStreamReader(new ByteArrayInputStream(xmlBytes));
            while (reader.hasNext()) {
                if (reader.next() == XMLStreamConstants.START_ELEMENT
                        && "Document".equals(reader.getLocalName())) {
                    return reader.getAttributeValue(null, "stan");
                }
            }
        } catch (Exception e) {
            log.warn("Failed to parse stan: {}", e.getMessage());
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (Exception ignored) { }
            }
        }
        return null;
    }
}
