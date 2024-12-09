package callable;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Callable greetCallable = new GreetCallable();
        FutureTask futureTask = new FutureTask(greetCallable);
        Thread thread = new Thread(futureTask);
        thread.start();
        String msg = (String) futureTask.get();
        System.out.println(msg);
    }
}
