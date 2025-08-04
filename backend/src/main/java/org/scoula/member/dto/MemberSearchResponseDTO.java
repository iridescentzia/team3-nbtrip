package org.scoula.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.scoula.member.domain.MemberVO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberSearchResponseDTO {
    private int userId;
    private String nickname;

    public static MemberSearchResponseDTO of(MemberVO memberVO) {
        return MemberSearchResponseDTO.builder()
                .nickname(memberVO.getNickname())
                .userId(memberVO.getUserId())
                .build();
    }
}
