package practice.chat.domain.chat.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.chat.domain.chat.domain.MemberRoom;
import practice.chat.domain.chat.domain.QMemberRoom;
import practice.chat.domain.chat.dto.RoomInfoResponseDto;

import java.util.List;

import static practice.chat.domain.chat.domain.QMemberRoom.*;
import static practice.chat.domain.chat.domain.QRoom.room;

@Repository
@RequiredArgsConstructor
public class RoomRepositoryDaoImpl implements RoomRepositoryDao {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<RoomInfoResponseDto> findRoomByMemberId(Long memberId) {

        List<RoomInfoResponseDto> result = jpaQueryFactory
                .select(Projections.constructor(
                        RoomInfoResponseDto.class,
                        room.id,
                        room.last_chat_message,
                        room.roomName))
                .from(room)
                .where(
                        room.id.in(jpaQueryFactory.select(memberRoom.room_id)
                        .from(memberRoom)
                        .where(memberRoom.joined_member_id.eq(memberId)).fetch()))
                .orderBy(
                        new OrderSpecifier(Order.DESC, room.id)).fetch();


        return result;
    }
}
