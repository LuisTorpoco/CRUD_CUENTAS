package com.bbva.mcwn.lib.r100.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.mcwn.lib.r100.MCWNR100;
import com.bbva.mcwn.lib.r101.MCWNR101;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class MCWNR100Abstract extends AbstractLibrary implements MCWNR100 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected MCWNR101 mcwnR101;


	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	public void setMcwnR101(MCWNR101 mcwnR101) {
		this.mcwnR101 = mcwnR101;
	}

}