package Tests;

import CapableSimulator.Rabbit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class RabbitSpawnAmountTest extends SpawnAmountTest{



    @BeforeEach
    public void setUp() {
        buildSpawnCalls("rabbit");
    }

    @RepeatedTest(10)
    public void RabbitSpawnAmountTest(){
        checkSpawns("rabbit");
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();
    }
}
