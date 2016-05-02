package bugreport;

import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Performance test the changes proposed https://github.com/apache/groovy/pull/325.
 * Assumption is that the size of the script parsed or the complexity of the
 * logic does not impact the performance.  What we are testing is the call
 * to ClassInfo#removeClassInfo and it's impact on the GroovyShell#evaluate
 * method runtime.
 */
public class Groovy7646Perf {

    private static final Integer FORTY_TWO = Integer.valueOf(42);
    private static final GroovyCodeSource gcs = new GroovyCodeSource("40 + 2", "Script1.groovy", GroovyShell.DEFAULT_CODE_BASE);

    private void runEvaluate() {
        GroovyShell shell = new GroovyShell();
        Integer result = (Integer) shell.evaluate(gcs);
        if (result != FORTY_TWO) {
            throw new RuntimeException();
        }
    }

    @Benchmark
    @Warmup(iterations = 5)
    @Measurement(iterations = 5)
    @BenchmarkMode({Mode.Throughput, Mode.AverageTime, Mode.SingleShotTime})
    @GroupThreads(2)
    public void shellEvaluate() {
        runEvaluate();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(Groovy7646Perf.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}