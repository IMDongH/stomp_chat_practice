package practice.chat.domain.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import practice.chat.domain.chat.dto.MessageResponseDto;
import practice.chat.domain.chat.dto.RoomInfoResponseDto;
import practice.chat.domain.chat.dto.RoomInitMessageResponseDto;
import practice.chat.domain.chat.model.ChatRoom;
import practice.chat.domain.chat.service.ChatService;
import practice.chat.global.util.SecurityUtil;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatService chatService;

    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }


    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<RoomInfoResponseDto> room() {
        return chatService.findAllRoom(SecurityUtil.getCurrentMemberId());
    }


    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        return chatService.createRoom(name);
    }


    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable Long roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }
    
    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public RoomInitMessageResponseDto roomInfo(@PathVariable Long roomId) {
        return chatService.findById(roomId);
    }
}
