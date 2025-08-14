package org.scoula.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.report.dto.ChartDTO;
import org.scoula.report.mapper.ReportCommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OpenAiServiceImpl implements OpenAiService {

    private static final String FALLBACK = "AI 분석을 불러오는 데 실패했습니다.";

    private final ChartService chartService;
    private final ChatGPTClient chatGPTClient;
    private final ReportCommentMapper reportCommentMapper;

    @Override
    public String getDonutReport(int tripId) {
        // 1) 이미 있으면 재사용
        var saved = reportCommentMapper.findLatest(tripId, "CATEGORY");
        if (saved != null) return saved.getContent();

        // 2) 생성 시도
        var donutData = chartService.getDonutChart(tripId);
        String prompt = buildDonutPrompt(donutData);
        String content = chatGPTClient.getCompletion(prompt);

        // 3) 실패면 저장하지 말고 실패 문구만 반환
        if (content == null || content.isBlank()) {
            log.warn("[AI] CATEGORY 생성 실패 -> DB 저장 안 함 (tripId={})", tripId);
            return FALLBACK;
        }

        // 4) 성공 시에만 저장
        reportCommentMapper.insert(tripId, "CATEGORY", content);
        return content;
    }

    @Override
    public String getLineReport(int tripId) {
        var saved = reportCommentMapper.findLatest(tripId, "DATE");
        if (saved != null) return saved.getContent();

        var lineData = chartService.getLineChart(tripId);
        String prompt = buildLinePrompt(lineData);
        String content = chatGPTClient.getCompletion(prompt);

        if (content == null || content.isBlank()) {
            log.warn("[AI] DATE 생성 실패 -> DB 저장 안 함 (tripId={})", tripId);
            return FALLBACK;
        }

        reportCommentMapper.insert(tripId, "DATE", content);
        return content;
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

    private String safeCallOpenAI(String prompt) {
        try {
            String res = chatGPTClient.getCompletion(prompt);
            if (res == null || res.isBlank()) {
                return "AI 분석을 생성하지 못했습니다. 잠시 후 다시 시도해 주세요.";
            }
            return res;
        } catch (Exception e) {
            log.error("OpenAI 호출 실패", e);
            return "AI 분석을 생성하지 못했습니다. 잠시 후 다시 시도해 주세요.";
        }
    }
}