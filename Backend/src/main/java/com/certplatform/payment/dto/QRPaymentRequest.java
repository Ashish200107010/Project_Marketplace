package com.certplatform.payment.dto;


import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QRPaymentRequest {
    
    @NotNull private UUID studentId;
    @DecimalMin("1.0") private BigDecimal amount;

}
