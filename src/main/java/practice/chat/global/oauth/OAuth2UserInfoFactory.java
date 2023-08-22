package practice.chat.global.oauth;

import practice.chat.global.oauth.impl.FacebookOAuth2UserInfo;
import practice.chat.global.oauth.impl.GoogleOAuth2UserInfo;
import practice.chat.global.oauth.impl.KakaoOAuth2UserInfo;
import practice.chat.global.oauth.impl.NaverOAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(String loginType, Map<String, Object> attributes) {
        switch (loginType) {
            case "GOOGLE": return new GoogleOAuth2UserInfo(attributes);
            case "FACEBOOK": return new FacebookOAuth2UserInfo(attributes);
            case "NAVER": return new NaverOAuth2UserInfo(attributes);
            case "KAKAO": return new KakaoOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("Invalid Provider Type.");
        }
    }
}
