package practice.chat.domain.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.chat.domain.chat.dao.MessageRepository;
import practice.chat.domain.chat.dao.MessageRepositoryDao;
import practice.chat.domain.chat.dao.RoomRepository;
import practice.chat.domain.chat.dao.RoomRepositoryDao;
import practice.chat.domain.chat.domain.Message;
import practice.chat.domain.chat.domain.Room;
import practice.chat.domain.chat.dto.MessageResponseDto;
import practice.chat.domain.chat.dto.RoomInfoResponseDto;
import practice.chat.domain.chat.dto.RoomInitMessageResponseDto;
import practice.chat.domain.chat.model.ChatMessage;
import practice.chat.domain.chat.model.ChatRoom;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final RoomRepositoryDao RoomRepositoryDaoImpl;
    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final MessageRepositoryDao MessageRepositoryDaoImpl;

    //채팅방 불러오기
    public List<RoomInfoResponseDto> findAllRoom(Long memberId) {

//        List<RoomInfoResponseDto> roomByMemberId = ;

//        //채팅방 최근 생성 순으로 반환
//        List<ChatRoom> result = new ArrayList<>(chatRooms.values());
//        Collections.reverse(result);

        return RoomRepositoryDaoImpl.findRoomByMemberId(memberId);
    }

    //채팅방 하나 불러오기
    public RoomInitMessageResponseDto findById(Long roomId) {
        List<MessageResponseDto> messages = MessageRepositoryDaoImpl.findMessageByRoomId(roomId);
        Room room = roomRepository.findById(roomId).orElseThrow(RuntimeException::new);
        return RoomInitMessageResponseDto.builder()
                .messages(messages)
                .roomName(room.getRoomName())
                .build();
    }


    @Transactional
    public void saveMessage(ChatMessage message){
        Message enterMessage = Message.builder()
                .room_id(Long.valueOf(message.getRoomId()))
                .sender_id(Long.valueOf(message.getSender())).message(message.getMessage()).build();
        messageRepository.save(enterMessage);
        Room room = roomRepository.findById(Long.valueOf(message.getRoomId())).orElseThrow(RuntimeException::new);
        room.updateLast_chat_message(message.getMessage());
    }
    //채팅방 생성
    public ChatRoom createRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name);
//        chatRooms.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }
}
