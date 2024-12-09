package Ex;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ex14Bakery {
    public static final int NUMBER_OF_CLIENT_THREADS = 4;
    public static final int NUMBER_OF_CLIENTS = 100;

    public static void main(String[] args) {
        TakeANumber takeANumber = new TakeANumber();
        try(ExecutorService customerService = Executors.newFixedThreadPool(NUMBER_OF_CLIENT_THREADS);
            ExecutorService clerkService = Executors.newSingleThreadExecutor()){

            System.out.println("Starting clerk and customer threads (simulation begins)");

            clerkService.execute(new Clerk(takeANumber));

            for (int i = 0; i < NUMBER_OF_CLIENT_THREADS; i++) {
                customerService.execute(new Customer(takeANumber));
            }
        }
    }

    static class Clerk  implements Runnable {
        private final TakeANumber takeANumber;
        public Clerk(TakeANumber takeANumber){
            this.takeANumber = takeANumber;
        }

        @Override
        public void run() {
            for (int i = 0; i < NUMBER_OF_CLIENTS; i++) {
                takeANumber.serverBread();
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    System.err.println("Error en sleep()");
                }
            }
        }
    }

    static class Customer implements Runnable {
        private final TakeANumber takeANumber;
        public Customer(TakeANumber takeANumber){
            this.takeANumber = takeANumber;
        }

        @Override
        public void run() {
            for (int i = 0; i < NUMBER_OF_CLIENTS/NUMBER_OF_CLIENT_THREADS; i++) {
                takeANumber.takeNumber(Thread.currentThread().getName());
                try {
                    Thread.sleep(new Random().nextInt(2000) + 2000);
                } catch (InterruptedException e) {
                    System.err.println("Error en sleep()");
                }
            }
        }
    }

    static class TakeANumber {
        private int nextNumber = 1;
        private int orderNumber = 0;

        public TakeANumber (){}

        public synchronized void serverBread(){

            while(!(orderNumber>=nextNumber)){
                System.out.println("Clerk waiting (there are no clients to serve)");
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Error en serverBread(): " + e.getMessage());
                }
            }

            System.out.println("Clerk serving ticket " + (nextNumber-1));
            nextNumber++;
            notifyAll();
        }

        public synchronized void takeNumber(String name){

            int myNumber = orderNumber++;
            System.out.println("Customer " + name + " takes ticket " + myNumber);

            while(myNumber>nextNumber){
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Error en serverBread(): " + e.getMessage());
                }
            }

            notifyAll();
        }
    }
}
