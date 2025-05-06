package edu.fandm.enovak;


import java.util.Scanner;
import java.io.IOError;
import java.io.IOException;


/* This is a custom Telegram client
 * This class Sends a given message to a given user (specified by their phone number)
 */
public class ParcelSendVCardMessage {

    private static final String myPhoneNumber = "+18473088133";
    private static final int API_ID = 27834364; /* YOUR_API_ID */;
    private static final String API_HASH = "8e15eadf93ae7b548e3c17b01d5b3c76"; /* YOUR_API_HASH */;

    private static volatile boolean currentlyAuthenticated = false;
    private static boolean msgSent = false;

    private static String recipientPhoneNumber; // e.g., +12225550123
    private static String vCard; // e.g., "BEGIN:VCARD\nVERSION:3.0\nFN:Alice Smith\nTEL;TYPE=cell:+1234567890\nEMAIL:

    private static Client c;

    /* This is the main method (javadoc comments force me to write something here.) */
    public static void main(String[] args) throws InterruptedException {

        // print all args
        for(int i = 0; i < args.length; i++){
            System.out.println("arg[" + i + "]:" + args[i]);
        }

        recipientPhoneNumber = args[0];
        String pathToVCard = args[1];

        // read the vCard file
        StringBuilder vCardBuilder = new StringBuilder();
        try (Scanner scanner = new Scanner(new java.io.File(pathToVCard))){
            while (scanner.hasNextLine()) {
                vCardBuilder.append(scanner.nextLine()).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Error reading vCard file: " + e.getMessage());
            return;
        }

        vCard = vCardBuilder.toString();
        System.out.println("Sending \"" + vCard + "\" to " + recipientPhoneNumber);


        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
        } catch (Client.ExecutionException error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        c = Client.create(new UpdateHandler(), new PrintOnlyHandler(), new PrintOnlyHandler());
        System.out.println("Native client ID: " + c.getNativeClientId());;


        while(currentlyAuthenticated != true){
            try{
                Thread.sleep(1000); // sleep for 1 second
            } catch (InterruptedException ie) {};
        }


        // Most parameters we don't know, so fill them with placeholder values
        TdApi.Contact recipient = new TdApi.Contact(recipientPhoneNumber, "First Name", "Last Name", "", 0);
        TdApi.ImportContacts request = new TdApi.ImportContacts(new TdApi.Contact[]{recipient});
        System.out.println("Sending request: " + request.toString());
        c.send(request, new ImportResultHandler());


        while(msgSent != true){
            try{
                Thread.sleep(1000); // sleep for 1 second
            } catch (InterruptedException ie) {};
        }

        System.out.println("main() ending");
    }

    private String parseVCardFields(String vCard){
        return "";
    }

    private static class UpdateHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            System.out.println("UpdateHandler.onResult() called");
            System.out.println("obj:" + object.toString());

            if(object.getConstructor() == TdApi.UpdateAuthorizationState.CONSTRUCTOR){
                doAuthorizationSequence(object);
            }

            if(object.getConstructor() == TdApi.Chats.CONSTRUCTOR){
                TdApi.Chats chats = (TdApi.Chats) object;

                System.out.println("Num Chat: " + chats.totalCount);
                for(int i = 0; i < chats.chatIds.length; i++){
                    System.out.println("Chat ID: " + chats.chatIds[i]);
                }

            }

        }


        private void doAuthorizationSequence(TdApi.Object object){
            TdApi.AuthorizationState authState = (((TdApi.UpdateAuthorizationState) object).authorizationState);
            if(authState.getConstructor() == TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR){

                System.out.println("Doing reply for TdlibParameters...");
                TdApi.SetTdlibParameters request = new TdApi.SetTdlibParameters();
                request.databaseDirectory = "parcel_database";
                request.useMessageDatabase = true;
                request.useSecretChats = true;
                request.apiId = API_ID;
                request.apiHash = API_HASH;
                request.systemLanguageCode = "en";
                request.deviceModel = "Desktop";
                request.applicationVersion = "1.0";

                System.out.println("send request: " + request.toString());
                c.send(request, this);

                //c.send(new TdApi.GetOption("usd_to_thousand_star_rate"), null, null);

                //TdApi.OptionValueInteger tmp = new TdApi.OptionValueInteger(2L);
                //c.send(new TdApi.SetOption("usd_to_thousand_star_rate", tmp), null, null);

            }

            if(authState.getConstructor() == TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR){
                System.out.println("WHOA!");
                System.out.println("sending phone number: " + myPhoneNumber.toString());
                c.send(new TdApi.SetAuthenticationPhoneNumber(myPhoneNumber, null), this);
            }

            if(authState.getConstructor() == TdApi.AuthorizationStateWaitCode.CONSTRUCTOR){
                Scanner s = new Scanner(System.in);
                System.out.println("A code has been sent to +18473088133 phone (perhapes captured inside the Telegram app)");
                System.out.print("Enter Code:");
                String code = s.nextLine();
                TdApi.CheckAuthenticationCode req = new TdApi.CheckAuthenticationCode(code);
                System.out.println("Sending request: " + req.toString());
                c.send(req, this);
            }

            if(authState.getConstructor() == TdApi.AuthorizationStateReady.CONSTRUCTOR){
                System.out.println("LOGGED IN!");
                currentlyAuthenticated = true;

                // This should send updates
                //c.send(new TdApi.LoadChats(new TdApi.ChatListMain(), Integer.MAX_VALUE), new ChatListHandler());


                // request for update to get all chat IDs
                // sends back a "Chats" object
                System.out.println("Sending request for chat list");
                c.send(new TdApi.GetChats(new TdApi.ChatListMain(), 100), this);
                
            }


            if(authState.getConstructor() == TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR){
                currentlyAuthenticated = false;
                System.out.println("Logging out");
            }
            if(authState.getConstructor() == TdApi.AuthorizationStateClosing.CONSTRUCTOR){
                currentlyAuthenticated = false;
                System.out.println("Closing");
            }
            if(authState.getConstructor() == TdApi.AuthorizationStateClosed.CONSTRUCTOR){
                System.out.println("Authorization State Closed");
                System.exit(0);
            }
        }
    }

    private static class ImportResultHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object){
            if(object.getConstructor() == TdApi.ImportedContacts.CONSTRUCTOR){
                TdApi.ImportedContacts importedContacts = (TdApi.ImportedContacts)object;
                System.out.println("Found contacts for phone number " + recipientPhoneNumber.toString());
                for(int i = 0; i < importedContacts.userIds.length; i++){
                    System.out.println("\tID:" + importedContacts.userIds[i]);
                }

                if(importedContacts.userIds.length == 1){
                    long id = importedContacts.userIds[0];
                    // passing "true" creates the chat without any network request / verification.
                    // so the whole chat (recipient name, number, pre-existing messages) may be invalid.
                    TdApi.CreatePrivateChat createChatRequest = new TdApi.CreatePrivateChat(id, false);
                    System.out.println("Sending request: " + createChatRequest.toString());
                    c.send(createChatRequest, new PrivateChatHandler());
                } else {
                    System.out.println("Failed to get user for number: " + recipientPhoneNumber);
                    System.out.println(object);
                }
            }
        }
    }
    
    private static class PrivateChatHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object){
            if(object.getConstructor() == TdApi.Chat.CONSTRUCTOR){
                System.out.println("Established private chat for " + recipientPhoneNumber);
                TdApi.Chat chat = (TdApi.Chat)object;

                TdApi.Contact randomVCardContact = new TdApi.Contact("+1234567890", "FAlice", "FSmith", vCard, 0);
                TdApi.InputMessageContact msg = new TdApi.InputMessageContact(randomVCardContact);
                TdApi.SendMessage sendMessageReq = new TdApi.SendMessage(chat.id, 0, null, null, null, msg);
                System.out.println("Sending request to send message: " + sendMessageReq.toString());
                c.send(sendMessageReq, this);
                System.out.println("Sent vCard to " + recipientPhoneNumber);
                msgSent = true;
            } else {
                System.out.println("Failed to establish chat for user");
                System.out.println(object);
            }
        }
    }

    private static class ChatListHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            System.out.println("ChatListHandler.onResult() called");
            System.out.println("obj:" + object.toString());

            if(object.getConstructor() == TdApi.Error.CONSTRUCTOR){
                TdApi.Error err = (TdApi.Error) object;
                System.out.println("Error: " + err.message);
                System.out.println("Error code: " + err.code);
                if(err.code == 404){
                    System.out.println("Full chat list recieved");

                    TdApi.InputMessageContent msg = new TdApi.InputMessageText(new TdApi.FormattedText("Hello from ParcelLogin!", null), null, true);
                    System.out.println("Sending request: " + msg.toString());
                    c.send(new TdApi.SendMessage(1L, 0, null, null, null, msg), this);
                }
            } else if(object.getConstructor() == TdApi.Ok.CONSTRUCTOR){
                System.out.println("Ok");
            } else{
                System.err.println("Receive wrong response from TDLib:" + object);
            }
        }
    }
}
