package Tests;

import CapableSimulator.CapableWorld;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class FungiTests {

    CapableWorld world;

    @BeforeEach
    void setup() {
        world = new CapableWorld(5);
    }

    @RepeatedTest(1)
    void fungiSpreadSporesTest() {

    }

    @AfterEach
    void tearDown() {

    }

}
