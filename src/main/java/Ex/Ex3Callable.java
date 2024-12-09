package Ex;

import callable.GreetCallable;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * We want to create a multithreaded application.
 *
 * The main thread must create an array of ten words.
 * That main thread must create ten threads whose job is to check whether a word is a palindrome or not.
 * A palindrome is a word, phrase, verse or even number that reads the same forwards or backwards.
 */
public class Ex3Callable {
    public static void main(String[] args) {
        HashSet<String> words = new HashSet<>(List.of("kayak",
                "defied",
                "rotator",
                "repaper",
                "deed",
                "peep",
                "wow",
                "noon",
                "civic",
                "racecar",
                "level",
                "mom"));

        HashSet<FutureTask<String>> tasks = new HashSet<>();

        for (String word: words){
            Callable<String> palindrome = new Palindrome(word);
            FutureTask<String> futureTask = new FutureTask<>(palindrome);
            tasks.add(futureTask);
            Thread thread = new Thread(futureTask);
            thread.start();
        }

        for (FutureTask<String> futureTask: tasks){
            try {
                System.out.println(futureTask.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

class Palindrome implements Callable<String>{
    private final String word;
    public Palindrome(String word){
        this.word = word;
    }

    @Override
    public String call() throws Exception {
        int left = 0;
        int right = word.length() - 1;

        while (left < right) {
            if (word.charAt(left) != word.charAt(right))
                return word + ": false";
            left++;
            right--;
        }
        return word + ": true";
    }
}
