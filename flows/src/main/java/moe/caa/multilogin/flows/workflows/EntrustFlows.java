package moe.caa.multilogin.flows.workflows;

import lombok.Getter;
import moe.caa.multilogin.flows.ProcessingFailedException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class EntrustFlows<C> extends BaseFlows<C> {
    @Getter
    private final List<BaseFlows<C>> steps;

    public EntrustFlows(List<BaseFlows<C>> steps) {
        this.steps = List.copyOf(steps);
    }

    @Override
    public Signal run(C context) {
        if (steps.isEmpty()) return Signal.TERMINATED;

        AtomicBoolean anyPassed = new AtomicBoolean(false);

        CompletableFuture<?>[] futures = steps.stream()
                .map(step -> CompletableFuture.runAsync(() -> {
                    if (anyPassed.get()) return;
                    try {
                        Signal signal = step.run(context);
                        if (signal == Signal.PASSED) {
                            anyPassed.set(true);
                        }
                    } catch (Exception ignored) {
                    }
                }, BaseFlows.getExecutorService()))
                .toArray(CompletableFuture[]::new);

        try {
            CompletableFuture.allOf(futures).join();
        } catch (Exception e) {
            throw new ProcessingFailedException(e.getCause());
        }

        return anyPassed.get() ? Signal.PASSED : Signal.TERMINATED;
    }
}
