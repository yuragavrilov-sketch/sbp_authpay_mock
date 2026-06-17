package ru.copperside.sbpauthpay;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AuthPayControllerTest {

    private static final String REQ =
            "<Document stan=\"auth-001\"><GCSvc version=\"1\"><Payment><ReqAuthPay>"
            + "<PayType>PayToAccount</PayType><ReqType>ReqPay</ReqType></ReqAuthPay></Payment></GCSvc></Document>";

    @Autowired
    private WebTestClient client;

    @Test
    void allowsPaymentWithV7BankOperId() {
        String body = client.post().uri("/api/gcsvc/authpay")
                .contentType(MediaType.APPLICATION_XML)
                .bodyValue(REQ)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_XML)
                .expectBody(String.class).returnResult().getResponseBody();

        assertThat(body).contains("<AnsAuthPay>").contains("<Code>0</Code>").contains("stan=\"auth-001\"");

        Matcher m = Pattern.compile("<BankOperId>([^<]+)</BankOperId>").matcher(body);
        assertThat(m.find()).isTrue();
        UUID id = UUID.fromString(m.group(1));
        assertThat(id.version()).isEqualTo(7);
    }

    @Test
    void honoursForcedStatusCodeHeader() {
        String body = client.post().uri("/api/gcsvc/authpay")
                .contentType(MediaType.APPLICATION_XML)
                .header("X-Mock-Status-Code", "1001")
                .bodyValue(REQ)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).returnResult().getResponseBody();

        assertThat(body).contains("<Code>1001</Code>");
    }
}
