package com.bbva.mcwn;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.mcwn.dto.holder.AccountDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import com.bbva.mcwn.lib.r100.MCWNR100;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public class MCWNT10201MXTransactionTest {

	private Map<String, Object> parameters;
	private Map<Class<?>, Object> serviceLibraries;

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private CommonRequestHeader commonRequestHeader;

	@Mock
	private MCWNR100 mcwnR100;

	private final MCWNT10201MXTransaction transaction = new MCWNT10201MXTransaction() {
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

	@Test
	public void executeHappyPathWithAccounts() {
		HolderDTO holder1 = new HolderDTO();
		holder1.setName("Juan");
		holder1.setLastName("Perez");
		holder1.setAge(30L);
		holder1.setCurp("JUAP900101HDFRRN01");
		holder1.setRfc("JUAP900101XXX");
		holder1.setClientType(0);

		AccountDTO account1 = new AccountDTO();
		account1.setAccountNumber(100001L);
		account1.setAccountNip(1256L);
		account1.setBalance(15000.5);
		account1.setAccountCard(4000412340000000L);
		account1.setStatus(true);
		holder1.setAccount(account1);

		HolderDTO holder2 = new HolderDTO();
		holder2.setName("Empresa SA");
		holder2.setRfc("EMP010101XX1");
		holder2.setClientType(1);

		AccountDTO account2 = new AccountDTO();
		account2.setAccountNumber(100002L);
		account2.setAccountNip(2222L);
		account2.setBalance(8000.0);
		account2.setAccountCard(4000412340000001L);
		account2.setStatus(true);
		holder2.setAccount(account2);

		List<HolderDTO> list = new ArrayList<>();
		list.add(holder1);
		list.add(holder2);

		when(mcwnR100.executeGetAllAccounts()).thenReturn(list);

		transaction.execute();

		Assert.assertNotNull(parameters.get("holders"));
	}

	@Test
	public void executeEmptyList() {
		when(mcwnR100.executeGetAllAccounts()).thenReturn(new ArrayList<>());

		transaction.execute();

		Assert.assertNotNull(parameters.get("holders"));
		Assert.assertEquals(0, ((List<?>) parameters.get("holders")).size());
	}
}