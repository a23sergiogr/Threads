package Ex;

import java.util.concurrent.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


/**
 * We want to create a multithreaded application.
 * <p>
 * The main thread must create a thread pool to create using a single thread 50 numbers whose size must be between 20 and 50 digits. Numbers starting with 0 are not allowed.
 * <p>
 * Once all numbers have been generated, we want to verify if each of the numbers is a multiple of 3, 5 or 11.
 * <p>
 * Use another thread pool of size 12 to perform the calculation.
 * <p>
 * Use the following information to check if the numbers are multiples of 3, 5 or 11:
 * <p>
 * If the sum of digits in a number is a multiple of 3 then the number is a multiple of 3, e.g., for 612, the sum of digits is 9 so it’s a multiple of 3.
 * <p>
 * If the last character is ’5′ or ’0′ then the number is a multiple of 5, otherwise not.
 * <p>
 * A number is a multiple of 11 if the difference between the sum of its digits in odd positions and the sum of its digits in even positions is a multiple of 11 (including 0).
 * <p>
 * Display the information about each number and multiple on a separate line.
 * <p>
 * Once all numbers have been verified, the program must terminate.
 */
public class Ex6ThreadPoolMultiples {
    public static void main(String[] args) {

        List<String> numbers = null;

        try (ExecutorService executorService = Executors.newSingleThreadExecutor()) {

            Callable<List<String>> createNumbers = new CreateNumbers();
            Future<List<String>> future = executorService.submit(createNumbers);

            numbers = future.get();

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Problema con Pool");
        }

        try (ExecutorService executorService = Executors.newFixedThreadPool(12)) {

            for (String bigInteger : numbers) {
                Callable<String> verifyNumbers = new VerifyNumbers(bigInteger);
                Future<String> future = executorService.submit(verifyNumbers);

                System.out.println(future.get());
            }

        } catch (InterruptedException | ExecutionException e) {
            System.err.println("Problema con Pool");
        }
    }
}

class CreateNumbers implements Callable<List<String>> {

    @Override
    public List<String> call() throws Exception {
        return generateRandomNumbers(50, 20, 50);
    }

    public static List<String> generateRandomNumbers(int count, int minDigits, int maxDigits) {
        List<String> numbers = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < count; i++) {
            int numDigits = random.nextInt(maxDigits - minDigits + 1) + minDigits;
            String number = generateRandomBigNumber(numDigits, random);
            numbers.add(number);
        }

        return numbers;
    }

    private static String generateRandomBigNumber(int numDigits, SecureRandom random) {
        StringBuilder sb = new StringBuilder();

        sb.append(random.nextInt(9) + 1);

        for (int i = 1; i < numDigits; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }
}

class VerifyNumbers implements Callable<String>{
    String bigInteger;
    public VerifyNumbers(String bigInteger) {
        this.bigInteger = bigInteger;
    }

    @Override
    public String call() throws Exception {
        StringBuilder result = new StringBuilder("Number: " + bigInteger + " is ");
        boolean isDivisibleBy3 = verify3();
        boolean isDivisibleBy5 = verify5();
        boolean isDivisibleBy11 = verify11();

        if (isDivisibleBy3 || isDivisibleBy5 || isDivisibleBy11) {
            result.append("divisible by ");
            if (isDivisibleBy3) result.append("3, ");
            if (isDivisibleBy5) result.append("5, ");
            if (isDivisibleBy11) result.append("11, ");
            result.setLength(result.length() - 2);
        } else
            result.append("not divisible.");


        return result.toString();
    }

    public boolean verify3(){
        int n = 0;
        for (int i = 0; i < bigInteger.length(); i++) {
            n = n + Integer.parseInt(String.valueOf(bigInteger.charAt(i)));
        }
        return n%3==0;
    }

    public boolean verify5(){
        return (bigInteger.charAt(bigInteger.length()-1) == '0') || (bigInteger.charAt(bigInteger.length()-1) == '5');
    }

    public boolean verify11(){
        int sumaPosicionesImpares = 0;
        int sumaPosicionesPares = 0;

        for (int i = 0; i < bigInteger.length(); i++) {
            int digito = Character.getNumericValue(bigInteger.charAt(i));
            if (i % 2 == 0)
                sumaPosicionesImpares += digito;
             else
                sumaPosicionesPares += digito;
        }

        int diferencia = Math.abs(sumaPosicionesImpares - sumaPosicionesPares);
        return diferencia % 11 == 0;
    }
}
