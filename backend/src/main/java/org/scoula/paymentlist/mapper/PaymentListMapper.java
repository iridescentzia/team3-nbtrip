package org.scoula.paymentlist.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.scoula.paymentlist.domain.PaymentListVO;
import org.scoula.paymentlist.dto.PaymentListDTO;

import java.util.List;


@Mapper
public interface PaymentListMapper {
    List<PaymentListVO> selectPaymentList(
            @Param("tripId") Integer tripId,
            @Param("userId") Integer userId
    );
    List<PaymentListVO> selectPaymentById(
            @Param("paymentId") Integer paymentId,
            @Param("userId") Integer userId
    );

}
