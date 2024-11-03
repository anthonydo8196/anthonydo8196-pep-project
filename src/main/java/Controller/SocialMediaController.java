package Controller;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController() {
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }

    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.get("/ex", this::exampleHandler);
        
        // POST register
        app.post("/register", this::postRegisterHandler);

        // POST login
        app.post("/login", this::postLoginHandler);

        // POST messages
        app.post("/messages", this::postMessagesHandler);

        // GET localhost:8080/messages
        app.get("/messages", this::getAllMessagesHandler);

        // GET messages/{message_id}
        app.get("/messages/{message_id}", this::getMessageHandler);
        
        // DELETE messages/{message_id}
        app.delete("/messages/{message_id}", this::deleteMessageHandler);
        
        // PATCH messages/{message_id}
        app.patch("/messages/{message_id}", this::patchMessageHandler);

        // GET accounts/{account_id}/messages
        app.get("accounts/{account_id}/messages", this::getMessagesUserHandler);

        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context ctx) {
        ctx.json("example handler!!\n");
    }

    // Handler to register a new account
    private void postRegisterHandler(Context ctx) throws JsonProcessingException {
        // Object mapper converts between Java objects and JSON
        ObjectMapper mapper = new ObjectMapper();
        // readValue method converts this JSON string into an account object
        Account acc = mapper.readValue(ctx.body(), Account.class);
        Account addedAcc = accountService.addAccount(acc);
        if (addedAcc != null) {
            ctx.json(mapper.writeValueAsString(addedAcc));
        } else { 
            ctx.status(400);
        }   
    }

    private void postLoginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account acc = mapper.readValue(ctx.body(), Account.class);
        Account loggedAcc = accountService.logAccount(acc);
        if (loggedAcc != null) {
            ctx.json(mapper.writeValueAsString(loggedAcc));
        } else { 
            ctx.status(401);
        } 
    }

    private void postMessagesHandler(Context ctx) throws JsonProcessingException {
        // Object mapper converts between Java objects and JSON
        ObjectMapper mapper = new ObjectMapper();
        // readValue method converts this JSON string into an account object
        Message msg = mapper.readValue(ctx.body(), Message.class);
        Message addedMsg = messageService.addMessage(msg);
        if (addedMsg != null) {
            ctx.json(mapper.writeValueAsString(addedMsg));
        } else { 
            ctx.status(400);
        } 
    }

    private void getAllMessagesHandler(Context ctx) {
        List<Message> msgs = messageService.getAllMessages();
        // Coverts the message list into JSON and sends this JSON data as HTTP response to client that made the request
        ctx.json(msgs);
    }

    private void getMessageHandler(Context ctx) throws JsonProcessingException {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message msg = messageService.getMessage(messageId);
        if(msg != null) {
            ctx.json(msg);
        } else {
            ctx.status(200);
        }

    }

    private void deleteMessageHandler(Context ctx) throws JsonProcessingException {
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message deletedMsg = messageService.deleteMessage(messageId);
        if (deletedMsg != null) {
            ctx.json(deletedMsg);
        } else { 
            ctx.status(200);
        } 
    }

    private void patchMessageHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message msg = mapper.readValue(ctx.body(), Message.class);
        int messageId = Integer.parseInt(ctx.pathParam("message_id"));
        Message patchedMsg = messageService.patchMessage(messageId, msg);
        if(patchedMsg != null) {
            ctx.json(patchedMsg);
        } else {
            ctx.status(400);
        }
    }

    private void getMessagesUserHandler(Context ctx) {
        // accounts/{account_id}/messages
        int accountId = Integer.parseInt(ctx.pathParam("account_id"));
        List<Message> msgs = messageService.getAllMessagesUser(accountId);
        if (msgs != null) {
            ctx.json(msgs);
        } else { 
            ctx.status(200);
        } 
    }
}