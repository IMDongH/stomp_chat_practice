package practice.chat.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;
import practice.chat.domain.chat.dao.MessageRepository;
import practice.chat.domain.chat.dao.RoomRepository;
import practice.chat.domain.chat.domain.Message;
import practice.chat.domain.chat.domain.Room;
import practice.chat.domain.chat.model.ChatMessage;
import practice.chat.domain.chat.service.ChatService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessageSendingOperations sendingOperations;

    private final ChatService chatService;
    @MessageMapping("/chat/message")
    public void enter(ChatMessage message) {
//        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
//            message.setMessage(message.getSender()+"님이 입장하였습니다.");
//        }

        chatService.saveMessage(message);
        sendingOperations.convertAndSend("/topic/chat/room/"+message.getRoomId(),message);
    }

//    @MessageMapping("/chat/message")
//    public void send(ChatMessage message) {
////        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
////            message.setMessage(message.getSender()+"님이 입장하였습니다.");
////        }
//        sendingOperations.convertAndSend("/topic/chat/room/"+message.getRoomId(),message);
//    }
}
