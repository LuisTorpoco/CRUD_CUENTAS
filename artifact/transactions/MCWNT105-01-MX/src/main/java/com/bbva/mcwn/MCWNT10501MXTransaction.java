package com.bbva.mcwn;

import com.bbva.elara.domain.transaction.Severity;
import com.bbva.mcwn.dto.holder.AccountInDTO;
import com.bbva.mcwn.lib.r100.MCWNR100;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCWNT10501MXTransaction extends AbstractMCWNT10501MXTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNT10501MXTransaction.class);

	@Override
	public void execute() {
		LOGGER.info("[MCWNT10501MX] - Inicio de transacción");

		AccountInDTO accountIn = this.getAccount();

		if (accountIn == null || accountIn.getAccountNumber() == null) {
			LOGGER.error("[MCWNT10501MX] - accountNumber obligatorio no recibido");
			this.addAdvice("MCWN01415034");
			this.setSeverity(Severity.ENR);
			return;
		}

		MCWNR100 mcwnR100 = this.getServiceLibrary(MCWNR100.class);
		int rows = mcwnR100.executeReactivateAccount(accountIn.getAccountNumber());

		if (rows == 0) {
			this.setSeverity(Severity.ENR);
			return;
		}

		LOGGER.info("[MCWNT10501MX] - Cuenta reactivada: {}", accountIn.getAccountNumber());
	}
}