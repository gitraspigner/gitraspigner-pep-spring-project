package com.example.service;

import com.example.entity.Message;
import com.example.repository.MessageRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    public Optional<Message> getMessageByID(int message_id) {
        return messageRepository.findById(message_id);
    }

    public int deleteMessageByID(int message_id) {
        return messageRepository.deleteById(message_id);
    }

    public List<Message> getMessagesPostedBy(int posted_by) {
        return messageRepository.findAllByPostedBy(posted_by);
    }

    public int updateMessage(int message_id, String message_text) {
        return messageRepository.updateMessageTextByMessageId(message_id, message_text);
    }
}
