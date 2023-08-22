package practice.chat.domain.chat.dao;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import practice.chat.domain.chat.domain.QMessage;
import practice.chat.domain.chat.dto.MessageResponseDto;

import java.util.List;

import static practice.chat.domain.chat.domain.QMessage.*;
import static practice.chat.domain.chat.domain.QRoom.room;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryDaoImpl implements MessageRepositoryDao {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<MessageResponseDto> findMessageByRoomId(Long roomId) {
        List<MessageResponseDto> messages = jpaQueryFactory.select(
                        Projections.constructor(
                                MessageResponseDto.class,
                                message1.id,
                                message1.message,
                                message1.sender_id
                        )
                )
                .from(message1)
                .where(message1.room_id.eq(roomId))
                .orderBy(
                        new OrderSpecifier(Order.DESC, message1.id)).fetch();
        return messages;
    }
}
