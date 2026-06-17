package ru.copperside.sbpauthpay;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class AuthPayController {

    private static final String ANS_AUTH_PAY_TEMPLATE = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <Document stan="%s">
                <GCSvc version="1">
                    <Payment>
                        <AnsAuthPay>
                            <Status>
                                <Code>%d</Code>
                            </Status>
                            <BankOperId>%s</BankOperId>
                        </AnsAuthPay>
                    </Payment>
                </GCSvc>
            </Document>""";

    private final StanExtractor stanExtractor;
    private final BankOperIdGenerator bankOperIdGenerator;

    public AuthPayController(StanExtractor stanExtractor, BankOperIdGenerator bankOperIdGenerator) {
        this.stanExtractor = stanExtractor;
        this.bankOperIdGenerator = bankOperIdGenerator;
    }

    @PostMapping(value = "/api/gcsvc/authpay", produces = MediaType.APPLICATION_XML_VALUE)
    public Mono<ResponseEntity<String>> authPay(
            @RequestBody(required = false) byte[] body,
            @RequestHeader(value = "X-Mock-Status-Code", required = false) Integer forcedStatusCode) {
        String stan = escapeXml(stanExtractor.extract(body));
        int code = forcedStatusCode != null ? forcedStatusCode : 0;
        String xml = ANS_AUTH_PAY_TEMPLATE.formatted(stan, code, bankOperIdGenerator.next());
        return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(xml));
    }

    private static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
