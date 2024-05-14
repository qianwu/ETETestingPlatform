package org.charlotte.e2ecore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private KeyCloakProp keyCloakProp;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/api/case/exec/complete")
                .antMatchers("/api/project/updateProjectCaseInfo")
                .antMatchers("/api/projectCaseCount/create");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/**")
                .authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(new KeyCloakJwtBearerTokenAuthenticationConverter(keyCloakProp.getResource()));
    }

}