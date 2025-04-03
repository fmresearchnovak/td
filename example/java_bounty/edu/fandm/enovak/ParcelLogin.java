package edu.fandm.enovak;


/* My Main Class, this is a custom Telegram client. */
public class ParcelLogin {

    private static final int API_ID = 27834364; /* YOUR_API_ID */;
    private static final String API_HASH = "8e15eadf93ae7b548e3c17b01d5b3c76"; /* YOUR_API_HASH */;

    /* This is the main method (javadoc comments force me to write something here.) */
    public static void main(String[] args) throws InterruptedException {

        Client c = new Client(null, null, null);
        System.out.println("Native client ID of c: " + c.getNativeClientId());
        System.out.println("Num clients: " + c.getClientCount());

        //Client.execute(null);

    }
}
    
    
    /*


        Client client = Client.create(new UpdatesHandler(), null, null);

        CountDownLatch latch = new CountDownLatch(1);

        client.send(new TdApi.SetLogVerbosityLevel(0), null);

        client.send(new TdApi.SetTdlibParameters(
                new TdApi.TdlibParameters(
                        false,
                        "/tmp/tdlib_example",
                        "/tmp/tdlib_example",
                        "/tmp/tdlib_example",
                        "/tmp/tdlib_example",
                        16777216,
                        1048576,
                        1048576,
                        false,
                        false,
                        false,
                        false,
                        false,
                        API_ID,
                        API_HASH,
                        "en",
                        "Ubuntu CLI",
                        "1.0",
                        "en"
                )
        ), (object) -> {
            if (object.getConstructor() == Error.CONSTRUCTOR) {
                System.err.println("Error setting parameters: " + ((Error) object).message_);
            }
        });

        latch.await();
        client.close();
    }

    private static class UpdatesHandler implements Client.ResultHandler {
        private static String phoneNumber;
        private static String authCode;
        private static String password;
        private static final CountDownLatch latch = new CountDownLatch(1);
        private static final CountDownLatch codeLatch = new CountDownLatch(1);
        private static final CountDownLatch passwordLatch = new CountDownLatch(1);
        private static final CountDownLatch encryptionLatch = new CountDownLatch(1);

        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    onAuthorizationStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState_);
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    latch.countDown();
                    codeLatch.countDown();
                    passwordLatch.countDown();
                    encryptionLatch.countDown();
                    break;
                case TdApi.Error.CONSTRUCTOR:
                    System.err.println("TDLib error: " + ((TdApi.Error) object).message_);
                    latch.countDown();
                    codeLatch.countDown();
                    passwordLatch.countDown();
                    encryptionLatch.countDown();
                    break;
                default:
                    System.out.println("Received: " + object);
            }
        }

        private static void onAuthorizationStateUpdated(AuthorizationState authorizationState) {
            Client client = Client.create(new UpdatesHandler(), null, null);
            switch (authorizationState.getConstructor()) {
                case AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                    System.out.print("Enter phone number: ");
                    Scanner scanner = new Scanner(System.in);
                    phoneNumber = scanner.nextLine();
                    client.send(new SetAuthenticationPhoneNumber(phoneNumber, null), null);
                    break;
                case AuthorizationStateWaitCode.CONSTRUCTOR:
                    System.out.print("Enter code: ");
                    Scanner scanner2 = new Scanner(System.in);
                    authCode = scanner2.nextLine();
                    client.send(new CheckAuthenticationCode(authCode), null);
                    break;
                case AuthorizationStateWaitPassword.CONSTRUCTOR:
                    System.out.print("Enter password: ");
                    Scanner scanner3 = new Scanner(System.in);
                    password = scanner3.nextLine();
                    client.send(new CheckAuthenticationPassword(password), null);
                    break;
                case AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                    client.send(new SetDatabaseEncryptionKey(new byte[0]), null);
                    break;
                case AuthorizationStateReady.CONSTRUCTOR:
                    System.out.println("Logged in!");
                    latch.countDown();
                    break;
                case AuthorizationStateClosed.CONSTRUCTOR:
                    System.out.println("Closed");
                    latch.countDown();
                    break;
                case AuthorizationStateLoggingOut.CONSTRUCTOR:
                    System.out.println("Logging out");
                    break;
                default:
                    System.out.println("Unsupported authorization state: " + authorizationState);
            }
        }
    }
        */
