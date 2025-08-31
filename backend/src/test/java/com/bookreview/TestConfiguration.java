package com.bookreview;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Test configuration for overriding beans in test environment.
 */
@org.springframework.boot.test.context.TestConfiguration
@Profile("test")
public class TestConfiguration {

    /**
     * Password encoder for testing.
     * Using a lower strength for faster test execution.
     *
     * @return BCryptPasswordEncoder with strength 4
     */
    @Bean
    @Primary
    public PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder(4); // Lower strength for faster tests
    }
}
