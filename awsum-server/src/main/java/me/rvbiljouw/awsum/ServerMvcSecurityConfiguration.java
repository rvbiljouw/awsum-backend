package me.rvbiljouw.awsum;

import me.rvbiljouw.awsum.auth.ApiAuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author rvbiljouw
 */
@Configuration
@EnableWebSecurity
public class ServerMvcSecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private ApiAuthenticationInterceptor authFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .cors().and().csrf().disable();
    }

}
