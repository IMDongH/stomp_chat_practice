package practice.chat.domain.user.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import practice.chat.domain.publicModel.Authority;
import practice.chat.domain.publicModel.LoginType;
import practice.chat.domain.user.dao.MemberRepository;
import practice.chat.domain.user.domain.Member;
import practice.chat.domain.user.domain.UserPrincipal;
import practice.chat.global.oauth.OAuth2UserInfo;
import practice.chat.global.oauth.OAuth2UserInfoFactory;
import practice.chat.global.response.ErrorCode;
import practice.chat.global.response.GeneralException;

import static practice.chat.domain.publicModel.LoginMemberType.GENERAL_USER;


@Service
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {


    private MemberRepository memberRepository;


    private PasswordEncoder passwordEncoder;

    CustomOAuth2UserService(MemberRepository memberRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws GeneralException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        LoginType loginType =  LoginType.valueOfLabel(userRequest.getClientRegistration().getRegistrationId().toUpperCase());
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(loginType.getType(), oAuth2User.getAttributes());

        Member findMember = memberRepository.findByEmail(userInfo.getEmail());


        if (findMember != null) { //회원가입 된경우
            if (loginType != findMember.getLoginType()) {
                throw new GeneralException(ErrorCode.DUPLICATION_SIGNUP, findMember.getLoginType().getType() + "로그인으로 회원가입 되어있습니다.");
            }
        } else {
            // 회원가입 안된 경우 회원가입 진행
            findMember = createUser(userInfo,loginType);
        }

        return UserPrincipal.create(findMember, oAuth2User.getAttributes());
    }


    private Member createUser(OAuth2UserInfo memberInfo, LoginType loginType){
        String password = passwordEncoder.encode(memberInfo.getId());
        Member createMember = Member.builder()
                .email(memberInfo.getEmail())
                .password(password)
                .name(memberInfo.getName())
                .loginType(loginType)
                .loginMemberType(GENERAL_USER)
                .authority(Authority.ROLE_USER)
                .build();

        return memberRepository.save(createMember);
    }

}