package com.franc.standard.config;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class JasyptConfigTest {

    private static final String ENCRYPT_KEY = "jasypt123";

    private StandardPBEStringEncryptor encryptor;


    @BeforeAll
    public void init() {
        encryptor = new StandardPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(ENCRYPT_KEY);
        config.setAlgorithm("PBEWithMD5AndTripleDES"); // 권장되는 기본 알고리즘
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
    }

    @Test
    @DisplayName("jasypt_암복호화_테스트")
    public void jasypt_test() {
        // # Given
        String value = "franc";

        // # When
        String decValue = jasyptDecode(jasyptEncode(value));

        // # Then
        assertThat(decValue).isEqualTo(value);
    }


    /**
     * jasypt 암호화
     * @param value
     * @return
     */
    public String jasyptEncode(String value) {
        return encryptor.encrypt(value);
    }

    /**
     * jasypt 복호화
     * @param value
     * @return
     */
    public String jasyptDecode(String value) {
        return encryptor.decrypt(value);
    }

}