package practice.chat.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import practice.chat.domain.user.domain.Member;

@Getter
@NoArgsConstructor
public class TokenDTO {

    private String grantType;
    private String accessToken;

    private String refreshToken;

    private Long accessTokenExpiresIn;

    @Builder
    public TokenDTO(String grantType, String accessToken, String refreshToken, Long accessTokenExpiresIn) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiresIn = accessTokenExpiresIn;
    }

    public MemberDTO.MemberLoginServiceResponseDto toLoginEntity(Member member){
        return MemberDTO.MemberLoginServiceResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .loginMemberType(member.getLoginMemberType())
                .authority(member.getAuthority())
                .build();
    }


}