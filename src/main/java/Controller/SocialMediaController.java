package Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import io.javalin.Javalin;
import io.javalin.http.Context;
import Service.AccountService;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;

    public SocialMediaController() {
        this.accountService = new AccountService();
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

        // POST messages

        // GET messages/{message_id}

        // DELETE messages/{message_id}

        // PATCH messages/{message_id}

        // GET accounts/{account_id}/messages
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
        ObjectMapper mapper = new ObjectMapper();
        Account acc = mapper.readValue(ctx.body(), Account.class);
        Account addedAcc = accountService.addAccount(acc);
        if (addedAcc != null) {
            ctx.json(mapper.writeValueAsString(addedAcc));
        } else { 
            ctx.status(400);
        }   
    }

}