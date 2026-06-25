package com.bbva.mcwn;

import com.bbva.mcwn.lib.r100.MCWNR100;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.AccountInDTO;
import com.bbva.mcwn.dto.holder.AccountOutDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import com.bbva.mcwn.dto.holder.HolderOutDTO;
import com.bbva.mcwn.lib.r100.MCWNR100;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCWNT10101MXTransaction extends AbstractMCWNT10101MXTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNT10101MXTransaction.class);

	@Override
	public void execute() {
		MCWNR100 mcwnR100 = this.getServiceLibrary(MCWNR100.class);

		LOGGER.info("[MCWNT10101MX] - Inicio de transacción");

		AccountInDTO accountIn = this.getAccount();

		if (accountIn == null) {
			LOGGER.error("[MCWNT10101MX] - El parámetro account es nulo");
			this.addAdvice("MCWN01415036");
			this.setSeverity(Severity.ENR);
			return;
		}

		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountNumber(accountIn.getAccountNumber());
		accountDTO.setAccountNip(accountIn.getAccountNip());

		LOGGER.info("[MCWNT10101MX] - Consultando cuenta: {}", accountIn.getAccountNumber());

		mcwnR100 = this.getServiceLibrary(MCWNR100.class);
		LOGGER.info("[MCWNT10101MX] - mcwnR100 es: {}", mcwnR100);

		HolderDTO holderDTO = mcwnR100.executeGetAccount(accountDTO);

		if (holderDTO == null) {
			LOGGER.warn("[MCWNT10101MX] - La librería no retornó resultado");
			this.setSeverity(Severity.ENR);
			return;
		}

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

		this.setHolder(holderOut);

		LOGGER.info("[MCWNT10101MX] - Transacción finalizada correctamente");
	}
}