package moe.caa.multilogin.flows.workflows;

import lombok.Getter;
import moe.caa.multilogin.flows.ProcessingFailedException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelFlows<C> extends BaseFlows<C> {
    @Getter
    private final List<BaseFlows<C>> steps;

    public ParallelFlows(List<BaseFlows<C>> steps) {
        this.steps = List.copyOf(steps);
    }

    @Override
    public Signal run(C context) {
        if (steps.isEmpty()) return Signal.PASSED;

        AtomicBoolean terminate = new AtomicBoolean(false);
        AtomicInteger remainingTasks = new AtomicInteger(steps.size());
        CountDownLatch latch = new CountDownLatch(1);

        for (BaseFlows<C> step : steps) {
            BaseFlows.getExecutorService().execute(() -> {
                try {
                    Signal signal = step.run(context);
                    if (signal == Signal.TERMINATED) {
                        terminate.set(true);
                    }
                } catch (Exception e) {
                    terminate.set(true);
                } finally {
                    if (remainingTasks.decrementAndGet() == 0) {
                        latch.countDown();
                    }
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new ProcessingFailedException(e);
        }
        return terminate.get() ? Signal.TERMINATED : Signal.PASSED;
    }
}
