package producer_consumer;

public class LaunchProducerConsumer {
    public static void main(String[] args) {
        SharedResource sharedResource = new SharedResource();

        Runnable producer = new Producer(sharedResource);
        Runnable consumer = new Consumer(sharedResource);

        Thread threadProducer = new Thread(producer);
        Thread threadConsumer = new Thread(consumer);

        threadProducer.start();
        threadConsumer.start();

        try{
            threadConsumer.join();
            threadProducer.join();
        } catch (InterruptedException e) {
            System.err.println("Error en join: " + e.getMessage());
        }

    }
}

class Producer implements Runnable{
    private final SharedResource sharedResource;

    public Producer(SharedResource sharedResource) {
        this.sharedResource = sharedResource;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            sharedResource.put(i);
        }
    }
}

class Consumer implements Runnable{
    private final SharedResource sharedResource;

    public Consumer(SharedResource sharedResource) {
        this.sharedResource = sharedResource;
    }

    @Override
    public void run() {
        for (int i = 0; i < 20; i++) {
            System.out.println(sharedResource.get());
        }
    }
}

class SharedResource{
    private int resource;
    private boolean avaliable = false;

    public synchronized void put(int resource){
        while (avaliable) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Error en wait(): " + e.getMessage());
            }
        }

        this.resource = resource;
        avaliable = true;
        notifyAll();
    }

    public synchronized int get() {
        if (!avaliable){
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println("Error en wait(): " + e.getMessage());
            }
        }

        avaliable = false;
        notifyAll();
        return resource;
    }
}
