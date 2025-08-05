package org.scoula.paymentlist.mapper;

import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;
import org.scoula.paymentlist.domain.PaymentListVO;

import java.util.List;


@Mapper
public interface PaymentListMapper {
    List<PaymentListVO> selectPaymentList(@Param("tripId")Integer tripId);
}
