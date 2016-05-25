package com.wccgroup.hackbattle.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import com.wccgroup.hackbattle.config.Constants;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentUserLogin();
        return (userName != null ? userName : Constants.SYSTEM_ACCOUNT);
    }
}
