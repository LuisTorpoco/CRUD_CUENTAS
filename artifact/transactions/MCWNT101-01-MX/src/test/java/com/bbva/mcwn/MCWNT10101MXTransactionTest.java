package com.bbva.mcwn;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.AccountInDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import com.bbva.mcwn.lib.r100.MCWNR100;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class MCWNT10101MXTransactionTest {

	private Map<String, Object> parameters;
	private Map<Class<?>, Object> serviceLibraries;

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private CommonRequestHeader commonRequestHeader;

	@Mock
	private MCWNR100 mcwnR100;

	private final MCWNT10101MXTransaction transaction = new MCWNT10101MXTransaction() {
		@Override
		protected void addParameter(String field, Object obj) {
			if (parameters != null) {
				parameters.put(field, obj);
			}
		}

		@Override
		protected Object getParameter(String field) {
			return parameters.get(field);
		}

		@Override
		protected <T> T getServiceLibrary(Class<T> serviceInterface) {
			return (T) serviceLibraries.get(serviceInterface);
		}

		@Override
		public String getProperty(String keyProperty) {
			return applicationConfigurationService.getProperty(keyProperty);
		}

		@Override
		protected CommonRequestHeader getRequestHeader() {
			return commonRequestHeader;
		}
	};

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		initializeTransaction();
		setServiceLibrary(MCWNR100.class, mcwnR100);
	}

	private void initializeTransaction() {
		transaction.setContext(new Context());
		parameters = new HashMap<>();
		serviceLibraries = new HashMap<>();
	}

	private void setServiceLibrary(Class<?> clasz, Object mockObject) {
		serviceLibraries.put(clasz, mockObject);
	}

	private void setParameterToTransaction(String parameter, Object value) {
		parameters.put(parameter, value);
	}

	@Test
	public void executeAccountNull() {
		setParameterToTransaction("account", null);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeAccountNumberNull() {
		AccountInDTO accountIn = new AccountInDTO();
		accountIn.setAccountNip(1256L);
		setParameterToTransaction("account", accountIn);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeAccountNipNull() {
		AccountInDTO accountIn = new AccountInDTO();
		accountIn.setAccountNumber(100001L);
		setParameterToTransaction("account", accountIn);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeLibraryReturnsNull() {
		AccountInDTO accountIn = new AccountInDTO();
		accountIn.setAccountNumber(100001L);
		accountIn.setAccountNip(1256L);
		setParameterToTransaction("account", accountIn);

		when(mcwnR100.executeGetAccount(any(AccountDTO.class))).thenReturn(null);

		transaction.execute();

		Assert.assertNull(parameters.get("holder"));
	}

	@Test
	public void executeHappyPath() {
		AccountInDTO accountIn = new AccountInDTO();
		accountIn.setAccountNumber(100001L);
		accountIn.setAccountNip(1256L);
		setParameterToTransaction("account", accountIn);

		HolderDTO holderDTO = new HolderDTO();
		holderDTO.setName("Juan");
		holderDTO.setLastName("Perez");
		holderDTO.setAge(30L);
		holderDTO.setCurp("JUAP900101HDFRRN01");
		holderDTO.setRfc("JUAP900101XXX");
		holderDTO.setClientType(0);

		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountNumber(100001L);
		accountDTO.setAccountNip(1256L);
		accountDTO.setBalance(15000.5);
		accountDTO.setAccountCard(4000412340000000L);
		accountDTO.setStatus(true);
		holderDTO.setAccount(accountDTO);

		when(mcwnR100.executeGetAccount(any(AccountDTO.class))).thenReturn(holderDTO);

		transaction.execute();

		Assert.assertNotNull(parameters.get("holder"));
	}

	@Test
	public void executeHappyPathMoral() {
		AccountInDTO accountIn = new AccountInDTO();
		accountIn.setAccountNumber(100002L);
		accountIn.setAccountNip(2222L);
		setParameterToTransaction("account", accountIn);

		HolderDTO holderDTO = new HolderDTO();
		holderDTO.setName("Empresa SA");
		holderDTO.setRfc("EMP010101XX1");
		holderDTO.setClientType(1);

		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountNumber(100002L);
		accountDTO.setAccountNip(2222L);
		accountDTO.setBalance(8000.0);
		accountDTO.setAccountCard(4000412340000001L);
		accountDTO.setStatus(true);
		holderDTO.setAccount(accountDTO);

		when(mcwnR100.executeGetAccount(any(AccountDTO.class))).thenReturn(holderDTO);

		transaction.execute();

		Assert.assertNotNull(parameters.get("holder"));
	}
}