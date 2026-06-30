package com.bbva.mcwn.lib.r101;

import com.bbva.mcwn.dto.holder.HolderDTO;
import java.util.List;

public interface MCWNR101 {

	List<HolderDTO> executeGetAccounts();

	HolderDTO executeGetAccountByNumber(Long accountNumber);

	HolderDTO executeGetAccountByCard(Long cardNumber);

	int executeInsertAccount(HolderDTO holder);

	int executeUpdateAccount(HolderDTO holder);

	int executeUpdateAccountStatus(Long accountNumber, Integer status);

}