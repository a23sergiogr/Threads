package sharedResoursed;

import java.util.ArrayList;
import java.util.List;

public class LaunchThreads {
    public static final int NUM_THREADS = 10;
    public static void main(String[] args) {
        Counter counter = new SafeCounter();
        List<Thread> listThread = new ArrayList<Thread>();
        for (int i = 0; i < NUM_THREADS; i++) {
            var counterRunnable = new CounterRunnable(counter);
            Thread th = new Thread(counterRunnable);
            th.start();
            listThread.add(th);
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            try {
                listThread.get(i).join();
            } catch (InterruptedException e) {
                System.err.println("Fallo join");
            }
        }
        System.out.println("The final value of Counter is " + counter.getCount());
    }
}
