package org.scoula.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.dto.TripInfoDTO;
import org.scoula.report.mapper.ChartMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final ChartMapper mapper;

    @Override
    public List<ChartDTO> getDonutChart(Integer tripId) {
        log.info("Fetching donut chart data for tripId={}", tripId);
        return mapper.selectDonutChart(tripId)
                .stream()
                .map(ChartDTO::of)
                .toList();
    }

    @Override
    public List<ChartDTO> getLineChart(Integer tripId) {
        log.info("Fetching line chart data for tripId={}", tripId);
        return mapper
                .selectLineChart(tripId)
                .stream()
                .map(ChartDTO::of)
                .toList();
    }

    @Override
    public TripInfoDTO getTripInfo(Integer tripId) {
        return mapper.selectTripInfo(tripId);
    }

}