package com.example.facilityreservation;

<<<<<<< HEAD
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FacilityReservationApplicationTests {

    @Test
    public void contextLoads() {
    }

}
=======
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class FacilityReservationApplicationTests {

    @Test
    public void contextLoads() {
    }
}
>>>>>>> 3459847 (テストコード追加)
