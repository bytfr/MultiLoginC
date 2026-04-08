package moe.caa.multilogin.flows.workflows;

import lombok.Getter;
import moe.caa.multilogin.flows.ProcessingFailedException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 代表一个并行的委托流
 * 所有工序并行尝试加工这个零件，直到有一条工序能顺利完成。
 */
public class EntrustFlows<C> extends BaseFlows<C> {
    @Getter
    private final List<BaseFlows<C>> steps;

    public EntrustFlows(List<BaseFlows<C>> steps) {
        this.steps = List.copyOf(steps);
    }

    @Override
    public Signal run(C context) {
        if (steps.isEmpty()) return Signal.TERMINATED;

        AtomicReference<Signal> firstPassed = new AtomicReference<>();

        CompletableFuture<?>[] futures = steps.stream()
                .map(step -> CompletableFuture.runAsync(() -> {
                    Signal signal = step.run(context);
                    if (signal == Signal.PASSED) {
                        firstPassed.compareAndSet(null, Signal.PASSED);
                    }
                }, BaseFlows.getExecutorService()))
                .toArray(CompletableFuture[]::new);

        try {
            CompletableFuture.allOf(futures).join();
        } catch (Exception e) {
            throw new ProcessingFailedException(e.getCause());
        }

        return firstPassed.get() != null ? Signal.PASSED : Signal.TERMINATED;
    }
}
