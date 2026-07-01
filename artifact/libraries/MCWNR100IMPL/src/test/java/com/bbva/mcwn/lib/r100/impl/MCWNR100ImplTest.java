package com.bbva.mcwn.lib.r100.impl;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import com.bbva.mcwn.lib.r101.MCWNR101;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class MCWNR100ImplTest {

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private MCWNR101 mcwnR101;


	@InjectMocks
	private MCWNR100Impl mcwnR100;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ThreadContext.set(new Context());
	}

	private HolderDTO buildHolder(Long accountNumber, Long nip, Boolean status) {
		HolderDTO holder = new HolderDTO();
		holder.setName("Juan");
		holder.setLastName("Perez");
		holder.setCurp("JUAP900101HDFRRN01");
		holder.setRfc("JUAP900101XXX");
		holder.setAge(30L);
		holder.setClientType(0);

		AccountDTO account = new AccountDTO();
		account.setAccountNumber(accountNumber);
		account.setAccountNip(nip);
		account.setBalance(15000.5);
		account.setAccountCard(4000412340000000L);
		account.setStatus(status);
		holder.setAccount(account);

		return holder;
	}

	// ---- executeGetAccount ----

	@Test
	public void executeGetAccountHappyPath() {
		HolderDTO holder = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(holder);

		AccountDTO request = new AccountDTO();
		request.setAccountNumber(100001L);
		request.setAccountNip(1256L);

		HolderDTO result = mcwnR100.executeGetAccount(request);

		Assert.assertNotNull(result);
		Assert.assertEquals("Juan", result.getName());
	}

	@Test
	public void executeGetAccountNotFound() {
		when(mcwnR101.executeGetAccountByNumber(999999L)).thenReturn(null);

		AccountDTO request = new AccountDTO();
		request.setAccountNumber(999999L);
		request.setAccountNip(1234L);

		HolderDTO result = mcwnR100.executeGetAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeGetAccountInactive() {
		HolderDTO holder = buildHolder(100001L, 1256L, false);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(holder);

		AccountDTO request = new AccountDTO();
		request.setAccountNumber(100001L);
		request.setAccountNip(1256L);

		HolderDTO result = mcwnR100.executeGetAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeGetAccountWrongNip() {
		HolderDTO holder = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(holder);

		AccountDTO request = new AccountDTO();
		request.setAccountNumber(100001L);
		request.setAccountNip(9999L);

		HolderDTO result = mcwnR100.executeGetAccount(request);

		Assert.assertNull(result);
	}

	// ---- executeGetAllAccounts ----

	@Test
	public void executeGetAllAccountsHappyPath() {
		java.util.List<HolderDTO> list = new java.util.ArrayList<>();
		list.add(buildHolder(100001L, 1256L, true));
		when(mcwnR101.executeGetAccounts()).thenReturn(list);

		java.util.List<HolderDTO> result = mcwnR100.executeGetAllAccounts();

		Assert.assertEquals(1, result.size());
	}

	// ---- executeCreateAccount ----

	@Test
	public void executeCreateAccountHappyPath() {
		when(mcwnR101.executeGetAccountByNumber(200002L))
				.thenReturn(null)
				.thenReturn(buildHolder(200002L, 9442L, true));
		when(mcwnR101.executeGetAccountByCard(1234567890123456L)).thenReturn(null);
		when(mcwnR101.executeInsertAccount(any(HolderDTO.class))).thenReturn(1);

		HolderDTO request = buildHolder(200002L, null, null);
		request.getAccount().setAccountCard(1234567890123456L);

		HolderDTO result = mcwnR100.executeCreateAccount(request);

		Assert.assertNotNull(result);
	}

	@Test
	public void executeCreateAccountActiveNumberExists() {
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(buildHolder(100001L, 1256L, true));

		HolderDTO request = buildHolder(100001L, null, null);

		HolderDTO result = mcwnR100.executeCreateAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeCreateAccountInactiveNumberExists() {
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(buildHolder(100001L, 1256L, false));

		HolderDTO request = buildHolder(100001L, null, null);

		HolderDTO result = mcwnR100.executeCreateAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeCreateAccountActiveCardExists() {
		when(mcwnR101.executeGetAccountByNumber(200002L)).thenReturn(null);
		when(mcwnR101.executeGetAccountByCard(4000412340000000L)).thenReturn(buildHolder(100001L, 1256L, true));

		HolderDTO request = buildHolder(200002L, null, null);
		request.getAccount().setAccountCard(4000412340000000L);

		HolderDTO result = mcwnR100.executeCreateAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeCreateAccountInactiveCardExists() {
		when(mcwnR101.executeGetAccountByNumber(200002L)).thenReturn(null);
		when(mcwnR101.executeGetAccountByCard(4000412340000000L)).thenReturn(buildHolder(100001L, 1256L, false));

		HolderDTO request = buildHolder(200002L, null, null);
		request.getAccount().setAccountCard(4000412340000000L);

		HolderDTO result = mcwnR100.executeCreateAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeCreateAccountInsertFails() {
		when(mcwnR101.executeGetAccountByNumber(200002L)).thenReturn(null);
		when(mcwnR101.executeGetAccountByCard(1234567890123456L)).thenReturn(null);
		when(mcwnR101.executeInsertAccount(any(HolderDTO.class))).thenReturn(0);

		HolderDTO request = buildHolder(200002L, null, null);
		request.getAccount().setAccountCard(1234567890123456L);

		HolderDTO result = mcwnR100.executeCreateAccount(request);

		Assert.assertNull(result);
	}

	// ---- executeUpdateAccount ----

	@Test
	public void executeUpdateAccountHappyPath() {
		HolderDTO existing = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);
		when(mcwnR101.executeUpdateAccount(any(HolderDTO.class))).thenReturn(1);

		HolderDTO request = buildHolder(100001L, null, null);
		request.getAccount().setAccountCard(null);

		HolderDTO result = mcwnR100.executeUpdateAccount(request);

		Assert.assertNotNull(result);
	}

	@Test
	public void executeUpdateAccountNotFound() {
		when(mcwnR101.executeGetAccountByNumber(999999L)).thenReturn(null);

		HolderDTO request = buildHolder(999999L, null, null);

		HolderDTO result = mcwnR100.executeUpdateAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeUpdateAccountInactive() {
		HolderDTO existing = buildHolder(100001L, 1256L, false);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);

		HolderDTO request = buildHolder(100001L, null, null);

		HolderDTO result = mcwnR100.executeUpdateAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeUpdateAccountCardAlreadyUsed() {
		HolderDTO existing = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);

		HolderDTO otherAccountWithCard = buildHolder(200002L, 9999L, true);
		when(mcwnR101.executeGetAccountByCard(5000000000000000L)).thenReturn(otherAccountWithCard);

		HolderDTO request = buildHolder(100001L, null, null);
		request.getAccount().setAccountCard(5000000000000000L);

		HolderDTO result = mcwnR100.executeUpdateAccount(request);

		Assert.assertNull(result);
	}

	@Test
	public void executeUpdateAccountUpdateFails() {
		HolderDTO existing = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);
		when(mcwnR101.executeUpdateAccount(any(HolderDTO.class))).thenReturn(0);

		HolderDTO request = buildHolder(100001L, null, null);
		request.getAccount().setAccountCard(null);

		HolderDTO result = mcwnR100.executeUpdateAccount(request);

		Assert.assertNull(result);
	}

	// ---- executeDeleteAccount ----

	@Test
	public void executeDeleteAccountHappyPath() {
		HolderDTO existing = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);
		when(mcwnR101.executeUpdateAccountStatus(100001L, 0)).thenReturn(1);

		int rows = mcwnR100.executeDeleteAccount(100001L);

		Assert.assertEquals(1, rows);
	}

	@Test
	public void executeDeleteAccountNotFound() {
		when(mcwnR101.executeGetAccountByNumber(999999L)).thenReturn(null);

		int rows = mcwnR100.executeDeleteAccount(999999L);

		Assert.assertEquals(0, rows);
	}

	@Test
	public void executeDeleteAccountAlreadyInactive() {
		HolderDTO existing = buildHolder(100001L, 1256L, false);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);

		int rows = mcwnR100.executeDeleteAccount(100001L);

		Assert.assertEquals(0, rows);
	}

	@Test
	public void executeDeleteAccountUpdateFails() {
		HolderDTO existing = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);
		when(mcwnR101.executeUpdateAccountStatus(100001L, 0)).thenReturn(0);

		int rows = mcwnR100.executeDeleteAccount(100001L);

		Assert.assertEquals(0, rows);
	}

	// ---- executeReactivateAccount ----

	@Test
	public void executeReactivateAccountHappyPath() {
		HolderDTO existing = buildHolder(100001L, 1256L, false);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);
		when(mcwnR101.executeUpdateAccountStatus(100001L, 1)).thenReturn(1);

		int rows = mcwnR100.executeReactivateAccount(100001L);

		Assert.assertEquals(1, rows);
	}

	@Test
	public void executeReactivateAccountNotFound() {
		when(mcwnR101.executeGetAccountByNumber(999999L)).thenReturn(null);

		int rows = mcwnR100.executeReactivateAccount(999999L);

		Assert.assertEquals(0, rows);
	}

	@Test
	public void executeReactivateAccountAlreadyActive() {
		HolderDTO existing = buildHolder(100001L, 1256L, true);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);

		int rows = mcwnR100.executeReactivateAccount(100001L);

		Assert.assertEquals(0, rows);
	}

	@Test
	public void executeReactivateAccountUpdateFails() {
		HolderDTO existing = buildHolder(100001L, 1256L, false);
		when(mcwnR101.executeGetAccountByNumber(100001L)).thenReturn(existing);
		when(mcwnR101.executeUpdateAccountStatus(100001L, 1)).thenReturn(0);

		int rows = mcwnR100.executeReactivateAccount(100001L);

		Assert.assertEquals(0, rows);
	}

	// ---- executeGetMessage ----

	@Test
	public void executeGetMessageReturnsNull() {
		HolderDTO result = mcwnR100.executeGetMessage(new HolderDTO(), "0", "01");
		Assert.assertNull(result);
	}
}