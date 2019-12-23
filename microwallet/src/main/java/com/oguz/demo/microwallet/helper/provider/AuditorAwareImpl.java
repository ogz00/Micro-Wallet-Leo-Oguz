package com.oguz.demo.microwallet.helper.provider;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    public static final String TEST_USER = "TEST_USER";
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(TEST_USER);
    }

}
