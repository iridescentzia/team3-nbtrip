package org.scoula.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportCommentVO {
    private int tripId;
    private String commentType; // "CATEGORY" | "DATE"
    private String content;
    private Timestamp generatedAt;
}