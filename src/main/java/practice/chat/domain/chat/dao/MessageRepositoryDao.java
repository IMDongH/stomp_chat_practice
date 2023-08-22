package practice.chat.domain.chat.dao;

import practice.chat.domain.chat.dto.MessageResponseDto;

import java.util.List;

public interface MessageRepositoryDao {

    List<MessageResponseDto> findMessageByRoomId(Long roomId);
}
