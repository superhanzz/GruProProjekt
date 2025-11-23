package Tests;

import CapableSimulator.Rabbit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class GrassSpawnAmountTest extends SpawnAmountTest{

    @BeforeEach
    public void setUp() {
        buildSpawnCalls("grass");
    }

    @RepeatedTest(10)
    public void RabbitSpawnAmountTest(){
        checkSpawns("grass");
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();
    }
}

