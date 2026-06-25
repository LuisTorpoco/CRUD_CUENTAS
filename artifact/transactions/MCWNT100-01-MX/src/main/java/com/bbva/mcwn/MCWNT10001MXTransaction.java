package com.bbva.mcwn;

import com.bbva.elara.domain.transaction.Advice;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.mcwn.dto.holder.*;
import com.bbva.mcwn.lib.r100.MCWNR100;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MCWNT10001MXTransaction extends AbstractMCWNT10001MXTransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(MCWNT10001MXTransaction.class);

	@Override
	public void execute() {
		HolderInDTO in = this.getHolder();

		String clientDocument = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CLIENTDOCUMENT);
		String aap = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.AAP);

		LOGGER.info("Headers recuperados -> client-document: '{}', AAP: '{}'", clientDocument, aap);

		MCWNR100 mcwnR100 = this.getServiceLibrary(MCWNR100.class);
		HolderDTO holderResponse = mcwnR100.executeGetMessage(mapHolder(in), clientDocument, aap);

		Advice advice = this.getAdvice();
		if (advice != null) {
			this.setSeverity(Severity.ENR);
		} else {
			this.setHolder(mapHolderOut(holderResponse));
		}

		LOGGER.info("Fin ejecución transacción");
	}

	private HolderDTO mapHolder(HolderInDTO in) {
		HolderDTO domainIn = new HolderDTO();
		if (in != null) {
			domainIn.setName(in.getName());
			domainIn.setLastName(in.getLastName());
			if (in.getAge() != null) {
				domainIn.setAge(in.getAge());
			}
			domainIn.setRfc(in.getRfc());
			domainIn.setCurp(in.getCurp());

			if (in.getAccount() != null) {
				AccountDTO accDom = new AccountDTO();
				if (in.getAccount().getAccountNumber() != null) {
					accDom.setAccountNumber(in.getAccount().getAccountNumber());
				}
				if (in.getAccount().getAccountNip() != null) {
					accDom.setAccountNip(in.getAccount().getAccountNip());
				}
				domainIn.setAccount(accDom);
			}
		}
		return domainIn;
	}

	private HolderOutDTO mapHolderOut(HolderDTO domainOut) {
		HolderOutDTO out = new HolderOutDTO();
		if (domainOut != null) {
			out.setName(domainOut.getName());
			out.setLastName(domainOut.getLastName());
			out.setAge(domainOut.getAge());
			out.setRfc(domainOut.getRfc());
			out.setCurp(domainOut.getCurp());

			if (domainOut.getAccount() != null) {
				AccountOutDTO accOut = new AccountOutDTO();
				accOut.setAccountNumber(domainOut.getAccount().getAccountNumber());
				accOut.setAccountNip(domainOut.getAccount().getAccountNip());
				out.setAccount(accOut);
			}
		}
		return out;
	}
}