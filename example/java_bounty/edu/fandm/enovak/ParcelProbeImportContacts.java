package edu.fandm.enovak;


import java.util.Scanner;
import java.io.IOError;
import java.io.IOException;


/* This is a custom Telegram client
 * This class is used to experiment and problem the ImportContacts functionality of the API
 */
public class ParcelProbeImportContacts {

    private static final String myPhoneNumber = "+18473088133";
    private static final int API_ID = 27834364; /* YOUR_API_ID */;
    private static final String API_HASH = "8e15eadf93ae7b548e3c17b01d5b3c76"; /* YOUR_API_HASH */;

    private static boolean currentlyAuthenticated = false;
    private static String number; // e.g., +12225550123
    private static boolean done = false;


    private static Client c;

    /* This is the main method (javadoc comments force me to write something here.) */
    public static void main(String[] args) throws InterruptedException {

        // print all args
        for(int i = 0; i < args.length; i++){
            System.out.println("arg[" + i + "]:" + args[i]);
        }

        number = args[0];


        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
        } catch (Client.ExecutionException error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        c = Client.create(new UpdateHandler(), new PrintOnlyHandler(), new PrintOnlyHandler());
        System.out.println("Native client ID of c: " + c.getNativeClientId());
        System.out.println("Num clients: " + c.getClientCount());


        while(currentlyAuthenticated != true){
            try{
                Thread.sleep(1000); // sleep for 1 second
            } catch (InterruptedException ie) {};
        }


        // Most parameters we don't know, so fill them with placeholder values
        TdApi.Contact recipient = new TdApi.Contact(number, "First Name", "Last Name", "", 0);
        TdApi.ImportContacts request = new TdApi.ImportContacts(new TdApi.Contact[]{recipient});
        System.out.println("Sending request: " + request.toString());
        c.send(request, new ImportResultHandler());


        while(done != true){
            try{
                Thread.sleep(1000); // sleep for 1 second
            } catch (InterruptedException ie) {};
        }

        System.out.println("main() ending");
    }

    private static class UpdateHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            //System.out.println("UpdateHandler.onResult() called");
            //System.out.println("obj:" + object.toString());

            if(object.getConstructor() == TdApi.UpdateAuthorizationState.CONSTRUCTOR){
                doAuthorizationSequence(object);
            } else if(object.getConstructor() == TdApi.UpdateUser.CONSTRUCTOR){
                System.out.println("UpdateHandler.onResult() called for UpdateUser");
                System.out.println("obj: " + object.toString());
                TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
                //System.out.println("UpdateUser: " + updateUser.user.toString());
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
                currentlyAuthenticated = false;
                System.out.println("Authorization State Closed");
                System.exit(0);
            }
        }
    }

    private static class ImportResultHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object){
            System.out.println("ImportResultHandler.onResult() called");
            System.out.println("obj:" + object.toString());
            System.out.println("obj.getConstructor():" + object.getConstructor());

            if(object.getConstructor() == TdApi.ImportedContacts.CONSTRUCTOR){
                TdApi.ImportedContacts importedContacts = (TdApi.ImportedContacts)object;
                System.out.println("Found contacts for phone number " + number.toString());
                for(int i = 0; i < importedContacts.userIds.length; i++){
                    System.out.println("\tID:" + importedContacts.userIds[i]);
                }

                if(importedContacts.userIds.length == 0){
                    System.out.println("Could not find Telegram user for number: " + number.toString());
                    System.out.println(object);
                }

                done = true;
            }
        }
    }
}
