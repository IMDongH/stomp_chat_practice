package practice.chat.domain.user.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import practice.chat.domain.user.application.MemberService;
import practice.chat.domain.user.dto.MemberDTO;
import practice.chat.global.response.ResultDataResponseDTO;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class MemberAuthController {

    private final MemberService memberService;

    /**
     * 회원가입
     * @param memberRequestDto
     * @return
     */
    @PostMapping("/signup")
    public ResultDataResponseDTO signup(@Valid @RequestBody MemberDTO.MemberSaveRequestDto memberRequestDto) {

        memberService.saveMember(memberRequestDto);

        return ResultDataResponseDTO.empty();
    }

    /**
     * 로그인
     * @param loginRequestDto
     * @param response
     * @return
     */
    @PostMapping("/login")
    public ResultDataResponseDTO<MemberDTO.MemberLoginServiceResponseDto> login(@RequestBody MemberDTO.MemberLoginRequestDto
                                                                              loginRequestDto, HttpServletResponse response,HttpServletRequest request) {

        MemberDTO.MemberLoginServiceResponseDto loginResponseDto = memberService.login(loginRequestDto,response,request);

        return ResultDataResponseDTO.of(loginResponseDto);
    }

    /**
     * JWT 토큰 재발급
     * @return
     */
    @PostMapping("/reissue")
    public ResultDataResponseDTO reissue(HttpServletRequest request,HttpServletResponse response) {

        memberService.reissue(request,response);
        return ResultDataResponseDTO.empty();
    }


}
