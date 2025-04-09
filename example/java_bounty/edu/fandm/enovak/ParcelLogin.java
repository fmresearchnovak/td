package edu.fandm.enovak;


import java.util.Scanner;
import java.io.IOError;
import java.io.IOException;


/* My Main Class, this is a custom Telegram client. */
public class ParcelLogin {

    private static final String phoneNumber = "+18473088133";

    private static final int API_ID = 27834364; /* YOUR_API_ID */;
    private static final String API_HASH = "8e15eadf93ae7b548e3c17b01d5b3c76"; /* YOUR_API_HASH */;

    private static boolean currentlyAuthenticated = false;

    private static Client c;

    /* This is the main method (javadoc comments force me to write something here.) */
    public static void main(String[] args) throws InterruptedException {

        try {
            Client.execute(new TdApi.SetLogVerbosityLevel(0));
        } catch (Client.ExecutionException error) {
            throw new IOError(new IOException("Write access to the current directory is required"));
        }

        c = Client.create(new UpdateHandler(), new printOnlyRequestHandler(), new printOnlyRequestHandler());
        System.out.println("Native client ID of c: " + c.getNativeClientId());
        System.out.println("Num clients: " + c.getClientCount());

        try{
            Thread.sleep(100000);
        } catch (InterruptedException ie) {};

    }

    private static class UpdateHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            System.out.println("UpdateHandler.onResult() called");
            System.out.println("obj:" + object.toString());

            if(object.getConstructor() == TdApi.UpdateAuthorizationState.CONSTRUCTOR){
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

                    System.out.println("request: " + request.toString());
                    System.out.println("sending...");
                    c.send(request, this);

                    //c.send(new TdApi.GetOption("usd_to_thousand_star_rate"), null, null);

                    //TdApi.OptionValueInteger tmp = new TdApi.OptionValueInteger(2L);
                    //c.send(new TdApi.SetOption("usd_to_thousand_star_rate", tmp), null, null);

                }

                if(authState.getConstructor() == TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR){
                    System.out.println("WHOA!");
                    System.out.println("sending phone number: " + phoneNumber.toString());
                    c.send(new TdApi.SetAuthenticationPhoneNumber(phoneNumber, null), this);
                }

                if(authState.getConstructor() == TdApi.AuthorizationStateWaitCode.CONSTRUCTOR){
                    Scanner s = new Scanner(System.in);
                    System.out.println("A code has been sent to +18473088133 phone (perhapes captured inside the Telegram app)");
                    System.out.print("Enter Code:");
                    String code = s.nextLine();
                    TdApi.CheckAuthenticationCode req = new TdApi.CheckAuthenticationCode(code);
                    c.send(req, this);
                }

                if(authState.getConstructor() == TdApi.AuthorizationStateReady.CONSTRUCTOR){
                    System.out.println("LOGGED IN!");
                    currentlyAuthenticated = true;

                    TdApi.InputMessageContent msg = new TdApi.InputMessageText(new TdApi.FormattedText("Hello from ParcelLogin!", null), null, true);
                    c.send(new TdApi.SendMessage(1L, 0, null, null, null, msg), this);
                }
            }
        }
    }

    /* 
    private static class AuthorizationRequestHandler implements Client.ResultHandler {
        @Override
        public void onResult(TdApi.Object object) {
            System.err.println("AuthorizationRequestHandlers received response from TDLib:" + object);
        }
    }
    */

    private static class printOnlyRequestHandler implements Client.ResultHandler,Client.ExceptionHandler {
        @Override
        public void onResult(TdApi.Object obj){
            System.out.println("Handling result by printing");
            System.out.println("obj:\n" + obj.toString());

        }

        @Override
        public void onException(Throwable t){
            System.out.println("Handling exception by printing");
            System.out.println("t:\n" + t.toString());
        }
    }
}
