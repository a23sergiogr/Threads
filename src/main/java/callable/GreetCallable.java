package callable;

import java.util.concurrent.Callable;

public class GreetCallable implements Callable {
    @Override
    public Object call() throws Exception {
        return "a";
    }
}
