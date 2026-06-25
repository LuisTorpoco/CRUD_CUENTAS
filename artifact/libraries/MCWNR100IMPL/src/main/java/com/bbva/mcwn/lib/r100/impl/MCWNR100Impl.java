package com.bbva.mcwn.lib.r100.impl;

import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MCWNR100Impl extends MCWNR100Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNR100Impl.class);

	@Override
	public HolderDTO executeGetMessage(HolderDTO holderDTOIn, String clientDocument, String aap) {
		return null;
	}

	@Override
	public HolderDTO executeGetAccount(AccountDTO accountDTO) {

		LOGGER.info("[MCWNR100] - executeGetAccount - cuenta: {}", accountDTO.getAccountNumber());
		LOGGER.info("[MCWNR100] - mcwnR101 es: {}", this.mcwnR101);

		List<HolderDTO> database = this.mcwnR101.executeGetAccounts();

		HolderDTO found = null;
		for (HolderDTO holder : database) {
			if (holder.getAccount() != null &&
					holder.getAccount().getAccountNumber().equals(accountDTO.getAccountNumber())) {
				found = holder;
				break;
			}
		}

		if (found == null) {
			LOGGER.warn("[MCWNR100] - Cuenta no encontrada: {}", accountDTO.getAccountNumber());
			this.addAdvice("MCWN01415036");
			return null;
		}

		if (!found.getAccount().getAccountNip().equals(accountDTO.getAccountNip())) {
			LOGGER.warn("[MCWNR100] - NIP incorrecto para cuenta: {}", accountDTO.getAccountNumber());
			this.addAdvice("MCWN01415037");
			return null;
		}

		LOGGER.info("[MCWNR100] - Validación exitosa. Retornando HolderDTO completo.");
		return found;
	}

	@Override
	public List<HolderDTO> executeGetAllAccounts() {
		LOGGER.info("[MCWNR100] - executeGetAllAccounts - Obteniendo lista completa de cuentas");
		return this.mcwnR101.executeGetAccounts();
	}
}