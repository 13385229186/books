package com.pyk.bysj.books.aop;

import com.pyk.bysj.books.annotation.CurrentUser;
import com.pyk.bysj.books.model.LoginUserDetails;
import com.pyk.bysj.books.model.entity.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

@Aspect
@Component
public class CurrentUserAspect {

  @Around("within(@org.springframework.stereotype.Controller *) || " +
          "within(@org.springframework.web.bind.annotation.RestController *)")
  public Object injectCurrentUser(ProceedingJoinPoint joinPoint) throws Throwable {
    // 获取方法签名
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();

    // 获取参数注解信息
    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    Object[] args = joinPoint.getArgs();

    // 查找@CurrentUser注解的参数
    for (int i = 0; i < args.length; i++) {
      for (Annotation annotation : parameterAnnotations[i]) {
        if (annotation.annotationType() == CurrentUser.class) {
          args[i] = getCurrentUser();
          break;
        }
      }
    }

    return joinPoint.proceed(args);
  }

  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new AccessDeniedException("用户未认证");
    }
    Object principal = authentication.getPrincipal();
    if (principal instanceof LoginUserDetails) {
      return ((LoginUserDetails) principal).getUser();
    }
    throw new AccessDeniedException("无效的用户凭证类型");
  }
}
