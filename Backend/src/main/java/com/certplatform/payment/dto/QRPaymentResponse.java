package com.certplatform.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QRPaymentResponse {

    private String qrCodeUrl;
    private String orderId;

}
