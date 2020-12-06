package com.example.facilityreservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

@SpringBootApplication
@EntityScan(basePackageClasses = {
    FacilityReservationApplication.class,
    Jsr310JpaConverters.class
})
public class FacilityReservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(FacilityReservationApplication.class, args);
    }
}
