package practice.chat.domain.chat.dao;

import practice.chat.domain.chat.dto.RoomInfoResponseDto;

import java.util.List;

public interface RoomRepositoryDao {

    List<RoomInfoResponseDto> findRoomByMemberId(Long memberId);
}
