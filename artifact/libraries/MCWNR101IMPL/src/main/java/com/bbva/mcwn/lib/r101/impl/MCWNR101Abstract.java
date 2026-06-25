package com.bbva.mcwn.lib.r101.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.jdbc.JdbcUtils;
import com.bbva.mcwn.lib.r101.MCWNR101;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class MCWNR101Abstract extends AbstractLibrary implements MCWNR101 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected JdbcUtils jdbcUtils;


	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	public void setJdbcUtils(JdbcUtils jdbcUtils) {
		this.jdbcUtils = jdbcUtils;
	}

}