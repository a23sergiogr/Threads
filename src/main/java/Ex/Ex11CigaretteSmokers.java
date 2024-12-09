package Ex;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Consider a tobacco shop with three smokers and a tobacconist.
 *
 * Each smoker smokes in an infinite loop.
 *
 * Each smoker must wait for certain conditions (having supplies to smoke) before smoking.
 *
 * The tobacconist produces supplies for smokers to smoke forever.
 *
 * To ensure true concurrency, it is important to note that the solution must allow several smokers to smoke simultaneously.
 *
 * The requirements for smokers and tobacconist are the following:
 *
 * Before smoking, it is necessary to roll a cigarette, for this the smoker needs three ingredients:
 * tobacco, paper and matches.
 * Each smoker has only tobacco, paper or matches.
 * The tobacconist randomly places two different ingredients out of the three needed to make a cigarette.  The smoker who has the third ingredient should remove the two items from the table, using them (along with their own supply) to make a cigarette, which they smoke for a while.
 * Once the smoker has made his cigarette, the tobacconist places two new random items.
 * The solution must avoid deadlocks between the different threads. It also must produce messages on the standard output to track the activity of the threads:
 *
 * The tobacconist must indicate when he produces ingredients and which supplies he produces.
 * Each smoker must indicate when he waits, which product(s) he waits for, and when he starts and stops smoking.
 */
public class Ex11CigaretteSmokers {
    public static final int NUMBER_OF_SMOKERS = 3;
    public static final int NUMBER_OF_TOBACCONIST = 1;

    public static void main(String[] args) {
        Table table = new Table();

        try(ExecutorService executorServiceSmokers = Executors.newFixedThreadPool(NUMBER_OF_SMOKERS);
            ExecutorService executorServiceTobacconist = Executors.newFixedThreadPool(NUMBER_OF_TOBACCONIST)){

            for (int i = 0; i < NUMBER_OF_TOBACCONIST; i++) {
                executorServiceTobacconist.execute(new Tobacconist(table));
            }

            for (int i = 0; i < NUMBER_OF_SMOKERS; i++) {
                executorServiceSmokers.execute(new Smoker(table, i));
            }
        }
    }

    static class Smoker implements Runnable {
        private final Table table;
        private final String name = Thread.currentThread().getName();
        private final int i;

        public Smoker(Table table, int i) {
            this.table = table;
            this.i = i;
        }

        @Override
        public void run() {
            while (true) {
                getIngredients();
                try {
                    Thread.sleep(new Random().nextInt(1000) + 1000);
                } catch (InterruptedException e) {
                    System.err.println("Error en sleep()");
                }
            }
        }

        public void getIngredients() {
            if (i == 0) {
                table.getIngredients(1, 1, 0, name);
            }
            if (i == 1) {
                table.getIngredients(1, 0, 1, name);
            }
            if (i == 2) {
                table.getIngredients(0, 1, 1, name);
            }
        }
    }

    static class Tobacconist implements Runnable {
        private final Table table;

        public Tobacconist(Table table) {
            this.table = table;
        }

        @Override
        public void run() {
            while (true) {
                int resourceNotProduced = new Random().nextInt(3);
                if (resourceNotProduced == 0) {
                    table.addIngredients(1, 1, 0, "Tobacconist");
                }
                if (resourceNotProduced == 1) {
                    table.addIngredients(1, 0, 1, "Tobacconist");
                }
                if (resourceNotProduced == 2) {
                    table.addIngredients(0, 1, 1, "Tobacconist");
                }
                try {
                    Thread.sleep(new Random().nextInt(500) + 500);
                } catch (InterruptedException e) {
                    System.err.println("Error en sleep()");
                }
            }
        }
    }

    static class Table {
        private int tobacco = 0;
        private int paper = 0;
        private int matches = 0;

        public Table() {}

        public synchronized void getIngredients(int tobaccoNeeded, int paperNeeded, int matchesNeeded, String smokerName) {
            while (tobacco < tobaccoNeeded || paper < paperNeeded || matches < matchesNeeded) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Error en wait()");
                }
            }

            tobacco -= tobaccoNeeded;
            paper -= paperNeeded;
            matches -= matchesNeeded;

            printStatus(smokerName + ": I have enough ingredients and now I am smoking");
        }

        public synchronized void addIngredients(int tobaccoProduced, int paperProduced, int matchesProduced, String producerName) {
            tobacco += tobaccoProduced;
            paper += paperProduced;
            matches += matchesProduced;

            notifyAll();

            printStatus(producerName + ": Ingredients added!");
        }

        private void printStatus(String actionMessage) {
            System.out.println("---------------------------------------------------------------");
            System.out.println(actionMessage);
            System.out.println("Current Table Status -> Tobacco: " + tobacco + ", Paper: " + paper + ", Matches: " + matches);
            System.out.println("---------------------------------------------------------------");
        }
    }

}
