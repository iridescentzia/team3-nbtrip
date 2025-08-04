package org.scoula.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.dto.OpenAiDTO;
import org.scoula.report.mapper.OpenAiMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final OpenAiMapper mapper;

    @Override
    public List<OpenAiDTO> getDonutReport(Integer tripId) {
        return mapper
                .selectDonutReport(tripId)
                .stream()
                .map(OpenAiDTO::of)
                .toList();
    }

    @Override
    public List<OpenAiDTO> getLineReport(Integer tripId) {
        return mapper
                .selectLineReport(tripId)
                .stream()
                .map(OpenAiDTO::of)
                .toList();
    }

}