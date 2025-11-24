package Tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

public class BurrowSpawnAmountTest extends SpawnAmountTest{

    @BeforeEach
    public void setUp() {
        buildSpawnCalls("burrow");
    }

    @RepeatedTest(10)
    public void RabbitSpawnAmountTest(){
        checkSpawns("burrow");
    }

    @AfterEach
    public void tearDown() {
        super.tearDown();
    }

}
