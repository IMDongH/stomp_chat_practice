package practice.chat.domain.user.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import practice.chat.domain.user.dao.MemberRepository;
import practice.chat.domain.user.dao.RefreshTokenDao;
import practice.chat.domain.user.domain.Member;
import practice.chat.domain.user.dto.MemberDTO;
import practice.chat.domain.user.dto.TokenDTO;
import practice.chat.global.jwt.TokenProvider;
import practice.chat.global.response.ErrorCode;
import practice.chat.global.response.GeneralException;
import practice.chat.global.util.CookieUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static practice.chat.domain.user.dto.MemberDTO.*;
import static practice.chat.global.constants.JwtConstants.ACCESSTOKEN;
import static practice.chat.global.constants.JwtConstants.ACCESS_TOKEN_COOKIE_EXPIRE_TIME;

@Service
@Slf4j
public class MemberService {

    private EntityManager em;
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    private PasswordEncoder passwordEncoder;

    private TokenProvider tokenProvider;
    private MemberRepository memberRepository;
    private RefreshTokenDao refreshTokenDao;



    public MemberService(EntityManager em, AuthenticationManagerBuilder authenticationManagerBuilder, @Lazy PasswordEncoder passwordEncoder, TokenProvider tokenProvider, MemberRepository memberRepository,  RefreshTokenDao refreshTokenDao) {
        this.em = em;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.memberRepository = memberRepository;
        this.refreshTokenDao = refreshTokenDao;
    }

    /**
     * 회원가입
     *
     * @param saveRequest
     * @return
     */

    @Transactional
    public Long saveMember(MemberSaveRequestDto saveRequest) {
        if (emailDuplicateCheck(saveRequest.getEmail())) {
            throw new GeneralException(ErrorCode.DUPLICATION_EMAIL, "이미 가입되어 있는 이메일입니다.");
        }

        saveRequest.passwordEncryption(passwordEncoder);
        Member member = saveRequest.toEntity();
        memberRepository.save(member);

        return member.getId();
    }

    /**
     * 회원 탈퇴
     *
     * @param deleteRequest
     */

    @Transactional
    public void deleteMember(MemberDeleteRequestDto deleteRequest, Long memberId) {

        Member findMember = findMemberByMemberId(memberId);

        if (findMember.getEmail() != deleteRequest.getEmail()) {

            throw new GeneralException(ErrorCode.INVALID_EMAIL_AND_PASSWORD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        String password = deleteRequest.getPassword();
        if (!passwordEncoder.matches(password, findMember.getPassword())) {
            throw new GeneralException(ErrorCode.INVALID_EMAIL_AND_PASSWORD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        memberRepository.deleteById(memberId);
    }


    /**
     * 로그인
     *
     * @param loginRequest
     * @return
     */
    @Transactional(readOnly = true)
    public MemberLoginServiceResponseDto login(MemberLoginRequestDto loginRequest, HttpServletResponse response, HttpServletRequest request) {

        // 로그인 정보로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();

        //비밀번호 체크 loginRequest 를 authenticationToken 형태로 바꿈
        //authenticate 가 실행될 때 UserDetailsService 에 만들어둔 loadUserByUsername 가 호출되어 DB 의 유저 정보와 일치하는지 확인
        try {
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            //jwt 토큰 생성
            TokenDTO tokenDto = tokenProvider.generateTokenDto(authentication);

            //response 에 유저 정보를 담기 위한 findById
            Member findMember = memberRepository.findById(Long.valueOf(authentication.getName()))
                    .orElseThrow(() -> new GeneralException(ErrorCode.NOTFOUND_MEMBER, "존재하지 않는 사용자입니다."));

            //유저 정보 + 토큰 값
            MemberLoginServiceResponseDto memberLoginResponse = tokenDto.toLoginEntity(findMember);

            String refreshToken = tokenDto.getRefreshToken();
            saveRefreshTokenInStorage(refreshToken, Long.valueOf(authentication.getName())); // 추후 DB 나 어딘가 저장 예정
            CookieUtil.deleteCookie(request, response, ACCESSTOKEN);
            CookieUtil.addCookie(response, ACCESSTOKEN, tokenDto.getAccessToken(), ACCESS_TOKEN_COOKIE_EXPIRE_TIME);

            return memberLoginResponse;
        } catch (BadCredentialsException e) {
            throw new GeneralException(ErrorCode.INVALID_EMAIL_AND_PASSWORD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다.");
        }


    }


    /**
     * 토큰 재발급
     *
     * @return
     */
    @Transactional
    public void reissue(HttpServletRequest request, HttpServletResponse response) {


        Cookie cookie = CookieUtil.getCookie(request, ACCESSTOKEN).orElse(null);
        String accessToken;
        if (cookie == null) {
            throw new GeneralException(ErrorCode.INVALID_REQUEST, "로그인이 필요한 서비스입니다.");
        } else {
            accessToken = cookie.getValue();
        }

        // 1. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(accessToken);

        // 2. 저장소에서 Member ID 를 기반으로 유저 확인
        if (!memberRepository.existsById(Long.valueOf(authentication.getName()))) {
            throw new GeneralException(ErrorCode.NOTFOUND_MEMBER, "존재하지 않는 사용자입니다.");
        }

        // 3. cache 에서 member Id 를 기반으로 refresh token 확인
        String refreshToken = refreshTokenDao.getRefreshToken(Long.valueOf(authentication.getName()));
        if (refreshToken == null) {
            throw new GeneralException(ErrorCode.NOTFOUND_REFRESHTOKEN, "로그아웃 된 사용자입니다.");
        }

        // 4. Refresh Token 검증
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new GeneralException(ErrorCode.INVALID_REFRESHTOKEN, "유효하지 않은 refresh token 입니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDTO tokenDTO = tokenProvider.generateTokenDto(authentication);

        // 6. 저장소 정보 업데이트
        saveRefreshTokenInStorage(tokenDTO.getRefreshToken(), Long.valueOf(authentication.getName()));// 추후 디비에 저장

        // 토큰 발급
        CookieUtil.addCookie(response, ACCESSTOKEN, tokenDTO.getAccessToken(), ACCESS_TOKEN_COOKIE_EXPIRE_TIME);
    }






    /**
     * redis 에 refresh token 저장
     *
     * @param refreshToken
     * @param memberId
     */
    private void saveRefreshTokenInStorage(String refreshToken, Long memberId) {
        refreshTokenDao.createRefreshToken(memberId, refreshToken);
    }



    /**
     * memberId로 멤버 찾기
     *
     * @param memberId
     * @return
     */
    @Transactional(readOnly = true)
    public Member findMemberByMemberId(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorCode.NOTFOUND_MEMBER, "존재하지 않는 사용자입니다."));

    }


    /**
     * email 로 member 찾기
     *
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public Member findMemberByEmail(String email) {

        Member findMember = memberRepository.findByEmail(email);
        if (findMember == null) {
            throw new GeneralException(ErrorCode.NOTFOUND_MEMBER, "존재하지 않는 사용자입니다.");
        }

        return findMember;
    }




    /**
     * email 중복 검사
     *
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public boolean emailDuplicateCheck(String email) {
        return memberRepository.existsByEmail(email);
    }


    /**
     * 쿠키 제거
     *
     * @param response
     * @param cookieName
     */
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null); // choiceCookieName(쿠키 이름)에 대한 값을 null로 지정
        cookie.setMaxAge(0); // 유효시간을 0으로 설정
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
