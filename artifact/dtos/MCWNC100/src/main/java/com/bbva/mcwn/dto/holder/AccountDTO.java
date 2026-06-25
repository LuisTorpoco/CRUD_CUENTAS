package com.bbva.mcwn.dto.holder;

public class AccountDTO {
	private Long accountNumber;
	private Long accountNip;
	private Double balance;
	private Long accountCard;
	private Boolean status;

	public Long getAccountNumber() { return accountNumber; }
	public void setAccountNumber(Long accountNumber) { this.accountNumber = accountNumber; }
	public Long getAccountNip() { return accountNip; }
	public void setAccountNip(Long accountNip) { this.accountNip = accountNip; }
	public Double getBalance() { return balance; }
	public void setBalance(Double balance) { this.balance = balance; }
	public Long getAccountCard() { return accountCard; }
	public void setAccountCard(Long accountCard) { this.accountCard = accountCard; }
	public Boolean getStatus() { return status; }
	public void setStatus(Boolean status) { this.status = status; }
}