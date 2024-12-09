package Ex;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Ex7SuperEvenNumbers {
    private static int BEGIN_NUMBER = 1;
    private static int END_NUMBER = 100;

    public static void main(String[] args) {
        try (ExecutorService executorService = Executors.newFixedThreadPool(4)) {

            for (int i = BEGIN_NUMBER; i < END_NUMBER; i++) {
                var verifyNumbers = new CallableCheckSuperEven(i);
                var future = executorService.submit(verifyNumbers);
                Integer res = future.get();
                if (res != null)
                    System.out.println(res);
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Problema con Pool");
        }
    }
}

class CallableCheckSuperEven implements Callable<Integer> {
    private Number number;
    public CallableCheckSuperEven(int number){
        this.number = new Number(number);
    }

    @Override
    public Integer call() throws Exception {
        try (ExecutorService executorService = Executors.newFixedThreadPool(2)) {

            char[] chars = number.getNumberChar();
            boolean[] booleans = new boolean[chars.length];
            int i = 0;
            for (char n : chars){
                var verifyNumbers = new CallableCheckEven(n);
                var future = executorService.submit(verifyNumbers);

                booleans[i] = future.get();
                i++;
            }
            for (boolean bool : booleans){
                if (!bool)
                    return null;
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Problema con Pool");
        }

        return number.getNumber();
    }
}

class CallableCheckEven implements Callable<Boolean> {
    public final int number;
    public CallableCheckEven(int number){
        this.number = number;
    }

    @Override
    public Boolean call() throws Exception {
        return number%2==0;
    }
}

class RunnableChechEven implements Runnable{
    public Number2 number;
    public RunnableChechEven(Number2 number){
        this.number = number;
    }

    @Override
    public void run() {

    }
}

class Number{
    private final AtomicInteger number = new AtomicInteger();
    private AtomicBoolean isEven;

    public Number(int number){
        this.number.set(number);
    }

    public int getNumber() {
        return number.get();
    }

    public char[] getNumberChar(){
        return (String.valueOf(number)).toCharArray();
    }
}


class SuperEvenNumberChecker {
    public static void main(String[] args) {
        // Generate two random numbers between 1 and 10000
        Random random = new Random();
        int num1 = random.nextInt(10000) + 1;
        int num2 = random.nextInt(10000) + 1;
        num1 = 0;
        num2 = 1000;

        int lowerBound = Math.min(num1, num2);
        int upperBound = Math.max(num1, num2);

        System.out.println("Checking numbers between: " + lowerBound + " and " + upperBound);

        try(ExecutorService mainThreadPool = Executors.newFixedThreadPool(4)){
            for (int number = lowerBound; number <= upperBound; number++) {
                int finalNumber = number;

                mainThreadPool.submit(() -> {
                    Number2NotAtomic sharedNumber = new Number2NotAtomic(finalNumber);
                    checkSuperEven(sharedNumber);

                    if (sharedNumber.isSuperEven())
                        System.out.println(finalNumber);
                });
            }
        }
    }

    private static void checkSuperEven(Number2NotAtomic sharedNumber) {
        try(ExecutorService digitThreadPool = Executors.newFixedThreadPool(2)){
            String numStr = String.valueOf(sharedNumber.getValue());

            for (char digitChar : numStr.toCharArray()) {
                int digit = Character.getNumericValue(digitChar);

                digitThreadPool.submit(() -> {
                    if (digit % 2 != 0) {
                        sharedNumber.setSuperEven(false);
                    }
                });
            }
        }
    }
}

class Number2 {
    private final int value;
    private final AtomicBoolean isSuperEven;

    public Number2(int value) {
        this.value = value;
        this.isSuperEven = new AtomicBoolean(true);
    }

    public int getValue() {
        return value;
    }

    public boolean isSuperEven() {
        return isSuperEven.get();
    }

    public void setSuperEven(boolean isSuperEven) {
        if (!isSuperEven) {
            this.isSuperEven.compareAndSet(true, false);
        }

    }
}

class Number2NotAtomic {
    private final int value;
    private boolean isSuperEven;

    public Number2NotAtomic(int value) {
        this.value = value;
        this.isSuperEven = true;
    }

    public int getValue() {
        return value;
    }

    public boolean isSuperEven() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return isSuperEven;
    }

    public void setSuperEven(boolean isSuperEven) {
        this.isSuperEven = isSuperEven;
    }
}