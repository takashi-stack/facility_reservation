package com.example.facilityreservation.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.facilityreservation.domain.entity.FacilityEntity;
import com.example.facilityreservation.domain.entity.ReservationEntity;
import com.example.facilityreservation.domain.repository.ReservationRepository;
import com.example.facilityreservation.exception.BusinessException;
import com.example.facilityreservation.web.bean.ReserveCell;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    public ReserveCell[][] reservationList(FacilityEntity facility, int year,
                                           int month) {
        LocalDateTime firstDay = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime lastDay = firstDay.plusMonths(1)
            .minusSeconds(1);

        // 該当の年月と会議室から、予約の一覧を取得
        List<ReservationEntity> reservations = reservationRepository
            .findByFacilityAndStartTimeBetween(facility, firstDay, lastDay);

        // 該当年月の日数を計算
        int days = (int) ChronoUnit.DAYS.between(
            firstDay.toLocalDate(), lastDay.toLocalDate()) + 1;

        // 日付ごとに予約情報を整理
        ReserveCell[] list = new ReserveCell[days];
        for (int i = 0; i < list.length; i++) {
            // 配列の初期化処理
            list[i] = new ReserveCell(year, month, i + 1, facility, new ArrayList<>());
        }

        for (ReservationEntity reserve : reservations) {
            int dayOfMonth = reserve.getStartTime()
                .getDayOfMonth();
            list[dayOfMonth - 1].getReserves()
                .add(reserve);
        }

        ReserveCell[][] rows = new ReserveCell[6][7];
        int offset = firstDay.getDayOfWeek()
            .getValue() - 1;
        for (int i = 0; i < list.length; i++) {
            int row = (offset + i) / 7;
            int col = (offset + i) % 7;
            rows[row][col] = list[i];
        }

        return rows;
    }

    public void checkReserve(FacilityEntity facility, LocalDateTime startTime,
                             LocalDateTime endTime) throws BusinessException {
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException("終了時間は開始時間より未来に設定してください。");
        }

        List<ReservationEntity> duplicate = reservationRepository.findDuplicate(
            facility, startTime, endTime);
        if (!duplicate.isEmpty()) {
            throw new BusinessException("この時間はすでに予約されています。");
        }
    }

    @Transactional
    public void reserve(ReservationEntity reservation)
            throws BusinessException {
        checkReserve(
            reservation.getFacility(), reservation.getStartTime(), reservation
                .getEndTime());
        reservationRepository.save(reservation);
    }
}
