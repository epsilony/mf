package net.epsilony.mf.integrate.integrator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import org.junit.Test;

public class MultiThreadIntegralRecorderTest {
    int threadNum = 3;
    int sampleNum = 100;
    boolean tested;

    @Test
    public void test() {
        tested = false;
        MultiThreadIntegralRecorder<Integer> collectioner = new MultiThreadIntegralRecorder<>(threadNum);

        Random random = new Random();
        Mock mock = new Mock();
        collectioner.register(mock, "inputList", new Class[] { List.class });
        List<Consumer<Integer>> integrators = collectioner.getIntegrators();
        for (int i = 0; i < sampleNum; i++) {
            Consumer<Integer> integrator = integrators.get(random.nextInt(threadNum));
            integrator.accept(i);
        }
        tested = false;
        collectioner.allThreadsFinished();
        assertTrue(tested);
    }

    public class Mock {
        public int inputList(List<Integer> input) {
            assertEquals(sampleNum, input.size());

            Collections.sort(input);
            int i = 0;
            for (int in : input) {
                assertEquals(i, in);
                i++;
            }
            tested = true;
            return input.size();
        }
    }
}
