package com.bbva.mcwn;

import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.AccountOutDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import com.bbva.mcwn.dto.holder.HolderInDTO;
import com.bbva.mcwn.dto.holder.HolderOutDTO;
import com.bbva.mcwn.lib.r100.MCWNR100;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCWNT10301MXTransaction extends AbstractMCWNT10301MXTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNT10301MXTransaction.class);

	@Override
	public void execute() {
		LOGGER.info("[MCWNT10301MX] - Inicio de transacción");

		String clientDocument = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT);

		// Validar clientType
		if (clientDocument == null || (!clientDocument.equals("0") && !clientDocument.equals("1"))) {
			this.addAdvice("MCWN01415030");
			this.setSeverity(Severity.ENR);
			return;
		}

		int clientType = Integer.parseInt(clientDocument);
		HolderInDTO in = this.getHolder();

		// Validar accountNumber obligatorio
		if (in == null || in.getAccount() == null || in.getAccount().getAccountNumber() == null) {
			this.addAdvice("MCWN01415034");
			this.setSeverity(Severity.ENR);
			return;
		}

		// Validaciones según tipo de persona
		if (clientType == 0) {
			// Persona Física
			if (in.getCurp() != null && in.getCurp().length() != 18) {
				this.addAdvice("MCWN01415031");
				this.setSeverity(Severity.ENR);
				return;
			}
			if (in.getRfc() != null && in.getRfc().length() != 13) {
				this.addAdvice("MCWN01415032");
				this.setSeverity(Severity.ENR);
				return;
			}
		} else {
			// Persona Moral
			// No deben enviarse campos de persona física
			if (in.getCurp() != null) {
				this.addAdvice("MCWN01415045");
				this.setSeverity(Severity.ENR);
				return;
			}
			if (in.getAge() != null) {
				this.addAdvice("MCWN01415045");
				this.setSeverity(Severity.ENR);
				return;
			}
			if (in.getLastName() != null) {
				this.addAdvice("MCWN01415045");
				this.setSeverity(Severity.ENR);
				return;
			}
			if (in.getRfc() != null && in.getRfc().length() != 12) {
				this.addAdvice("MCWN01415033");
				this.setSeverity(Severity.ENR);
				return;
			}
		}

		// Mapear a HolderDTO
		HolderDTO holder = new HolderDTO();
		holder.setClientType(clientType);

		if (clientType == 0) {
			holder.setName(in.getName());
			holder.setLastName(in.getLastName());
			holder.setAge(in.getAge());
			holder.setCurp(in.getCurp());
			holder.setRfc(in.getRfc());
		} else {
			// Persona Moral: limpiar campos de física
			holder.setName(in.getName());
			holder.setLastName("NO_APLICA");
			holder.setAge(0L);
			holder.setCurp("NO_APLICA");
			holder.setRfc(in.getRfc());
		}

		AccountDTO account = new AccountDTO();
		account.setAccountNumber(in.getAccount().getAccountNumber());
		account.setBalance(in.getAccount().getBalance());
		account.setAccountCard(in.getAccount().getAccountCard());
		account.setAccountNip(in.getAccount().getAccountNip());
		holder.setAccount(account);

		MCWNR100 mcwnR100 = this.getServiceLibrary(MCWNR100.class);
		HolderDTO updated = mcwnR100.executeUpdateAccount(holder);

		if (updated == null) {
			this.setSeverity(Severity.ENR);
			return;
		}

		AccountOutDTO accountOut = new AccountOutDTO();
		if (updated.getAccount() != null) {
			accountOut.setAccountNumber(updated.getAccount().getAccountNumber());
			accountOut.setAccountNip(updated.getAccount().getAccountNip());
			accountOut.setBalance(updated.getAccount().getBalance());
			accountOut.setAccountCard(updated.getAccount().getAccountCard());
			accountOut.setAccountStatus(updated.getAccount().getStatus());
		}

		HolderOutDTO holderOut = new HolderOutDTO();
		holderOut.setName(updated.getName());
		holderOut.setLastName(updated.getLastName());
		holderOut.setAge(updated.getAge());
		holderOut.setCurp(updated.getCurp());
		holderOut.setRfc(updated.getRfc());
		holderOut.setHolderType(clientType == 0 ? "FISICO" : "MORAL");
		holderOut.setAccount(accountOut);

		this.setHolder(holderOut);

		LOGGER.info("[MCWNT10301MX] - Transacción finalizada correctamente");
	}
}