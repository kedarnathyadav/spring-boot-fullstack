package com.kedarnath;


import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;

public class TestContianersTest extends AbstractTestcontainersUnitTest{

    @Test
    void canStartPostgresDB() {
        assertThat(postgresSQLContainer.isRunning()).isTrue();
        assertThat(postgresSQLContainer.isCreated()).isTrue();
    }


}
