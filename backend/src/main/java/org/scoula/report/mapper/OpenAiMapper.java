package org.scoula.report.mapper;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.scoula.report.domain.OpenAiVO;

import java.util.List;


@Mapper
public interface OpenAiMapper {

    List<OpenAiVO> selectDonutReport(@Param("tripId")Integer tripId);


    List<OpenAiVO> selectLineReport(@Param("tripId")Integer tripId);


}