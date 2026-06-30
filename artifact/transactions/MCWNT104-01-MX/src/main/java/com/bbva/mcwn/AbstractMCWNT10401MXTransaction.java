package com.bbva.mcwn;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.mcwn.dto.holder.AccountInDTO;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractMCWNT10401MXTransaction extends AbstractTransaction {

	protected AbstractMCWNT10401MXTransaction(){
	}


	/**
	 * Return value for input parameter account
	 */
	protected AccountInDTO getAccount(){
		return (AccountInDTO)this.getParameter("account");
	}
}
