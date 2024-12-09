package sharedResoursed;

public class Counter  {
    private int count = 0;

    public int getCount() {
        return count;
    }

    public Counter setCount(int count) {
        this.count = count;
        return this;
    }

    public int increase(){
        count++;
        return count;
    }
}
