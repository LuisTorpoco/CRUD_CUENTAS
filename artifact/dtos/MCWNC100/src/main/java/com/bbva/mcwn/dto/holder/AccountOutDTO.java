package com.bbva.mcwn.dto.holder;

public class AccountOutDTO {
	private Long accountNumber;
	private Long accountNip;
	private Double balance;
	private Long accountCard;
	private Boolean accountStatus;

	public Long getAccountNumber() { return accountNumber; }
	public void setAccountNumber(Long accountNumber) { this.accountNumber = accountNumber; }

	public Long getAccountNip() { return accountNip; }
	public void setAccountNip(Long accountNip) { this.accountNip = accountNip; }

	public Double getBalance() { return balance; }
	public void setBalance(Double balance) { this.balance = balance; }

	public Long getAccountCard() { return accountCard; }
	public void setAccountCard(Long accountCard) { this.accountCard = accountCard; }

	public Boolean getAccountStatus() { return accountStatus; }
	public void setAccountStatus(Boolean accountStatus) { this.accountStatus = accountStatus; }
}