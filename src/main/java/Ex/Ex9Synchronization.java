package Ex;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Ex9Synchronization {
    public static final int MAX_RANDOM = 100;
    public static void main(String[] args) {
        HiddenNumber hiddenNumber = new HiddenNumberSynchronized(new Random().nextInt(MAX_RANDOM));
        Runnable runnable = new RunnableGuess(hiddenNumber, MAX_RANDOM);
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}

class RunnableGuess implements Runnable{
    private final HiddenNumber hiddenNumber;
    private final int max_random;

    public RunnableGuess(HiddenNumber hiddenNumber, int max_random){
        this.hiddenNumber = hiddenNumber;
        this.max_random = max_random;

    }

    @Override
    public void run() {
        boolean salir = false;
        while (!salir){
            Random random = new Random();
            int num = random.nextInt(max_random);
            int res = hiddenNumber.numberGuess(num);
            if (res != 0)
                salir = true;
            System.out.println("Thread: " + Thread.currentThread() + ", Numero: " + num + ", Resultado: " + res);
        }
    }
}

class HiddenNumberAtomic implements HiddenNumber{
    private final AtomicInteger number;
    public HiddenNumberAtomic(int number){
        this.number = new AtomicInteger(number);
    }

    public int numberGuess(int numberGuess){
        if (number.compareAndSet(numberGuess, -1))
            return 1;

        if (number.get() == -1)
            return -1;

        return 0;
    }
}

class HiddenNumberSynchronized implements HiddenNumber{
    private int number;
    private final Object lock = new Object();
    public HiddenNumberSynchronized(int number){
        this.number = number;
    }

    public int numberGuess(int numberGuess){

        if (number == -1)
            return number;

        synchronized (lock){
            if (number == numberGuess){
                number = -1;
                return 1;
            }
        }

        return 0;
    }
}

interface HiddenNumber{
    int numberGuess(int numberGuess);
}