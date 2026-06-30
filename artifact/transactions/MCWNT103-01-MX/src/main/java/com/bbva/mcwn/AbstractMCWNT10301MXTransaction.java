package com.bbva.mcwn;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.mcwn.dto.holder.HolderInDTO;
import com.bbva.mcwn.dto.holder.HolderOutDTO;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractMCWNT10301MXTransaction extends AbstractTransaction {

	protected AbstractMCWNT10301MXTransaction(){
	}


	/**
	 * Return value for input parameter holder
	 */
	protected HolderInDTO getHolder(){
		return (HolderInDTO)this.getParameter("holder");
	}

	/**
	 * Set value for HolderOutDTO output parameter holder
	 */
	protected void setHolder(final HolderOutDTO field){
		this.addParameter("holder", field);
	}
}
