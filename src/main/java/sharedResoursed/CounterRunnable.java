package sharedResoursed;

public
class CounterRunnable implements Runnable{
    private static final int NUM_OP = 500;
    private Counter counter;
    public CounterRunnable(Counter counter){
        this.counter = counter;
    }


    @Override
    public void run() {
        for (int i = 0; i < NUM_OP; i++) {
            System.out.println(Thread.currentThread().getName() + " " + counter.increase());
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
