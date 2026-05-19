package com.newton.zaocycle.certification.application;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class TokenGeneratorTest {

    private final TokenGenerator generator = new TokenGenerator();
    private static final Pattern TOKEN_PATTERN = Pattern.compile("^ZC-[A-Z2-9]{4}-[A-Z2-9]{4}$");

    @Test
    void generate_matchesExpectedFormat() {
        String token = generator.generate();
        assertThat(token).matches(TOKEN_PATTERN);
    }

    @Test
    void generate_doesNotContainConfusingCharacters() {
        for (int i = 0; i < 200; i++) {
            String token = generator.generate();
            assertThat(token).doesNotContain("0", "O", "I", "1");
        }
    }

    @Test
    void generate_producesUniqueTokensAcross1000Generations() {
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            tokens.add(generator.generate());
        }
        assertThat(tokens).hasSizeGreaterThan(990);
    }
}
