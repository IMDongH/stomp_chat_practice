package practice.chat.domain.chat.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomInfoResponseDto {
    private Long roomId;
    private String lastMessageId;
    private String roomName;

    @Builder
    public RoomInfoResponseDto(Long roomId, String lastMessageId, String roomName) {
        this.roomId = roomId;
        this.lastMessageId = lastMessageId;
        this.roomName = roomName;
    }
}
