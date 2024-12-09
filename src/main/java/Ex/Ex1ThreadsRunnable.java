package Ex;

import java.util.Random;

/**
 * Create a Java class that implements the Runnable interface.
 *
 * The run() method of the class must do the following:
 *
 * Display a welcome message with the name of the current thread.
 * Repeat five times:
 * Get a random number between 10 and 500 (use java.util.Random).
 * Pause the execution of the current thread for the number of miliseconds equal to the random number obtained above.
 * Display a goodbye message with the name of the current thread.
 * Create a Java executable class to launch two threads created using the previous class.
 * Thie main thread waits for the other two threads to finish and then displays a message indicating that it has finished.
 */
public class Ex1ThreadsRunnable {
    public static void main(String[] args) throws InterruptedException {
        InfoRunnable infoRunnable = new InfoRunnable();
        Thread thread = new Thread(infoRunnable, "Th1");
        Thread thread2 = new Thread(infoRunnable, "Th2");
        System.out.println("START");
        thread.start();
        thread2.start();
        thread.join();
        thread2.join();
        System.out.println("\nEND");
    }
}

class InfoRunnable implements Runnable {
    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println("Bos días: " + name);
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int time = random.nextInt(490)+10;
            System.out.println("\n" + name + ": " + time);
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Adeus: " + name);
    }
}
