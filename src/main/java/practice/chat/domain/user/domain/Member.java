package practice.chat.domain.user.domain;

import lombok.*;
import practice.chat.domain.publicModel.Authority;
import practice.chat.domain.publicModel.LoginMemberType;
import practice.chat.domain.publicModel.LoginType;
import practice.chat.domain.publicModel.MemberBase;
import practice.chat.domain.user.dto.MemberDTO;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import static practice.chat.domain.user.dto.MemberDTO.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends MemberBase {

    private String twitter;

    private String instagram;

    private String otherSns;

    private String information;

    private String profile;
    private String egName;



    @Builder
    public Member(Long id, String email, String name, String password, LoginMemberType loginMemberType, LoginType loginType, Authority authority, String profile) {
        super(id, email, name, password, loginMemberType, loginType, authority,profile);
        this.profile = profile;
    }







    public MemberInfoResponseDto toFindMemberDto(){
        return MemberInfoResponseDto.builder()
                .id(this.getId())
                .email(this.getEmail())
                .name(this.getName())
                .loginMemberType(this.getLoginMemberType())
                .loginType(this.getLoginType())
                .authority(this.getAuthority())
                .information(this.getInformation())
                .instagram(instagram)
                .twitter(twitter)
                .otherSns(otherSns)
                .profile(profile)
                .egName(this.getEgName())
                .build();
    }

    public MemberBoardInfoResponseDto toMessageMemberDTO(){
        return MemberBoardInfoResponseDto.builder()
                .id(this.getId())
                .email(this.getEmail())
                .name(this.getName())
                .build();
    }

    public MemberBoardInfoResponseDto toBoardMemberDTO(){
        return MemberBoardInfoResponseDto.builder()
                .id(this.getId())
                .email(this.getEmail())
                .name(this.getName())
                .build();
    }
    public void updatePassword(String password){
        this.password = password;
    }


    public void updateProfile(String profileUrl){
        this.profile = profileUrl;
    }


}
