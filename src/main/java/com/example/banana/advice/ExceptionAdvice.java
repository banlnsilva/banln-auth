package com.example.banana.advice;

import com.example.banana.advice.exception.CEmailSigninFailedException;
import com.example.banana.advice.exception.CUserNotFoundException;
import com.example.banana.model.response.CommonResult;
import com.example.banana.service.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@RequiredArgsConstructor
@RestControllerAdvice // 예외 발생 시 json 형태로 결과 반환 | 프로젝트의 모든 Controller에 로직 적용
//@RestControllerAdvice(basePackages = "com.example.banana") //banana 하위의 Controller 에만 로직 적용
public class ExceptionAdvice {
    private final ResponseService responseService; // 결과에 대한 정보를 도출하는 클래스 명시
    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("unKnown.code")), getMessage("unKnown.msg"));
    }

    @ExceptionHandler(CUserNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult userNotFoundException(HttpServletRequest request, CUserNotFoundException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("userNotFound.code")), getMessage("userNotFound.msg"));
    }

    @ExceptionHandler(CEmailSigninFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult emailSigninFailed(HttpServletRequest request, CEmailSigninFailedException e) {
        return responseService.getFailResult(Integer.valueOf(getMessage("emailSigninFailed.code")), getMessage("emailSigninFailed.msg"));

    }

//    @ExceptionHandler(CAuthenticationEntryPointException.class)
//    public CommonResult authenticationEntryPointException(HttpServletRequest request, CAuthenticationEntryPointException e) {
//        return responseService.getFailResult(Integer.valueOf(getMessage("entryPointException.code")), getMessage("entryPointException.msg"));
//    }
//
//    @ExceptionHandler(AccessDeniedException.class)
//    public CommonResult AccessDeniedException(HttpServletRequest request, AccessDeniedException e) {
//        return responseService.getFailResult((Integer.valueOf(getMessage("accessDenied.code"))), getMessage("accessDenied.msg"));
//    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
//        return messageSource.getMessage(code, args, Locale.ENGLISH);
    }

}
