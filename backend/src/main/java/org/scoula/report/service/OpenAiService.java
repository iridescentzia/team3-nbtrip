package org.scoula.report.service;

import org.scoula.report.dto.OpenAiDTO;

import java.util.List;

public interface OpenAiService {

    List<OpenAiDTO> getDonutReport(Integer tripId);

    List<OpenAiDTO> getLineReport(Integer tripId);


}
