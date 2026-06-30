package com.bbva.mcwn.lib.r101.impl;

import com.bbva.apx.exception.db.NoResultException;
import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCWNR101Impl extends MCWNR101Abstract {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNR101Impl.class);

	@Override
	public List<HolderDTO> executeGetAccounts() {
		LOGGER.info("Ejecucion executeGetAccounts");
		Map<String, Object> args = new HashMap<>();
		args.put("status", 1);
		List<HolderDTO> accountsList = new ArrayList<>();
		try {
			List<Map<String, Object>> accountsData = jdbcUtils.queryForList("select.accounts.by.status", args);
			if (accountsData != null && !accountsData.isEmpty()) {
				for (Map<String, Object> account : accountsData) {
					accountsList.add(mapRowToHolder(account));
				}
			}
		} catch (NoResultException e) {
			LOGGER.info("Error: {}", e.getMessage());
		}
		return accountsList;
	}

	@Override
	public HolderDTO executeGetAccountByNumber(Long accountNumber) {
		LOGGER.info("Ejecucion executeGetAccountByNumber: {}", accountNumber);
		Map<String, Object> args = new HashMap<>();
		args.put("accountNumber", accountNumber);
		try {
			List<Map<String, Object>> result = jdbcUtils.queryForList("select.account.by.number", args);
			if (result != null && !result.isEmpty()) {
				return mapRowToHolder(result.get(0));
			}
		} catch (NoResultException e) {
			LOGGER.info("Cuenta no encontrada: {}", e.getMessage());
		}
		return null;
	}

	@Override
	public HolderDTO executeGetAccountByCard(Long cardNumber) {
		LOGGER.info("Ejecucion executeGetAccountByCard: {}", cardNumber);
		Map<String, Object> args = new HashMap<>();
		args.put("cardNumber", cardNumber);
		try {
			List<Map<String, Object>> result = jdbcUtils.queryForList("select.account.by.card", args);
			if (result != null && !result.isEmpty()) {
				return mapRowToHolder(result.get(0));
			}
		} catch (NoResultException e) {
			LOGGER.info("Cuenta no encontrada por tarjeta: {}", e.getMessage());
		}
		return null;
	}

	@Override
	public int executeInsertAccount(HolderDTO holder) {
		LOGGER.info("Ejecucion executeInsertAccount: {}", holder.getAccount().getAccountNumber());
		Map<String, Object> args = new HashMap<>();
		args.put("accountNumber", holder.getAccount().getAccountNumber());
		args.put("cardNumber", holder.getAccount().getAccountCard());
		args.put("balance", holder.getAccount().getBalance());
		args.put("nip", holder.getAccount().getAccountNip());
		args.put("firstName", holder.getName());
		args.put("lastName", holder.getLastName());
		args.put("age", holder.getAge());
		args.put("curp", holder.getCurp());
		args.put("rfc", holder.getRfc());
		args.put("clientType", holder.getClientType());
		return jdbcUtils.update("insert.account", args);
	}

	@Override
	public int executeUpdateAccount(HolderDTO holder) {
		LOGGER.info("Ejecucion executeUpdateAccount: {}", holder.getAccount().getAccountNumber());
		Map<String, Object> args = new HashMap<>();
		args.put("accountNumber", holder.getAccount().getAccountNumber());
		args.put("cardNumber", holder.getAccount().getAccountCard());
		args.put("balance", holder.getAccount().getBalance());
		args.put("nip", holder.getAccount().getAccountNip());
		args.put("firstName", holder.getName());
		args.put("lastName", holder.getLastName());
		args.put("age", holder.getAge());
		args.put("curp", holder.getCurp());
		args.put("rfc", holder.getRfc());
		return jdbcUtils.update("update.account", args);
	}

	@Override
	public int executeUpdateAccountStatus(Long accountNumber, Integer status) {
		LOGGER.info("Ejecucion executeUpdateAccountStatus: {} status: {}", accountNumber, status);
		Map<String, Object> args = new HashMap<>();
		args.put("accountNumber", accountNumber);
		args.put("status", status);
		return jdbcUtils.update("update.account.status", args);
	}

	private HolderDTO mapRowToHolder(Map<String, Object> row) {
		HolderDTO holder = new HolderDTO();
		holder.setName((String) row.get("FIRST_NAME"));
		holder.setLastName((String) row.get("LAST_NAME"));
		holder.setCurp((String) row.get("CURP"));
		holder.setRfc((String) row.get("RFC"));
		holder.setAge(parseBigDecimalToLong(row.get("AGE")));
		holder.setClientType(parseBigDecimalToInt(row.get("CLIENT_TYPE")));

		AccountDTO innerAccount = new AccountDTO();
		innerAccount.setAccountNumber(parseBigDecimalToLong(row.get("ACCOUNT_NUMBER")));
		innerAccount.setAccountNip(parseBigDecimalToLong(row.get("NIP")));
		innerAccount.setBalance(parseBigDecimalToDouble(row.get("BALANCE")));
		innerAccount.setAccountCard(parseBigDecimalToLong(row.get("CARD_NUMBER")));
		innerAccount.setStatus(row.get("STATUS") != null && parseBigDecimalToLong(row.get("STATUS")) == 1L);

		holder.setAccount(innerAccount);
		return holder;
	}

	private static Long parseBigDecimalToLong(Object object) {
		if (object instanceof BigDecimal) {
			return ((BigDecimal) object).longValue();
		}
		return null;
	}

	private static Double parseBigDecimalToDouble(Object object) {
		if (object instanceof BigDecimal) {
			return ((BigDecimal) object).doubleValue();
		}
		return null;
	}

	private static Integer parseBigDecimalToInt(Object object) {
		if (object instanceof BigDecimal) {
			return ((BigDecimal) object).intValue();
		}
		return null;
	}
}