package com.bbva.mcwn.lib.r101.impl;

import com.bbva.elara.domain.transaction.Context;
import com.bbva.elara.domain.transaction.ThreadContext;
import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MCWNR101ImplTest {

	@Mock
	private ApplicationConfigurationService applicationConfigurationService;

	/* There are methods of the APX Architecture that require greater complexity to mock, for this reason
	 * an instance of the class to be tested can be created with the overwritten methods and on these
	 * methods the mocking of the classes is carried out, for example Header data
	 * (The Mocking the header is only for libraries that are used online, in batch it would not work)
	 *
	 * Import section:
	 * - import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
	 * - import com.bbva.elara.domain.transaction.request.header.CommonRequestHeader;
	 *
	 * Instance section:
	 * 	@Mock
	 *	private CommonRequestHeader commonRequestHeader;
	 *
	 *	@InjectMocks
	 *	private MCWNR101Impl mcwnR101 = new MCWNR101Impl() {
	 *		@Override
	 *		protected CommonRequestHeader getRequestHeader() {
	 *			return commonRequestHeader;
	 *		}
	 *	};
	 */
	@InjectMocks
	private MCWNR101Impl mcwnR101;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		ThreadContext.set(new Context());
	}

	@Test
	public void executeTest(){
		// when(commonRequestHeader.getHeaderParameter(RequestHeaderParamsName.COUNTRYCODE)).thenReturn("ES");
		// when(applicationConfigurationService.getProperty("config.property")).thenReturn("value");
		//mcwnR101.execute();
		Assert.assertEquals(0, mcwnR101.getAdviceList().size());
	}
}