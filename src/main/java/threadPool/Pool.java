package threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Pool {
    public static void main(String[] args) {
        String[] names={"1","2","3"};
        ExecutorService pool = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 3; i++) {
            RunnableGreet rg = new RunnableGreet(names[i]);
            pool.execute(rg);
        }
        pool.shutdown();
    }
}

class RunnableGreet implements Runnable{
    String name;
    public RunnableGreet(String name){
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("name: " + name);
    }
}