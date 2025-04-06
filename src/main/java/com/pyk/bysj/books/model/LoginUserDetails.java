package com.pyk.bysj.books.model;

import com.pyk.bysj.books.model.entity.Login;
import com.pyk.bysj.books.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserDetails implements UserDetails {
  private User user;
  private String username;
  private String password;
  //通过springSecurity进行授权
  private Collection<? extends GrantedAuthority> authorities;

  public LoginUserDetails(User user, Login login, String role) {
    this.user = user;
    this.username = login.getUsername();
    this.password = login.getPassword();
    this.authorities = Collections.singleton(new SimpleGrantedAuthority(role));
  }

  /**
   * 返回用户拥有的权限集合
   * @return
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  // 后面四个方法都是用户是否可用、是否过期之类的。我都设置为true
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

}
