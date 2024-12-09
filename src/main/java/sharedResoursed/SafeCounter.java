package sharedResoursed;

import java.util.concurrent.atomic.AtomicInteger;

public class SafeCounter extends Counter{
    private AtomicInteger count = new AtomicInteger(0);

    public int getCount(){
        return count.intValue();
    }

    public int increase(){
        return count.incrementAndGet();
    }
}
