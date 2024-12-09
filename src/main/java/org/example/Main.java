package org.example;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        InfoRunnable infoRunnable = new InfoRunnable();
        Thread thread = new Thread(infoRunnable);
        Thread thread2 = new Thread(infoRunnable);
        Thread thread3 = new Thread(() -> System.out.println("AAAAA: " + Thread.currentThread().getName()), "th3");
        PrintingThreads thread4 = new PrintingThreads();
        thread.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread.join();
    }
}

class InfoRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Hello my thread: " + Thread.currentThread().getName());
    }
}

class PrintingThreads extends Thread{
    @Override
    public void run() {
        System.out.println("Tengo extend: " + Thread.currentThread().getName());
    }
}