package com.bbva.mcwn;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.mcwn.dto.holder.AccountInDTO;
import com.bbva.mcwn.lib.r100.MCWNR100;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class MCWNT10501MXTransactionTest {

	private Map<String, Object> parameters;
	private Map<Class<?>, Object> serviceLibraries;

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private CommonRequestHeader commonRequestHeader;

	@Mock
	private MCWNR100 mcwnR100;

	private final MCWNT10501MXTransaction transaction = new MCWNT10501MXTransaction() {
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
		setParameterToTransaction("account", accountIn);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeLibraryFails() {
		AccountInDTO accountIn = new AccountInDTO();
		accountIn.setAccountNumber(200003L);
		setParameterToTransaction("account", accountIn);

		when(mcwnR100.executeReactivateAccount(200003L)).thenReturn(0);

		transaction.execute();

		Assert.assertNotNull(accountIn);
	}

	@Test
	public void executeHappyPath() {
		AccountInDTO accountIn = new AccountInDTO();
		accountIn.setAccountNumber(200003L);
		setParameterToTransaction("account", accountIn);

		when(mcwnR100.executeReactivateAccount(200003L)).thenReturn(1);

		transaction.execute();

		Assert.assertEquals(0, transaction.getAdviceList().size());
	}
}