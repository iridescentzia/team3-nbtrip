package org.scoula.report.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.report.domain.ReportCommentVO;

@Mapper
public interface ReportCommentMapper {
    // 가장 최근 한 건
    ReportCommentVO findLatest(@Param("tripId") int tripId, @Param("type") String type);

    // 새 코멘트 저장 (히스토리 보존; generated_at은 NOW()로 기록)
    void insert(@Param("tripId") int tripId,
                @Param("type") String type,
                @Param("content") String content);
}
