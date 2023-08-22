package practice.chat.domain.chat.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberRoom {


    @Id
    @GeneratedValue
    @Column(name = "member_room_id")
    private Long id;


    @Column(name="room_id")
    private Long room_id;

    @Column(name="member_id")
    private Long joined_member_id;


}
