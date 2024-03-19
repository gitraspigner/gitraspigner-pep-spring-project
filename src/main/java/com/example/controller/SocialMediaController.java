package com.example.controller;

import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; //@RestController import

//import com.azul.crs.client.Response;
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.repository.AccountRepository;
import com.example.service.AccountService;
import com.example.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

/**
 * DONE: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

@Controller
public class SocialMediaController {

    @Autowired
    AccountService accountService;

    @Autowired
    MessageService messageService;

    /*
     * Register/post a new user account
     * 
     * As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register. 
     * The body will contain a representation of a JSON Account, but will not contain an account_id.
     * 
     * The registration will be successful if and only if the username is not blank, the password is 
     * at least 4 characters long, and an Account with that username does not already exist. 
     * If all these conditions are met, the response body should contain a JSON of the Account, including its account_id. 
     * The response status should be 200 OK, which is the default. The new account should be persisted to the database.
     * If the registration is not successful due to a duplicate username, the response status should be 409. (Conflict)
     * If the registration is not successful for some other reason, the response status should be 400. (Client error)
     * 
     */
    @PostMapping("/register")
    public  @ResponseBody ResponseEntity<Account> postUserHandler(@RequestBody Account account) {
        Optional<Account> accountExistsWithUsername = accountService.accountExistsByUsername(account.getUsername());
        if (accountExistsWithUsername.isPresent()) {
            // if found account with username already
            return ResponseEntity.status(409).body(new Account()); // return empty account in body and 409 status
        } else if (!account.getUsername().equals("") && account.getPassword().length() >= 4) { 
            //if login requirements pass ok
            Account newAccount = accountService.saveAccount(account); // try adding the account
            if (newAccount != null) {
                //if successful add
                return ResponseEntity.status(200).body(newAccount); // return new account added in body and 200 status
            } else {
                //if unsuccessful add
                return ResponseEntity.status(400).body(new Account()); // return empty account in body and 400 status
            }
        } else {
            // login requirements did not pass
            return ResponseEntity.status(400).body(new Account()); // return empty account in body and 400 status
        }
    }

    /*
     * Login to existing user account
     * 
     * As a user, I should be able to verify my login on the endpoint POST localhost:8080/login. 
     * The request body will contain a JSON representation of an Account.
     * 
     * The login will be successful if and only if the username and password provided in the request 
     * body JSON match a real account existing on the database. If successful, the response body should 
     * contain a JSON of the account in the response body, including its account_id. The response status 
     * should be 200 OK, which is the default.
     * 
     * If the login is not successful, the response status should be 401. (Unauthorized)
     * 
     */
    @PostMapping("/login")
    public  @ResponseBody ResponseEntity<Account> loginUserHandler(@RequestBody Account account) {
        Optional<Account> accountMatchingLogin = 
            accountService.accountExistsByUsernameAndPassword(account.getUsername(), account.getPassword());
        if (!accountMatchingLogin.isPresent()) {
            // if no account found matching username and password, no login
            return ResponseEntity.status(401).body(new Account()); // return empty account in body and 401 status
        } else {
            // if account found, login performed
            Account existingLoginAccount = accountMatchingLogin.get();
            return ResponseEntity.status(200).body(existingLoginAccount);// return the login account in body and 200 status
        }
    }

    /*
     * Post a new message
     * 
     * As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages. 
     * The request body will contain a JSON representation of a message, which should be persisted to 
     * the database, but will not contain a message_id.
     * 
     * The creation of the message will be successful if and only if the message_text is not blank, 
     * is not over 255 characters, and posted_by refers to a real, existing user. 
     * If successful, the response body should contain a JSON of the message, including its message_id. 
     * The response status should be 200, which is the default. The new message should be persisted to the database.
     * If the creation of the message is not successful, the response status should be 400. (Client error)
     * 
     */
    @PostMapping("/messages")
    public  @ResponseBody ResponseEntity<Message> postMessageHandler(@RequestBody Message message) {
        Optional<Account> accountMatchingPostedBy = accountService.accountExistsById(message.getPosted_by());
            
        if (!accountMatchingPostedBy.isPresent()) {
            // if no account found matching message posted_by (id)
            return ResponseEntity.status(400).body(new Message()); // return empty message in body and 400 status
        } else {
            // if account found
            if (!message.getMessage_text().equals("") && message.getMessage_text().length() <= 255) {
                // if message text requirements pass ok
                Message newMessage = messageService.saveMessage(message); //add message

                if (newMessage != null) { //if successful add
                    return ResponseEntity.status(200).body(newMessage); // return new message added in body and 200 status
                } else {
                    //if unsuccessful add
                    return ResponseEntity.status(400).body(new Message()); // return empty message in body and 400 status
                }
            } else {
                // message text requirements did not pass
                return ResponseEntity.status(400).body(new Message()); // return empty message in body and 400 status
            }  
        }
    }

    /*
     * Get all existing messages
     * 
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.
     * The response body should contain a JSON representation of a list containing all messages retrieved from the database. 
     * It is expected for the list to simply be empty if there are no messages. 
     * The response status should always be 200, which is the default.
     * 
     */
    @GetMapping("/messages")
    public  @ResponseBody ResponseEntity<List<Message>> getAllMessagesHandler() {
        return ResponseEntity.status(200).body(messageService.findAllMessages());    
    }

    /*
     * Get a message
     * 
     * As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.
     * The response body should contain a JSON representation of the message identified by the message_id. 
     * It is expected for the response body to simply be empty if there is no such message. 
     * The response status should always be 200, which is the default.
     * 
     */
    @GetMapping("/messages/{message_id}")
    public ResponseEntity<String> getMessageByIDHandler(@PathVariable int message_id) throws JsonProcessingException {
        Optional<Message> messageReturned = messageService.getMessageByID(message_id);    
        if (messageReturned.isPresent()) {
            //add successful
            Message message = messageReturned.get();
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonMessage = objectMapper.writeValueAsString(message);
            return ResponseEntity.status(200).body(jsonMessage); // return message found and 200 status
        } else {
            //add unsuccessful
            return ResponseEntity.status(200).body(""); 
            // return empty message in body and 200 status
        }
    }

    /*
     * Delete a message
     * 
     * As a User, I should be able to submit a DELETE request 
     * on the endpoint DELETE localhost:8080/messages/{message_id}.
     * The deletion of an existing message should remove an existing message from the database. 
     * 
     * If the message existed, the response body should contain the number of rows updated (1). 
     * The response status should be 200, which is the default.
     * If the message did not exist, the response status should be 200, but the response body should be empty. 
     * 
     * This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint 
     * should 
     * respond with the same type of response.
     * 
     */
    @DeleteMapping("/messages/{message_id}")
    public ResponseEntity<String> deleteMessageByIDHandler(@PathVariable int message_id) {
        int rowsDeleted = messageService.deleteMessageByID(message_id);    
        if (rowsDeleted >= 1) {
            //delete successful
            return ResponseEntity.status(200).body(rowsDeleted+""); // return message found in body and 200 status
        } else {
            //add unsuccessful
            return ResponseEntity.status(200).body(""); // return empty message in body and 200 status
        }
    }

    /*
     * Get all messages for a user account
     * 
     * As a user, I should be able to submit a GET request on the endpoint GET 
     * localhost:8080/accounts/{account_id}/messages.
     * 
     * The response body should contain a JSON representation of a list containing all messages posted 
     * by a particular user, which is retrieved from the database. 
     * It is expected for the list to simply be empty if there are no messages. 
     * The response status should always be 200, which is the default.
     * 
     */
    @GetMapping("/accounts/{account_id}/messages")
    public ResponseEntity<List<Message>> getAllMessagesForUserHandler(@PathVariable int account_id) {
        return ResponseEntity.status(200).body(messageService.getMessagesPostedBy(account_id));    
    }

    /*
     * Update an existing message
     * 
     * As a user, I should be able to submit a PATCH request on 
     * the endpoint PATCH localhost:8080/messages/{message_id}. 
     * The request body should contain a new message_text values to replace 
     * the message identified by message_id. 
     * The request body can not be guaranteed to contain any other information.
     * 
     * The update of a message should be successful if and only if the message id already exists 
     * and the new message_text is not blank and is not over 255 characters. 
     * 
     * If the update is successful, the response body should contain 
     * the number of rows updated (1), and the response status should be 200, which is the default. 
     * The message existing on the database should have the updated message_text.
     * 
     * If the update of the message is not successful for any reason, the response status should be 400. (Client error)
     * 
     */
    @PatchMapping("/messages/{message_id}")
    public ResponseEntity<Integer> updateMessageHandler(@PathVariable int message_id, @RequestBody String message_text) {
        Optional<Message> messageMatchingID = messageService.getMessageByID(message_id);
        
        if (!messageMatchingID.isPresent()) {
            // if no message found matching message_id
            return ResponseEntity.status(400).body(null); // return empty message in body and 400 status
        } else {
            // if message found
            if (message_text != null && !message_text.equals("{\"message_text\": \"\"}") && message_text.length() <= 255) {
                // if message text requirements pass ok

                int updatedRows = messageService.updateMessage(message_id, message_text); //update message
                
                if (updatedRows == 1) { //if successful update
                    // return # of rows updated in body and 200 status
                    return ResponseEntity.status(200).body(updatedRows);
                } else { //if unsuccessful update
                    // return empty message in body and 400 status
                    return ResponseEntity.status(400).body(null); 
                }
            } else {
                // message text requirements did not pass
                return ResponseEntity.status(400).body(null); // return empty message in body and 400 status
            }  
        }
    }
}
