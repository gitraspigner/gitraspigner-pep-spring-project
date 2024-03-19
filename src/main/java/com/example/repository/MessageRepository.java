package com.example.repository;

import java.util.List;
//import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Integer>{
    @Modifying
    @Query("DELETE FROM Message m WHERE m.message_id = :messageId")
    int deleteById(int messageId);
    
    @Query("SELECT m FROM Message m WHERE m.posted_by = :postedBy")
    List<Message> findAllByPostedBy(@Param("postedBy") int postedBy);

    @Transactional
    @Modifying
    @Query("UPDATE Message m SET m.message_text = :messageText WHERE m.message_id = :messageId")
    int updateMessageTextByMessageId(@Param("messageId") Integer messageId, @Param("messageText") String messageText);
}
