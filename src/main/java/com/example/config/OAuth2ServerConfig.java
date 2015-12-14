package com.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import javax.sql.DataSource;

/**
 * Created by Just_CJ on 2015/12/7.
 */

@Configuration
public class OAuth2ServerConfig {

    private static final String RESOURCE_ID = "OAuthDemo";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
            resources.resourceId(RESOURCE_ID);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {

            // @formatter:off
            http
//                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
//                    .and()
//                      .requestMatchers().antMatchers("/greeting", "/greeting2")
                      .requestMatcher(
                          new OrRequestMatcher(
                              new AntPathRequestMatcher("/greeting"),
                              new AntPathRequestMatcher("/greeting2")
                          )
                      )
//                      .and()
                      .authorizeRequests()
                      .antMatchers("/greeting").access("#oauth2.hasScope('scope1')")
                      .antMatchers("/greeting2").access("#oauth2.hasScope('scope2')");
            // @formatter:on

        }

    }


    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Value("${oauth.paths.token:/oauth/token}")
        private String tokenPath = "/oauth/token";

        @Value("${oauth.paths.token_key:/oauth/token_key}")
        private String tokenKeyPath = "/oauth/token_key";

        @Value("${oauth.paths.check_token:/oauth/check_token}")
        private String checkTokenPath = "/oauth/check_token";

        @Value("${oauth.paths.authorize:/oauth/authorize}")
        private String authorizePath = "/oauth/authorize";

        @Value("${oauth.paths.confirm:/oauth/confirm_access}")
        private String confirmPath = "/oauth/confirm_access";

        @Autowired
        private DataSource dataSource;

        @Autowired
        private TokenStore tokenStore;

//        @Autowired
//        private UserApprovalHandler userApprovalHandler;

        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Autowired
        private ServerProperties server;


        @Bean
        public TokenStore tokenStore() {
//            return new InMemoryTokenStore();
            return new JdbcTokenStore(dataSource);
        }

        @Bean
        public ApprovalStore approvalStore() throws Exception {
            TokenApprovalStore store = new TokenApprovalStore();
            store.setTokenStore(tokenStore);
            return store;
        }

//        @Override
//        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//            oauthServer.checkTokenAccess("hasRole('ROLE_CLIENT')");
//        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {


            String prefix = server.getServletPrefix();
            endpoints.prefix(prefix);
            // @formatter:off
            endpoints.authenticationManager(authenticationManager)
                    .tokenStore(tokenStore)
                    .pathMapping("/oauth/confirm_access", confirmPath)
                    .pathMapping("/oauth/token", tokenPath)
                    .pathMapping("/oauth/check_token", checkTokenPath)
                    .pathMapping("/oauth/token_key", tokenKeyPath)
                    .pathMapping("/oauth/authorize", authorizePath);
            // @formatter:on
        }




        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

            clients.jdbc(dataSource);
//            clients.inMemory()
//                    .withClient("my-client")
//                    .resourceIds(RESOURCE_ID)
//                    .secret("123456")
//                    .authorizedGrantTypes("authorization_code", "implicit")
//                    .authorities("ROLE_CLIENT")
//                    .scopes("scope1", "scope2", "scope3", "scope4");

        }

    }

}
