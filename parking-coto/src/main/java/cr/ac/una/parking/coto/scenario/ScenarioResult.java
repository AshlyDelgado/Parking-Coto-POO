package cr.ac.una.parking.coto.scenario;

/**
 * Immutable outcome of running one demonstration scenario.
 *
 * <p>A scenario describes an input, the result the business rules require and
 * the result the system actually produced. The scenario passes when both
 * match.</p>
 *
 * @author Carolain Quesada and Ashly Delgado
 * @version 1.0
 */
public class ScenarioResult {

    /** Position of the scenario in the execution order. */
    private final int number;
    /** Origin of the scenario: required by the assignment or additional. */
    private final String category;
    /** Short name of the scenario. */
    private final String name;
    /** Description of the situation that is executed. */
    private final String input;
    /** Result that the business rules require. */
    private final String expected;
    /** Result actually produced by the system. */
    private final String obtained;
    /** Whether the obtained result matches the expected one. */
    private final boolean passed;

    /**
     * Creates the outcome of a scenario.
     *
     * @param number position of the scenario in the execution order
     * @param category origin of the scenario
     * @param name short name of the scenario
     * @param input description of the executed situation
     * @param expected result required by the business rules
     * @param obtained result produced by the system
     * @param passed whether both results match
     */
    public ScenarioResult(int number, String category, String name, String input,
                          String expected, String obtained, boolean passed) {
        this.number = number;
        this.category = category;
        this.name = name;
        this.input = input;
        this.expected = expected;
        this.obtained = obtained;
        this.passed = passed;
    }

    /**
     * Returns the position of the scenario.
     *
     * @return execution order starting at one
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns the origin of the scenario.
     *
     * @return category text
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the short name of the scenario.
     *
     * @return scenario name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description of the executed situation.
     *
     * @return input description
     */
    public String getInput() {
        return input;
    }

    /**
     * Returns the result required by the business rules.
     *
     * @return expected result text
     */
    public String getExpected() {
        return expected;
    }

    /**
     * Returns the result produced by the system.
     *
     * @return obtained result text
     */
    public String getObtained() {
        return obtained;
    }

    /**
     * Indicates whether the scenario passed.
     *
     * @return {@code true} if the obtained result matches the expected one
     */
    public boolean isPassed() {
        return passed;
    }
}
