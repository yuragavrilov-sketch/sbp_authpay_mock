package ru.copperside.sbpauthpay;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankOperIdGeneratorTest {

    private final BankOperIdGenerator generator = new BankOperIdGenerator();

    @Test
    void generatesCanonicalUuidV7() {
        String id = generator.next();
        assertThat(id).hasSize(36);
        UUID parsed = UUID.fromString(id);
        assertThat(parsed.version()).isEqualTo(7);
    }

    @Test
    void generatesDistinctIds() {
        assertThat(generator.next()).isNotEqualTo(generator.next());
    }
}
