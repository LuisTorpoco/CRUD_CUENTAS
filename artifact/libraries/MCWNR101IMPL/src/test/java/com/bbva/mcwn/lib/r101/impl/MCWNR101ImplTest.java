package com.bbva.mcwn.lib.r101.impl;
import com.bbva.apx.exception.db.NoResultException;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.utility.jdbc.JdbcUtils;
import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class MCWNR101ImplTest {

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private JdbcUtils jdbcUtils;

	@InjectMocks
	private MCWNR101Impl mcwnR101;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ThreadContext.set(new Context());
	}

	private Map<String, Object> buildRow() {
		Map<String, Object> row = new HashMap<>();
		row.put("FIRST_NAME", "Juan");
		row.put("LAST_NAME", "Perez");
		row.put("CURP", "JUAP900101HDFRRN01");
		row.put("RFC", "JUAP900101XXX");
		row.put("AGE", BigDecimal.valueOf(30));
		row.put("CLIENT_TYPE", BigDecimal.valueOf(0));
		row.put("ACCOUNT_NUMBER", BigDecimal.valueOf(100001));
		row.put("NIP", BigDecimal.valueOf(1256));
		row.put("BALANCE", BigDecimal.valueOf(15000.5));
		row.put("CARD_NUMBER", BigDecimal.valueOf(4000412340000000L));
		row.put("STATUS", BigDecimal.valueOf(1));
		return row;
	}

	@Test
	public void executeGetAccountsHappyPath() {
		List<Map<String, Object>> rows = new ArrayList<>();
		rows.add(buildRow());
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenReturn(rows);

		List<HolderDTO> result = mcwnR101.executeGetAccounts();

		Assert.assertEquals(1, result.size());
		Assert.assertEquals("Juan", result.get(0).getName());
	}

	@Test
	public void executeGetAccountsEmptyList() {
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenReturn(new ArrayList<>());

		List<HolderDTO> result = mcwnR101.executeGetAccounts();

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void executeGetAccountsNoResultException() {
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenThrow(new NoResultException("Sin resultados"));

		List<HolderDTO> result = mcwnR101.executeGetAccounts();

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void executeGetAccountByNumberHappyPath() {
		List<Map<String, Object>> rows = new ArrayList<>();
		rows.add(buildRow());
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenReturn(rows);

		HolderDTO result = mcwnR101.executeGetAccountByNumber(100001L);

		Assert.assertNotNull(result);
		Assert.assertEquals(Long.valueOf(100001), result.getAccount().getAccountNumber());
	}

	@Test
	public void executeGetAccountByNumberNotFound() {
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenReturn(new ArrayList<>());

		HolderDTO result = mcwnR101.executeGetAccountByNumber(999999L);

		Assert.assertNull(result);
	}

	@Test
	public void executeGetAccountByNumberNoResultException() {
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenThrow(new NoResultException("Sin resultados"));

		HolderDTO result = mcwnR101.executeGetAccountByNumber(100001L);

		Assert.assertNull(result);
	}

	@Test
	public void executeGetAccountByCardHappyPath() {
		List<Map<String, Object>> rows = new ArrayList<>();
		rows.add(buildRow());
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenReturn(rows);

		HolderDTO result = mcwnR101.executeGetAccountByCard(4000412340000000L);

		Assert.assertNotNull(result);
	}

	@Test
	public void executeGetAccountByCardNotFound() {
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenReturn(new ArrayList<>());

		HolderDTO result = mcwnR101.executeGetAccountByCard(1111111111111111L);

		Assert.assertNull(result);
	}

	@Test
	public void executeGetAccountByCardNoResultException() {
		when(jdbcUtils.queryForList(anyString(), anyMap())).thenThrow(new NoResultException("Sin resultados"));

		HolderDTO result = mcwnR101.executeGetAccountByCard(4000412340000000L);

		Assert.assertNull(result);
	}

	@Test
	public void executeInsertAccountHappyPath() {
		when(jdbcUtils.update(anyString(), anyMap())).thenReturn(1);

		HolderDTO holder = new HolderDTO();
		AccountDTO account = new AccountDTO();
		account.setAccountNumber(200002L);
		account.setAccountCard(1234567890123456L);
		account.setBalance(5000.0);
		account.setAccountNip(9442L);
		holder.setAccount(account);
		holder.setName("Pedro");
		holder.setLastName("Garcia");
		holder.setAge(25L);
		holder.setCurp("GAGP010101HDFRCR01");
		holder.setRfc("GAGP010101AB3");
		holder.setClientType(0);

		int rows = mcwnR101.executeInsertAccount(holder);

		Assert.assertEquals(1, rows);
	}

	@Test
	public void executeUpdateAccountHappyPath() {
		when(jdbcUtils.update(anyString(), anyMap())).thenReturn(1);

		HolderDTO holder = new HolderDTO();
		AccountDTO account = new AccountDTO();
		account.setAccountNumber(100001L);
		account.setBalance(20000.0);
		holder.setAccount(account);
		holder.setName("Juan");
		holder.setLastName("Perez Updated");

		int rows = mcwnR101.executeUpdateAccount(holder);

		Assert.assertEquals(1, rows);
	}

	@Test
	public void executeUpdateAccountStatusHappyPath() {
		when(jdbcUtils.update(anyString(), anyMap())).thenReturn(1);

		int rows = mcwnR101.executeUpdateAccountStatus(100001L, 0);

		Assert.assertEquals(1, rows);
	}

	@Test
	public void executeUpdateAccountStatusZeroRows() {
		when(jdbcUtils.update(anyString(), anyMap())).thenReturn(0);

		int rows = mcwnR101.executeUpdateAccountStatus(999999L, 0);

		Assert.assertEquals(0, rows);
	}
}