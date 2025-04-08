package edu.fandm.enovak;

import edu.fandm.enovak.Client;

import java.util.Scanner;


/* My Main Class, this is a custom Telegram client. */
public class ParcelLogin {

    private static final int API_ID = 27834364; /* YOUR_API_ID */;
    private static final String API_HASH = "8e15eadf93ae7b548e3c17b01d5b3c76"; /* YOUR_API_HASH */;

    private static Client c;

    /* This is the main method (javadoc comments force me to write something here.) */
    public static void main(String[] args) throws InterruptedException {

        c = Client.create(new UpdateHandler(), new printOnlyRequestHandler(), new printOnlyRequestHandler());
        System.out.println("Native client ID of c: " + c.getNativeClientId());
        System.out.println("Num clients: " + c.getClientCount());

    }

    private static class UpdateHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            System.out.println("onResult() called");
            System.out.println("obj:" + object.toString());

            if(object.getConstructor() == TdApi.UpdateAuthorizationState.CONSTRUCTOR){
                TdApi.UpdateAuthorizationState authState = (TdApi.UpdateAuthorizationState) object;
                if(authState.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR){

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

                    System.out.println("request: " + request.toString());

                    System.out.println("sending...");
                    c.send(request, new printOnlyRequestHandler());


                    String phoneNumber = "+18473088133";
                    c.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), new printOnlyRequestHandler());

                }
            }

            if(object.getConstructor() == TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR){
                System.out.println("WHOA!");
                // pause for user input
                Scanner userInput = new Scanner(System.in);
                while(!userInput.hasNext());

            }
        }
    }

    private static class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            System.err.println("AuthorizationRequestHandlers received response from TDLib:" + object);
        }
    }

    private static class printOnlyRequestHandler implements Client.ResultHandler,Client.ExceptionHandler {
        @Override
        public void onResult(TdApi.Object obj){
            System.out.println("Handling result by printing and stopping");
            System.out.println("obj:\n" + obj.toString());

        }

        @Override
        public void onException(Throwable t){
            System.out.println("Handling exception by printing and stopping");
            System.out.println("t:\n" + t.toString());
        }
    }
}
