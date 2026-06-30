package com.bbva.mcwn.lib.r100;

import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;

import java.util.List;

public interface MCWNR100 {
	HolderDTO executeGetMessage(HolderDTO holderDTOIn, String clientDocument, String aap);
	HolderDTO executeGetAccount(AccountDTO account);
	List<HolderDTO> executeGetAllAccounts();
	HolderDTO executeCreateAccount(HolderDTO holder);
	HolderDTO executeUpdateAccount(HolderDTO holder);
	int executeDeleteAccount(Long accountNumber);
	int executeReactivateAccount(Long accountNumber);
}