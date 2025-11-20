package com.apushkin.ai.localaicorechat.repository;

import com.apushkin.ai.localaicorechat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

}
