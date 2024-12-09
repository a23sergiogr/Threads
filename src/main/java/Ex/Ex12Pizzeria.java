package Ex;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * We want to simulate a home-delivery pizzeria, where we have two kinds of processes: cooks and deliverers.
 * Each cook prepares pizzas as orders come in. Once a pizza is ready, the cook places it on a tray for delivery when it is possible.
 * Each delivery person waits for a pizza to be on the tray, removes it and takes it to the corresponding customer.
 * The deliverer then returns to the pizzeria and waits for a new pizza on the tray.
 * Additionally, each tray has a limited capacity, no more than 5 pizzas can be stored on the tray.
 * Each pizza must have a different identifier (starting with 1) and a prize between 10 and 50€.
 * Use a linked list to model the tray. Each pizza must be delivered in the order in which it was cooked.
 * Stop the simulation when 100 pizzas have been cooked and delivered.
 * Suppose the cook takes between 500 and 1000ms to cook the pizza.
 * Suppose the delivery person takes between 1000 and 2000ms to deliver the pizza.
 * Show a message when the cook has placed a pizza on the tray.
 * Show a message when the delivery person has removed a pizza from the tray.
 * The solution must avoid deadlocks between the different threads.
 */
public class Ex12Pizzeria {
    public static final int PIZZA_TRAY_CAPACITY = 20;
    public static final int PIZZAS_TO_COOK = 10000;
    public static final int NUMBER_OF_COOKERS = 10;
    public static final int NUMBER_OF_DELIVERS = 20;
    public static int AUTOINCREMENT_ID_PIZZA = 1;

    public static void main(String[] args) {

        PizzaTray tray = new PizzaTray();

        try(ExecutorService CookService = Executors.newFixedThreadPool(NUMBER_OF_COOKERS);
            ExecutorService DeliverService = Executors.newFixedThreadPool(NUMBER_OF_DELIVERS)){

            for (int i = 0; i < NUMBER_OF_COOKERS; i++) {
                CookService.execute(new Cook(tray));
            }

            for (int i = 0; i < NUMBER_OF_DELIVERS; i++) {
                DeliverService.execute(new Deliverer(tray));
            }
        }
    }


    static class Deliverer implements Runnable {
        private final PizzaTray pizzaTray;
        public Deliverer(PizzaTray pizzaTray){
            this.pizzaTray = pizzaTray;
        }

        @Override
        public void run() {
            for (int i = 1; i < PIZZAS_TO_COOK/NUMBER_OF_DELIVERS+1; i++) {
                try {
                    Thread.sleep(new Random().nextInt(1000) + 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                pizzaTray.get();
            }
        }
    }

    static class Cook implements Runnable {
        private final PizzaTray pizzaTray;
        public Cook(PizzaTray pizzaTray){
            this.pizzaTray = pizzaTray;
        }

        @Override
        public void run() {
            for (int i = 1; i < PIZZAS_TO_COOK/NUMBER_OF_COOKERS+1; i++) {
                try {
                    Thread.sleep(new Random().nextInt(500) + 500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                pizzaTray.put(new Pizza());
            }
        }
    }

    static class PizzaTray {
        private final LinkedList<Pizza> pizzas;
        private boolean full = false;

        public PizzaTray() {
            pizzas = new LinkedList<>();
        }

        public synchronized void put(Pizza pizza) {
            while (full) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Error in wait() of put(): " + e.getMessage());
                }
            }

            pizzas.add(pizza);
            if (pizzas.size() == PIZZA_TRAY_CAPACITY) {
                full = true;
            }
            System.out.println(pizza + ": Added to Tray\n\tPizzas Remaining in Tray: " + pizzas.size());
            notifyAll();
        }

        public synchronized Pizza get() {
            while (pizzas.isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    System.err.println("Error in wait() of get(): " + e.getMessage());
                }
            }

            Pizza pizza = pizzas.remove();
            if (pizzas.size() < PIZZA_TRAY_CAPACITY) {
                full = false;
            }
            System.out.println( pizza + ": Sent to Customer" );
            notifyAll();
            return pizza;
        }
    }

    static class Pizza{
        private final int id;
        private final String price;

        public Pizza() {
            this.id = AUTOINCREMENT_ID_PIZZA;
            AUTOINCREMENT_ID_PIZZA++;
            this.price = new Random().nextInt(40) + 10 + "€";
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Pizza.class.getSimpleName() + "[", "]")
                    .add("id=" + id)
                    .add("price='" + price + "'")
                    .toString();
        }
    }

}

