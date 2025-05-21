package com.pyk.bysj.books.config;

import com.pyk.bysj.books.exception.handler.auth.AccessDeniedHandlerImpl;
import com.pyk.bysj.books.exception.handler.auth.AuthenticationEntryPointImpl;
import com.pyk.bysj.books.filter.JwtAuthenticationTokenFilter;
import com.pyk.bysj.books.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //开启webSecurity服务
public class SecurityConfig {
  @Autowired
  private CustomUserDetailsService customUserDetailsService;

  @Autowired
  JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

//  private final JwtAuthFilter jwtAuthFilter;
  @Autowired
  private AuthenticationEntryPointImpl authenticationEntryPoint;

  @Autowired
  private AccessDeniedHandlerImpl accessDeniedHandler;

  @Bean
  public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    //将编写的UserDetailsService注入进来
    provider.setUserDetailsService(customUserDetailsService);
    //将使用的密码编译器加入进来
    provider.setPasswordEncoder(passwordEncoder);
    //将provider放置到AuthenticationManager 中
    ProviderManager providerManager = new ProviderManager(provider);
    return providerManager;
  }

//  @Bean
//  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//    return config.getAuthenticationManager(); // 使用Spring Security默认配置
//  }

  /*
   * 在security安全框架中，提供了若干密码解析器实现类型。
   * 其中BCryptPasswordEncoder 叫强散列加密。可以保证相同的明文，多次加密后，
   * 密码有相同的散列数据，而不是相同的结果。
   * 匹配时，是基于相同的散列数据做的匹配。
   * Spring Security 推荐使用 BCryptPasswordEncoder 作为密码加密和解析器。
   * */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /*
   * 配置权限相关的配置
   * 安全框架本质上是一堆的过滤器，称之为过滤器链，每一个过滤器链的功能都不同
   * 设置一些链接不要拦截
   * */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.csrf(AbstractHttpConfigurer::disable); // 关闭csrf
    // 配置路径相关
    httpSecurity.authorizeHttpRequests(it->
            it.requestMatchers("/api/user/login", "/api/user/register", "/api/captcha", "/api/verify").permitAll()  //设置登录路径所有人都可以访问
                    .requestMatchers("/api/admin/**").hasRole("ADMIN") // 管理员接口
                    .requestMatchers("/api/user/**").hasAnyRole("ADMIN", "USER") // 用户接口
                    .anyRequest().authenticated()  //其他路径都要进行拦截
    );
    // 异常处理
    httpSecurity.exceptionHandling(exceptions -> exceptions
            .authenticationEntryPoint(authenticationEntryPoint) // 认证失败处理
            .accessDeniedHandler(accessDeniedHandler) // 权限不足处理
    );

    // 添加JWT过滤器
    httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();

  }

//  @Bean
//  public CommandLineRunner testPassword(PasswordEncoder encoder) {
//    return args -> {
//      String rawPassword = "123456"; // 用户输入的密码
//      String encodedPassword = "$2a$10$WkvhOZ8TATPS.Lygi7dKdOxSjEOiyPf/Gv4uJpZnYEjzcjBx9313O"; // 数据库中的密码
//      System.out.println("密码匹配结果: " + encoder.matches(rawPassword, encodedPassword));
//    };
//  }


}