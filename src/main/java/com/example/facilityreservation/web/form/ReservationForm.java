package com.example.facilityreservation.web.form;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReservationForm implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer reservationId;

    @NotNull
    private Integer y;
    @NotNull
    private Integer m;
    @NotNull
    private Integer d;

    @NotNull
    @Pattern(regexp = "[0-9]+")
    private String startHour;

    @NotNull
    @Pattern(regexp = "[0-9]+")
    private String startMin;

    @NotNull
    @Pattern(regexp = "[0-9]+")
    private String endHour;

    @NotNull
    @Pattern(regexp = "[0-9]+")
    private String endMin;

    public LocalDateTime getStartTime() {
        int hour = Integer.parseInt(startHour);
        int minute = Integer.parseInt(startMin);

        return LocalDateTime.of(y, m, d, hour, minute, 0, 0);
    }

    public LocalDateTime getEndTime() {
        int hour = Integer.parseInt(endHour);
        int minute = Integer.parseInt(endMin);

        return LocalDateTime.of(y, m, d, hour, minute, 0, 0);
    }
}
