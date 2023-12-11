package com.ogae.service;

import com.ogae.vo.PaymentVO;

public interface PaymentService {
	PaymentVO getPayment(String param);
	void insertPayment(PaymentVO vo);
}
