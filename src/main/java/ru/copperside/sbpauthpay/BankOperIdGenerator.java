package ru.copperside.sbpauthpay;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedEpochGenerator;
import org.springframework.stereotype.Component;

/** Generates canonical 36-char UUID v7 BankOperId values (time-ordered, Unix-epoch based). */
@Component
public class BankOperIdGenerator {

    private final TimeBasedEpochGenerator generator = Generators.timeBasedEpochGenerator();

    public String next() {
        return generator.generate().toString();
    }
}
