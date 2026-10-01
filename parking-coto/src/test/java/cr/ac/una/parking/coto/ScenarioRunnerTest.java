package cr.ac.una.parking.coto;

import cr.ac.una.parking.coto.scenario.ScenarioResult;
import cr.ac.una.parking.coto.scenario.ScenarioRunner;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScenarioRunnerTest {

    private final List<ScenarioResult> results = new ScenarioRunner().runAll();

    @Test
    void shouldIncludeTheFifteenRequiredCases() {
        long required = results.stream().filter(r -> "Obligatorio".equals(r.getCategory())).count();

        assertEquals(15, required);
        assertEquals(19, results.size());
    }

    @Test
    void shouldPassEveryScenario() {
        for (ScenarioResult result : results) {
            assertTrue(result.isPassed(), "Caso " + result.getNumber() + " (" + result.getName()
                    + "): esperado [" + result.getExpected() + "] pero fue [" + result.getObtained() + "]");
        }
    }

    @Test
    void shouldNumberScenariosInOrderFromOne() {
        for (int i = 0; i < results.size(); i++) {
            assertEquals(i + 1, results.get(i).getNumber());
        }
    }
}
