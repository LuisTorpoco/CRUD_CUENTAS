package com.bbva.mcwn;

import com.bbva.mcwn.dto.holder.AccountOutDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import com.bbva.mcwn.dto.holder.HolderOutDTO;
import com.bbva.mcwn.lib.r100.MCWNR100;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MCWNT10201MXTransaction extends AbstractMCWNT10201MXTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNT10201MXTransaction.class);

	@Override
	public void execute() {
		LOGGER.info("[MCWNT10201MX] - Inicio de transacción");

		MCWNR100 mcwnR100 = this.getServiceLibrary(MCWNR100.class);

		List<HolderDTO> holderList = mcwnR100.executeGetAllAccounts();

		List<HolderOutDTO> holdersOut = new ArrayList<>();
		for (HolderDTO holderDTO : holderList) {

			AccountOutDTO accountOut = new AccountOutDTO();
			if (holderDTO.getAccount() != null) {
				accountOut.setAccountNumber(holderDTO.getAccount().getAccountNumber());
				accountOut.setAccountNip(holderDTO.getAccount().getAccountNip());
				accountOut.setBalance(holderDTO.getAccount().getBalance());
				accountOut.setAccountCard(holderDTO.getAccount().getAccountCard());
				accountOut.setAccountStatus(holderDTO.getAccount().getStatus());
			}

			HolderOutDTO holderOut = new HolderOutDTO();
			holderOut.setName(holderDTO.getName());
			holderOut.setLastName(holderDTO.getLastName());
			holderOut.setAge(holderDTO.getAge());
			holderOut.setCurp(holderDTO.getCurp());
			holderOut.setRfc(holderDTO.getRfc());
			holderOut.setAccount(accountOut);
			holderOut.setHolderType(holderDTO.getClientType() != null && holderDTO.getClientType() == 0 ? "FISICO" : "MORAL");

			holdersOut.add(holderOut);
		}

		this.setHolders(holdersOut);

		LOGGER.info("[MCWNT10201MX] - Transacción finalizada correctamente");
	}
}