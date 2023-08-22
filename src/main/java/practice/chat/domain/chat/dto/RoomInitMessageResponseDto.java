package practice.chat.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomInitMessageResponseDto {

    private Long roomId;
    private String roomName;

    private List<MessageResponseDto> messages;
}
