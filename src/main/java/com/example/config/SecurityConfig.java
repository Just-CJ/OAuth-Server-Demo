package com.example.config;

/**
 * Created by Just_CJ on 2015/12/7.
 */
import com.example.service.AuthenticationService;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
//@EnableWebSecurity(debug=true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    public void globalUserDetails(AuthenticationManagerBuilder auth) throws Exception {
//        ShaPasswordEncoder encoder = new ShaPasswordEncoder();
        auth.userDetailsService(authenticationService); //.passwordEncoder(encoder);

//            .inMemoryAuthentication()
//                .withUser("test1").password("123456").roles("ADMIN").and()
//                .withUser("test2").password("123456").roles("USER");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**", "/images/**", "/oauth/uncache_approvals", "/oauth/cache_approvals");
    }

    @Override
    @Bean
    @Qualifier("authenticationManagerBean")
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/home").permitAll()
                .anyRequest().authenticated()
                .and()
//            .csrf()
//                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
//                .disable()
            .formLogin()
//                .loginProcessingUrl("/login")
//                .failureUrl("/login?error")
                .loginPage("/login")
                .permitAll()
                .and()
            .logout()
//                .logoutUrl("/login?logout")
//                .logoutSuccessUrl("/login")
                .permitAll();

//        super.configure(http);
    }

    @Bean(name = "dataSource")
    public HikariDataSource dataSource() {
        HikariDataSource driverManagerDataSource = new HikariDataSource();
//        driverManagerDataSource.setDriverClassName("com.mysql.jdbc.Driver");
//        driverManagerDataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        driverManagerDataSource.setJdbcUrl("jdbc:mysql://localhost:3306/oauthdemo");
        driverManagerDataSource.setUsername("Just_CJ");
        driverManagerDataSource.setPassword("940411");
        return driverManagerDataSource;
    }

}
