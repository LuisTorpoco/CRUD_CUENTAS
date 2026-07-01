package com.bbva.mcwn;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
import com.bbva.mcwn.dto.holder.AccountInDTO;
import com.bbva.mcwn.dto.holder.HolderInDTO;
import com.bbva.mcwn.dto.holder.HolderDTO;
import com.bbva.mcwn.dto.holder.AccountDTO;
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

public class MCWNT10301MXTransactionTest {

	private Map<String, Object> parameters;
	private Map<Class<?>, Object> serviceLibraries;

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	@Mock
	private CommonRequestHeader commonRequestHeader;

	@Mock
	private MCWNR100 mcwnR100;

	private final MCWNT10301MXTransaction transaction = new MCWNT10301MXTransaction() {
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

	private HolderInDTO buildHolderFisica() {
		HolderInDTO in = new HolderInDTO();
		in.setName("Juan");
		in.setLastName("Perez Updated");
		in.setAge(31L);
		in.setCurp("JUAP900101HDFRRN01");
		in.setRfc("JUAP900101XXX");

		AccountInDTO account = new AccountInDTO();
		account.setAccountNumber(100001L);
		account.setBalance(20000.0);
		in.setAccount(account);

		return in;
	}

	@Test
	public void executeInvalidClientType() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("9");
		setParameterToTransaction("holder", buildHolderFisica());

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeAccountNumberMissing() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		HolderInDTO in = buildHolderFisica();
		in.getAccount().setAccountNumber(null);
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeFisicaCurpInvalid() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		HolderInDTO in = buildHolderFisica();
		in.setCurp("CORTA");
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeFisicaRfcInvalid() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		HolderInDTO in = buildHolderFisica();
		in.setRfc("CORTO");
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeMoralCurpRecibida() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("1");
		HolderInDTO in = new HolderInDTO();
		in.setCurp("JUAP900101HDFRRN01");
		in.setRfc("EMP010101XX1");
		AccountInDTO account = new AccountInDTO();
		account.setAccountNumber(100001L);
		in.setAccount(account);
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeMoralRfcInvalid() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("1");
		HolderInDTO in = new HolderInDTO();
		in.setRfc("CORTO");
		AccountInDTO account = new AccountInDTO();
		account.setAccountNumber(100001L);
		in.setAccount(account);
		setParameterToTransaction("holder", in);

		transaction.execute();

		Assert.assertEquals(1, transaction.getAdviceList().size());
	}

	@Test
	public void executeUpdateLibraryFails() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");
		when(mcwnR100.executeUpdateAccount(any(HolderDTO.class))).thenReturn(null);
		setParameterToTransaction("holder", buildHolderFisica());

		transaction.execute();

		Assert.assertEquals(com.bbva.elara.domain.transaction.Severity.ENR, transaction.getSeverity());
	}

	@Test
	public void executeHappyPathFisica() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("0");

		HolderDTO updated = new HolderDTO();
		updated.setName("Juan");
		updated.setLastName("Perez Updated");
		updated.setAge(31L);
		updated.setCurp("JUAP900101HDFRRN01");
		updated.setRfc("JUAP900101XXX");

		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountNumber(100001L);
		accountDTO.setAccountNip(1256L);
		accountDTO.setBalance(20000.0);
		accountDTO.setAccountCard(4000412340000000L);
		accountDTO.setStatus(true);
		updated.setAccount(accountDTO);

		when(mcwnR100.executeUpdateAccount(any(HolderDTO.class))).thenReturn(updated);
		setParameterToTransaction("holder", buildHolderFisica());

		transaction.execute();

		Assert.assertNotNull(parameters.get("holder"));
	}

	@Test
	public void executeHappyPathMoral() {
		when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT)).thenReturn("1");

		HolderInDTO in = new HolderInDTO();
		in.setRfc("EMP010101XX1");
		AccountInDTO account = new AccountInDTO();
		account.setAccountNumber(100002L);
		account.setBalance(15000.0);
		in.setAccount(account);
		setParameterToTransaction("holder", in);

		HolderDTO updated = new HolderDTO();
		updated.setName("Empresa SA");
		updated.setLastName("NO_APLICA");
		updated.setAge(0L);
		updated.setCurp("NO_APLICA");
		updated.setRfc("EMP010101XX1");

		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountNumber(100002L);
		accountDTO.setAccountNip(2222L);
		accountDTO.setBalance(15000.0);
		accountDTO.setAccountCard(4000412340000001L);
		accountDTO.setStatus(true);
		updated.setAccount(accountDTO);

		when(mcwnR100.executeUpdateAccount(any(HolderDTO.class))).thenReturn(updated);

		transaction.execute();

		Assert.assertNotNull(parameters.get("holder"));
	}
}