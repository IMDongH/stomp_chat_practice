package practice.chat.domain.chat.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.chat.domain.chat.domain.Message;
import practice.chat.domain.chat.domain.Room;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
}
