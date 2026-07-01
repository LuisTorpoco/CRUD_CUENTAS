package com.bbva.mcwn.lib.r100.impl;

import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class MCWNR100Impl extends MCWNR100Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNR100Impl.class);
	//para evitar duplicados, se utiliza en los metodos
	private static final String ADVICE_ACCOUNT_NOT_FOUND = "MCWN01415035";
	private static final Random RANDOM = new Random();

	// Pendiente de implementación - retorna null de moment
	@Override
	public HolderDTO executeGetMessage(HolderDTO holderDTOIn, String clientDocument, String aap) {
		return null;
	}

	/* Busca una cuenta por numero,valida que esté activa
	y que el NIP sea correcto*/
	@Override
	public HolderDTO executeGetAccount(AccountDTO accountDTO) {
		LOGGER.info("[MCWNR100] - executeGetAccount - cuenta: {}", accountDTO.getAccountNumber());

		HolderDTO found = this.mcwnR101.executeGetAccountByNumber(accountDTO.getAccountNumber());

		if (found == null) {
			LOGGER.warn("[MCWNR100] - Cuenta no encontrada: {}", accountDTO.getAccountNumber());
			this.addAdvice(ADVICE_ACCOUNT_NOT_FOUND);
			return null;
		}

		if (found.getAccount().getStatus() != null && !found.getAccount().getStatus()) {
			LOGGER.warn("[MCWNR100] - Cuenta inactiva: {}", accountDTO.getAccountNumber());
			this.addAdvice("MCWN01415044");
			return null;
		}

		if (!found.getAccount().getAccountNip().equals(accountDTO.getAccountNip())) {
			LOGGER.warn("[MCWNR100] - NIP incorrecto para cuenta: {}", accountDTO.getAccountNumber());
			this.addAdvice("MCWN01415036");
			return null;
		}

		LOGGER.info("[MCWNR100] - Validación exitosa. Retornando HolderDTO completo.");
		return found;
	}

	//Retorna la lista completa de cuentas delegando en R101
	@Override
	public List<HolderDTO> executeGetAllAccounts() {
		LOGGER.info("[MCWNR100] - executeGetAllAccounts - Obteniendo lista completa de cuentas");
		return this.mcwnR101.executeGetAccounts();
	}

	//createAccount y updateAccount ahora validan que si el
	// usuario manda un accountCard, este no exista en otra cuenta activa
	@Override
	public HolderDTO executeCreateAccount(HolderDTO holder) {
		LOGGER.info("[MCWNR100] - executeCreateAccount - cuenta: {}", holder.getAccount().getAccountNumber());

		HolderDTO existingByNumber = this.mcwnR101.executeGetAccountByNumber(holder.getAccount().getAccountNumber());
		if (existingByNumber != null && existingByNumber.getAccount().getStatus()) {
			this.addAdvice("MCWN01415038");
			return null;
		}
		if (existingByNumber != null && !existingByNumber.getAccount().getStatus()) {
			this.addAdvice("MCWN01415042");
			return null;
		}

		if (holder.getAccount().getAccountCard() != null) {
			HolderDTO existingByCard = this.mcwnR101.executeGetAccountByCard(holder.getAccount().getAccountCard());
			if (existingByCard != null && existingByCard.getAccount().getStatus()) {
				this.addAdvice("MCWN01415039");
				return null;
			}
			if (existingByCard != null && !existingByCard.getAccount().getStatus()) {
				this.addAdvice("MCWN01415043");
				return null;
			}
		}

		// Si el NIP no se proporciona, generamos uno aleatorio de 4 dígitos
		Long nip = 1000L + (RANDOM.nextLong() % 9000);
		holder.getAccount().setAccountNip(nip);

		int rows = this.mcwnR101.executeInsertAccount(holder);
		if (rows == 0) {
			this.addAdvice("MCWN01415040");
			return null;
		}

		return this.mcwnR101.executeGetAccountByNumber(holder.getAccount().getAccountNumber());
	}

	@Override
	public HolderDTO executeUpdateAccount(HolderDTO holder) {
		LOGGER.info("[MCWNR100] - executeUpdateAccount - cuenta: {}", holder.getAccount().getAccountNumber());

		HolderDTO existing = this.mcwnR101.executeGetAccountByNumber(holder.getAccount().getAccountNumber());
		if (existing == null) {
			this.addAdvice(ADVICE_ACCOUNT_NOT_FOUND);
			return null;
		}

		if (existing.getAccount().getStatus() != null && !existing.getAccount().getStatus()) {
			this.addAdvice("MCWN01415037");
			return null;
		}

		resolveAccountCard(holder, existing);
		resolveRemainingFields(holder, existing);

		int rows = this.mcwnR101.executeUpdateAccount(holder);
		if (rows == 0) {
			this.addAdvice("MCWN01415041");
			return null;
		}

		return this.mcwnR101.executeGetAccountByNumber(holder.getAccount().getAccountNumber());
	}

	private void resolveAccountCard(HolderDTO holder, HolderDTO existing) {
		if (holder.getAccount().getAccountCard() == null) {
			holder.getAccount().setAccountCard(existing.getAccount().getAccountCard());
		} else if (!holder.getAccount().getAccountCard().equals(existing.getAccount().getAccountCard())) {
			HolderDTO existingByCard = this.mcwnR101.executeGetAccountByCard(holder.getAccount().getAccountCard());
			if (existingByCard != null && !existingByCard.getAccount().getAccountNumber().equals(holder.getAccount().getAccountNumber())) {
				this.addAdvice("MCWN01415039");
			}
		}
	}
	//resolve remaining hace que si el usuario no manda algun campo,
	//se mantenga el valor que ya tenia en la base de datos
	private void resolveRemainingFields(HolderDTO holder, HolderDTO existing) {
		if (holder.getAccount().getAccountNip() == null) { holder.getAccount().setAccountNip(existing.getAccount().getAccountNip()); }
		if (holder.getAccount().getBalance() == null) { holder.getAccount().setBalance(existing.getAccount().getBalance()); }
		if (holder.getName() == null) { holder.setName(existing.getName()); }
		if (holder.getLastName() == null) { holder.setLastName(existing.getLastName()); }
		if (holder.getAge() == null) { holder.setAge(existing.getAge()); }
		if (holder.getCurp() == null) { holder.setCurp(existing.getCurp()); }
		if (holder.getRfc() == null) { holder.setRfc(existing.getRfc()); }
	}
	
	//Desactiva una cuenta (borrado lógico) actualizando su status a 0
	@Override
	public int executeDeleteAccount(Long accountNumber) {
		LOGGER.info("[MCWNR100] - executeDeleteAccount - cuenta: {}", accountNumber);

		HolderDTO existing = this.mcwnR101.executeGetAccountByNumber(accountNumber);
		if (existing == null) {
			this.addAdvice(ADVICE_ACCOUNT_NOT_FOUND);
			return 0;
		}

		if (existing.getAccount().getStatus() != null && !existing.getAccount().getStatus()) {
			this.addAdvice("MCWN01415037");
			return 0;
		}

		int rows = this.mcwnR101.executeUpdateAccountStatus(accountNumber, 0);
		if (rows == 0) {
			this.addAdvice("MCWN01415041");
		}
		//rows es el numero de filas afectadas, si es 0 significa que no se pudo actualizar el estatus de la cuenta
		return rows;
	}

	@Override
	public int executeReactivateAccount(Long accountNumber) {
		LOGGER.info("[MCWNR100] - executeReactivateAccount - cuenta: {}", accountNumber);

		HolderDTO existing = this.mcwnR101.executeGetAccountByNumber(accountNumber);
		if (existing == null) {
			this.addAdvice(ADVICE_ACCOUNT_NOT_FOUND);
			return 0;
		}

		if (existing.getAccount().getStatus() != null && existing.getAccount().getStatus()) {
			this.addAdvice("MCWN01415047");
			return 0;
		}

		int rows = this.mcwnR101.executeUpdateAccountStatus(accountNumber, 1);
		if (rows == 0) {
			this.addAdvice("MCWN01415046");
		}
		return rows;
	}
}