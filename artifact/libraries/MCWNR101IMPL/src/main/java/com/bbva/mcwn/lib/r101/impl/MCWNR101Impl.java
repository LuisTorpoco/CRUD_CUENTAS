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
			LOGGER.info("{}", accountsData);

			if (accountsData != null && !accountsData.isEmpty()) {
				for (Map<String, Object> account : accountsData) {
					HolderDTO holder = new HolderDTO();
					holder.setName((String) account.get("FIRST_NAME"));
					holder.setLastName((String) account.get("LAST_NAME"));
					holder.setCurp((String) account.get("CURP"));
					holder.setRfc((String) account.get("RFC"));
					holder.setAge(parseBigDecimalToLong(account.get("AGE")));

					AccountDTO innerAccount = new AccountDTO();
					innerAccount.setAccountNumber(parseBigDecimalToLong(account.get("ACCOUNT_NUMBER")));
					innerAccount.setAccountNip(parseBigDecimalToLong(account.get("NIP")));
					innerAccount.setBalance(parseBigDecimalToDouble(account.get("BALANCE")));
					innerAccount.setAccountCard(parseBigDecimalToLong(account.get("CARD_NUMBER")));
					innerAccount.setStatus(account.get("STATUS") != null && parseBigDecimalToLong(account.get("STATUS")) == 1L);

					holder.setAccount(innerAccount);
					accountsList.add(holder);
				}
			}

		} catch (NoResultException e) {
			LOGGER.info("Error: {}", e.getMessage());
		}
		return accountsList;
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
}