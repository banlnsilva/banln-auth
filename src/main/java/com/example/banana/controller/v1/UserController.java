package com.example.banana.controller.v1;

import com.example.banana.advice.exception.CUserNotFoundException;
import com.example.banana.entity.User;
import com.example.banana.model.response.CommonResult;
import com.example.banana.model.response.ListResult;
import com.example.banana.model.response.SingleResult;
import com.example.banana.repository.UserJpaRepository;
import com.example.banana.service.ResponseService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"1. User"}) // UserController를 대표하는 최상단 타이틀 영역에 표시될 값 세팅
@RequiredArgsConstructor // class 내부의 final 객체는 Constructor Injection 수행, @Autowired도 가능
@RestController // 결과를 JSON으로 도출
@RequestMapping(value = "/v1") // api resource를 버전별로 관리, /v1 을 모든 리소스 주소에 적용
public class UserController {

    private final UserJpaRepository userJpaRepository; // Jpa를 활용한 CRUD 쿼리 가능
    private final ResponseService responseService; // 결과를 처리하는 Service

    @Secured("ROLE_USER")
    @ApiOperation(value = "회원 리스트 조회", notes = "모든 회원을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/users")
    public ListResult<User> findAllUser() { // 데이터가 1개 이상일 수 있기에 List<User>로 선언
        // JPA를 사용하면 CRUD에 대해 설정 없이 쿼리 사용 가능 (select * from user 와 같음)
        //결과 데이터가 여러개인 경우 getListResult 활용
        return responseService.getListResult(userJpaRepository.findAll());
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @ApiOperation(value = "회원 단건 조회", notes = "회원번호(msrl)로 회원을 조회한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @GetMapping(value = "/user")
    public SingleResult<User> findUserById() {
        // SecurityContext에서 인증 받은 회원의 정보를 얻어온다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = authentication.getName();
        // 결과 데이터가 단일건인 경우 getSingleResult를 이용하여 결과를 출력
        return  responseService.getSingleResult(userJpaRepository.findByUid(id).orElseThrow(CUserNotFoundException::new));
    }

    @ApiOperation(value = "회원 입력", notes = "회원을 입력한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "uid", value = "회원아이디", required = true),
            @ApiImplicitParam(name = "name", value = "회원이름", required = true),
            @ApiImplicitParam(name = "password", value = "패스워드", required = true)
    })
    @PostMapping(value = "/user") // user 테이블에 데이터를 입력하는 부분 insert into user (msrl, name, uid) values (null, ?, ?) 와 같음
    public SingleResult<User> save(@RequestParam String uid, @RequestParam String name, @RequestParam String password) {
//    public SingleResult<User> save(@ApiParam(value = "회원아이디", required = true) @RequestParam String uid,
//                                   @ApiParam(value = "회원이름", required = true) @RequestParam String name) {
        User user = User.builder()
                .uid(uid) // User 클래스에서 만들어진 변수 uid, name
                .name(name)
                .password(password)
                .build();

        return responseService.getSingleResult(userJpaRepository.save(user));
    }

    @ApiOperation(value = "회원 수정", notes = "회원정보를 수정한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @PutMapping(value = "/user")
    public SingleResult<User> modify(
            @ApiParam(value = "회원번호", required = true) @RequestParam long msrl,
            @ApiParam(value = "회원이름", required = true) @RequestParam String name
            ) {
        User user = User.builder()
                .msrl(msrl)
                .name(name)
                .build();

        return responseService.getSingleResult(userJpaRepository.save(user));
    }

    @ApiOperation(value = "회원 삭제", notes = "msrl로 회원정보를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 access_token",
                    required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping(value = "/user/{msrl}")
    public CommonResult delete(
            @ApiParam(value = "회원정보", required = true) @PathVariable long msrl) {
        // deleteById id를 받아 delete query 실행
        userJpaRepository.deleteById(msrl);

        // 성공 결과 정보만 필요한 경우 getSuccessResult()를 이용하여 결과를 출력
        return responseService.getSuccessResult();
    }
}
