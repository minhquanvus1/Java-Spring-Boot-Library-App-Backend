package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.MessageRepository;
import com.luv2code.springbootlibrary.entity.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MessagesService {

    private MessageRepository messageRepository;

    // constructor dependency injection
    public MessagesService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // function to allow user to post a message
    // here, we want to let user post a message --> we need to pass in this function: the actual Message object, the userEmail
    public void postMessage(Message messageRequest, String userEmail) {
        // because we only need the: message's title, and the message's question --> so, we can extract these infor from the passed in messageRequest
Message message = new Message(messageRequest.getTitle(), messageRequest.getQuestion());
message.setUserEmail(userEmail); // set the message's userEmail to be the passed in userEmail
messageRepository.save(message); // save this Message object into the message table in database
    }
}
