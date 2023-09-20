package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource
public interface MessageRepository extends JpaRepository<Message, Long> {

    // available at: http://localhost:8080/api/messages/search/findByUserEmail?userEmail={userEmail}
    // return list of all messages (Message objects) sent by a user with a particular userEmail
    Page<Message> findByUserEmail(@RequestParam("user_email") String userEmail, Pageable pageable);
}
