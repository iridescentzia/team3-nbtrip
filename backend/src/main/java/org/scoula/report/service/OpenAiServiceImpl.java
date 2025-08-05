package org.scoula.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.dto.ChartDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    private final ChartService chartService;
    private final ChatGPTClient chatGPTClient;

    @Override
    public String getDonutReport(int tripId) {
        List<ChartDTO> donutData = chartService.getDonutChart(tripId);
        String prompt = buildDonutPrompt(donutData);
        return chatGPTClient.getCompletion(prompt);
    }

    @Override
    public String getLineReport(int tripId) {
        List<ChartDTO> lineData = chartService.getLineChart(tripId);
        String prompt = buildLinePrompt(lineData);
        return chatGPTClient.getCompletion(prompt);
    }

    private String buildDonutPrompt(List<ChartDTO> donutData) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음은 여행 지출의 카테고리별 비율입니다:\n");
        for (ChartDTO data : donutData) {
            sb.append("- ").append(data.getCategory())
                    .append(": ").append(String.format("%.1f", data.getTotal_amount()))
                    .append("원\n");
        }
        sb.append("\n이 데이터를 기반으로 140자 이내의 소비 습관 조언을 한국어로 작성해 주세요.");
        return sb.toString();
    }

    private String buildLinePrompt(List<ChartDTO> lineData) {
        StringBuilder sb = new StringBuilder();
        sb.append("다음은 여행 기간 동안의 날짜별 지출 내역입니다:\n");
        for (ChartDTO data : lineData) {
            sb.append("- ").append(data.getDate())
                    .append(": ").append(String.format("%.1f", data.getTotal_amount()))
                    .append("원 (").append(data.getCount()).append("건)\n");
        }
        sb.append("\n이 데이터를 분석해 소비 추이에 대한 조언을 140자 이내 한국어로 작성해 주세요.");
        return sb.toString();
    }
}
