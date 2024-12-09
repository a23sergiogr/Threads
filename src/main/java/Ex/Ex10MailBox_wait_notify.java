package Ex;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ex10MailBox_wait_notify {
    public static final int THREAD_NUMBER = 4;
    public static void main(String[] args) {
        MailBox sharedResource = new MailBox();

        try(ExecutorService ConsumerService = Executors.newFixedThreadPool(THREAD_NUMBER);
            ExecutorService ProducerService = Executors.newFixedThreadPool(THREAD_NUMBER)){

            for (int i = 0; i < THREAD_NUMBER; i++) {
                ConsumerService.execute(new MailConsumer(sharedResource));
                ProducerService.execute(new MailProducer(sharedResource));
            }
        }
    }


    static class MailProducer implements Runnable{
        private final MailBox sharedResource;

        public MailProducer(MailBox sharedResource) {
            this.sharedResource = sharedResource;
        }

        @Override
        public void run() {
            while(true) {
                int sleepTime = new Random().nextInt(1000);
                sharedResource.put("number: " + sleepTime, Thread.currentThread().getName());
                //System.out.println(sleepTime + ", Thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.err.println("Error en run() de MailProducer: " + e.getMessage());
                }
            }
        }
    }

    static class MailConsumer implements Runnable{
        private final MailBox sharedResource;

        public MailConsumer(MailBox sharedResource) {
            this.sharedResource = sharedResource;
        }

        @Override
        public void run() {
            while(true){
                sharedResource.get(Thread.currentThread().getName());
                //System.out.println(sharedResource.get()  + ", Thread: " + Thread.currentThread().getName());
                try {
                    Thread.sleep(new Random().nextInt(1000));
                } catch (InterruptedException e) {
                    System.err.println("Error en run() de MailConsumer: " + e.getMessage());
                }

            }
        }
    }

    static class MailBox{
        private String resource;
        private boolean avaliable = false;

        public synchronized void put(String resource, String threadName){
            while (avaliable) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Error en wait(): " + e.getMessage());
                }
            }

            System.out.println("Set Resource: [" + resource + "], " + threadName);
            this.resource = resource;
            avaliable = true;
            notifyAll();
        }

        public synchronized String get(String threadName) {
            while  (!avaliable){
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Error en wait(): " + e.getMessage());
                }
            }

            avaliable = false;
            notifyAll();
            System.out.println("Get Resource: [" + resource + "], " + threadName);
            return resource;
        }
    }

}

