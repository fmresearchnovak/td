package edu.fandm.enovak;



public class PrintOnlyHandler implements Client.ResultHandler,Client.ExceptionHandler {
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