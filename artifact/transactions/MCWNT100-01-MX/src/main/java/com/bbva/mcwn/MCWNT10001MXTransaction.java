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

public class MCWNT10001MXTransaction extends AbstractMCWNT10001MXTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNT10001MXTransaction.class);

	@Override
	public void execute() {
		LOGGER.info("[MCWNT10001MX] - Inicio de transacción");

		String clientDocument = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT);
		LOGGER.info("[MCWNT10001MX] - client-document: {}", clientDocument);

		// Validar clientType
		if (clientDocument == null || (!clientDocument.equals("0") && !clientDocument.equals("1"))) {
			LOGGER.error("[MCWNT10001MX] - clientType inválido: {}", clientDocument);
			this.addAdvice("MCWN01415030");
			this.setSeverity(Severity.ENR);
			return;
		}

		int clientType = Integer.parseInt(clientDocument);
		HolderInDTO in = this.getHolder();

		// Validar campos obligatorios según tipo
		if (clientType == 0) {
			if (in == null || in.getName() == null || in.getLastName() == null
					|| in.getAge() == null || in.getCurp() == null || in.getRfc() == null
					|| in.getAccount() == null || in.getAccount().getAccountNumber() == null) {
				this.addAdvice("MCWN01415034");
				this.setSeverity(Severity.ENR);
				return;
			}
			if (in.getCurp().length() != 18) {
				this.addAdvice("MCWN01415031");
				this.setSeverity(Severity.ENR);
				return;
			}
			if (in.getRfc().length() != 13) {
				this.addAdvice("MCWN01415032");
				this.setSeverity(Severity.ENR);
				return;
			}
		} else {
			if (in == null || in.getRfc() == null
					|| in.getAccount() == null || in.getAccount().getAccountNumber() == null) {
				this.addAdvice("MCWN01415034");
				this.setSeverity(Severity.ENR);
				return;
			}
			if (in.getRfc().length() != 12) {
				this.addAdvice("MCWN01415033");
				this.setSeverity(Severity.ENR);
				return;
			}
		}

		// Validar balance máximo
		String maxBalanceStr = this.getProperty("MAX.BALANCE");
		if (maxBalanceStr != null && in.getAccount().getBalance() != null) {
			double maxBalance = Double.parseDouble(maxBalanceStr);
			if (in.getAccount().getBalance() > maxBalance) {
				this.addAdvice("MCWN01415048");
				this.setSeverity(Severity.ENR);
				return;
			}
		}

		// Mapear a HolderDTO
		HolderDTO holder = new HolderDTO();
		holder.setName(in.getName());
		holder.setLastName(in.getLastName());
		holder.setAge(in.getAge());
		holder.setCurp(in.getCurp());
		holder.setRfc(in.getRfc());
		holder.setClientType(clientType);

		AccountDTO account = new AccountDTO();
		account.setAccountNumber(in.getAccount().getAccountNumber());
		account.setBalance(in.getAccount().getBalance() != null ? in.getAccount().getBalance() : 0.0);
		account.setAccountCard(in.getAccount().getAccountCard());
		holder.setAccount(account);

		MCWNR100 mcwnR100 = this.getServiceLibrary(MCWNR100.class);
		HolderDTO created = mcwnR100.executeCreateAccount(holder);

		if (created == null) {
			this.setSeverity(Severity.ENR);
			return;
		}

		AccountOutDTO accountOut = new AccountOutDTO();
		if (created.getAccount() != null) {
			accountOut.setAccountNumber(created.getAccount().getAccountNumber());
			accountOut.setAccountNip(created.getAccount().getAccountNip());
			accountOut.setBalance(created.getAccount().getBalance());
			accountOut.setAccountCard(created.getAccount().getAccountCard());
			accountOut.setAccountStatus(created.getAccount().getStatus());
		}

		HolderOutDTO holderOut = new HolderOutDTO();
		holderOut.setName(created.getName());
		holderOut.setLastName(created.getLastName());
		holderOut.setAge(created.getAge());
		holderOut.setCurp(created.getCurp());
		holderOut.setRfc(created.getRfc());
		holderOut.setHolderType(clientType == 0 ? "FISICO" : "MORAL");
		holderOut.setAccount(accountOut);

		this.setHolder(holderOut);

		LOGGER.info("[MCWNT10001MX] - Transacción finalizada correctamente");
	}
}