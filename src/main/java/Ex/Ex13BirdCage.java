package Ex;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Ex13BirdCage {
    public static int NUMBER_OF_BIRDS = 10;
    public static int NUMBER_OF_PLATES = 3;
    public static int NUMBER_OF_SWINGS = 1;

    public static void main(String[] args) {
        Cage cage = new Cage();

        try(ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_BIRDS)){
            for (int i = 0; i < NUMBER_OF_BIRDS; i++) {
                executorService.execute(new Bird(cage));
            }
        }
    }


    static class Bird implements Runnable{
        Cage cage;

        public Bird(Cage cage){
            this.cage = cage;
        }

        @Override
        public void run() {
            while (true){
                try {
                    String name = Thread.currentThread().getName();

                    cage.enterPlateWaitingList(name);
                    Thread.sleep(new Random().nextInt(500) + 500);
                    cage.leavePlateWaitingList(name);

                    cage.enterSwingWaitingList(name);
                    Thread.sleep(new Random().nextInt(1500) + 500);
                    cage.leaveSwingWaitingList(name);

                } catch (InterruptedException e) {
                    System.err.println("Error en sleep");
                }
            }
        }
    }


    static class Cage{
        private int birdsEating = 0;
        private int birdsSwinging = 0;

        public Cage(){}


        public synchronized void enterPlateWaitingList(String name) throws InterruptedException {
            while (birdsEating == NUMBER_OF_PLATES){
                wait();
            }

            System.out.println("Yo " + name + " estoy comiendo");
            birdsEating++;
        }

        public synchronized void leavePlateWaitingList(String name){
            System.out.println("Yo " + name + " estoy lleno y quiero ir al columpio");
            birdsEating--;
            notifyAll();
        }


        public synchronized void enterSwingWaitingList(String name) throws InterruptedException {
            while (birdsSwinging == NUMBER_OF_SWINGS){
                wait();
            }

            System.out.println("Yo " + name + " estoy en el columpio");
            birdsSwinging++;
        }

        public synchronized void leaveSwingWaitingList(String name){
            System.out.println("Yo " + name + " estoy cansado y quiero comer");
            birdsSwinging--;
            notifyAll();
        }
    }
}


