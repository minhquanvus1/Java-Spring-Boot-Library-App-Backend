package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.MessageRepository;
import com.luv2code.springbootlibrary.entity.Message;
import com.luv2code.springbootlibrary.requestmodels.AdminQuestionRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    // create a message service that allows: admin user to put message (respond to messages that have not been responded yet (have: "closed" = 0/false))
    // here, we need to pass in: adminQuestionRequest (response of admin user), userEmail (admin's email)
    public void putMessage(AdminQuestionRequest adminQuestionRequest, String userEmail) throws Exception {
        // check if the message to be responded is actually in the message table in database or not (because admin can only respond to messages that have been asked)
        Optional<Message> message = messageRepository.findById(adminQuestionRequest.getId());
        if(!message.isPresent()) {
            throw new Exception("Message not found");
        }
        // if the message does actually exist, we need to PUT/update: admin's response, closed to true, and the response of admin
        message.get().setAdminEmail(userEmail);
        message.get().setClosed(true);
        message.get().setResponse(adminQuestionRequest.getResponse());

        // save this updated message to database
        messageRepository.save(message.get());
    }
}
