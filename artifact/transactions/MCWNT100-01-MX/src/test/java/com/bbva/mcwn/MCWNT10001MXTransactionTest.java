package com.bbva.mcwn;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.mcwn.dto.holder.AccountInDTO;
import com.bbva.mcwn.dto.holder.HolderInDTO;
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

public class MCWNT10001MXTransactionTest {

	private Map<String, Object> parameters;
	private Map<Class<?>, Object> serviceLibraries;

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private CommonRequestHeader commonRequestHeader;

	@Mock
	private MCWNR100 mcwnR100;

	private final MCWNT10001MXTransaction transaction = new MCWNT10001MXTransaction() {
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

	private HolderInDTO buildHolderIn() {
		HolderInDTO in = new HolderInDTO();
		in.setName("Pedro");
		in.setLastName("Garcia");
		in.setAge(25L);
		in.setCurp("GAGP010101HDFRCR01");
		in.setRfc("GAGP010101AB3");

		AccountInDTO account = new AccountInDTO();
		account.setAccountNumber(200002L);
		account.setBalance(5000.0);
		account.setAccountCard(1234567890123456L);
		in.setAccount(account);

		return in;
	}

	@Test
	public void executeInvalidClientType() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("9");
		setParameterToTransaction("holder", buildHolderIn());

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeFisicaMissingFields() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		HolderInDTO in = buildHolderIn();
		in.setName(null);
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeFisicaCurpInvalid() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		HolderInDTO in = buildHolderIn();
		in.setCurp("CORTA");
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeFisicaRfcInvalid() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		HolderInDTO in = buildHolderIn();
		in.setRfc("CORTO");
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeMoralMissingRfc() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("1");
		HolderInDTO in = buildHolderIn();
		in.setRfc(null);
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeMoralRfcInvalid() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("1");
		HolderInDTO in = buildHolderIn();
		in.setRfc("CORTO");
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeBalanceExceedsMax() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		when(applicationConfigurationService.getProperty("MAX.BALANCE")).thenReturn("9000");
		HolderInDTO in = buildHolderIn();
		in.getAccount().setBalance(50000.0);
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeCreateLibraryFails() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		when(mcwnR100.executeCreateAccount(any(HolderDTO.class))).thenReturn(null);
		setParameterToTransaction("holder", buildHolderIn());

		transaction.execute();

		Assert.assertEquals(com.bbva.elara.domain.transaction.Severity.ENR, transaction.getSeverity());
	}

	@Test
	public void executeHappyPath() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");

		HolderDTO created = new HolderDTO();
		created.setName("Pedro");
		created.setLastName("Garcia");
		created.setAge(25L);
		created.setCurp("GAGP010101HDFRCR01");
		created.setRfc("GAGP010101AB3");

		com.bbva.mcwn.dto.holder.AccountDTO accountDTO = new com.bbva.mcwn.dto.holder.AccountDTO();
		accountDTO.setAccountNumber(200002L);
		accountDTO.setAccountNip(9442L);
		accountDTO.setBalance(5000.0);
		accountDTO.setAccountCard(1234567890123456L);
		accountDTO.setStatus(true);
		created.setAccount(accountDTO);

		when(mcwnR100.executeCreateAccount(any(HolderDTO.class))).thenReturn(created);
		setParameterToTransaction("holder", buildHolderIn());

		transaction.execute();

		Assert.assertNotNull(parameters.get("holder"));
	}
}