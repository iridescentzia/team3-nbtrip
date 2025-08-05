package org.scoula.report.service;

public interface OpenAiService {
    String getDonutReport(int tripId);  // 카테고리 기반 분석
    String getLineReport(int tripId);   // 날짜 기반 분석
}


