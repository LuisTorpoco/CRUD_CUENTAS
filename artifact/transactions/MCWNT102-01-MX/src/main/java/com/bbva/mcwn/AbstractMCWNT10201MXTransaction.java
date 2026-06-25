package com.bbva.mcwn;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.mcwn.dto.holder.HolderOutDTO;
import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractMCWNT10201MXTransaction extends AbstractTransaction {

	protected AbstractMCWNT10201MXTransaction(){
	}


	/**
	 * Set value for List<HolderOutDTO> output parameter holders
	 */
	protected void setHolders(final List<HolderOutDTO> field){
		this.addParameter("holders", field);
	}
}
