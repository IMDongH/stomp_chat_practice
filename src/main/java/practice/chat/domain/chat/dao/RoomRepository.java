package practice.chat.domain.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.chat.domain.chat.domain.Room;
import practice.chat.domain.user.domain.Member;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
