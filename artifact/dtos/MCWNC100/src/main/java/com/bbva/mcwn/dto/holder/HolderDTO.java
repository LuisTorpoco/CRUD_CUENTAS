package com.bbva.mcwn.dto.holder;

public class HolderDTO {
	private String name;
	private String lastName;
	private Long age;
	private String curp;
	private String rfc;
	private Boolean isAdult;
	private AccountDTO account;

	public Long getAge() { return age; }
	public void setAge(Long age) { this.age = age; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }
	public String getCurp() { return curp; }
	public void setCurp(String curp) { this.curp = curp; }
	public String getRfc() { return rfc; }
	public void setRfc(String rfc) { this.rfc = rfc; }
	public Boolean getIsAdult() { return isAdult; }
	public void setIsAdult(Boolean isAdult) { this.isAdult = isAdult; }
	public AccountDTO getAccount() { return account; }
	public void setAccount(AccountDTO account) { this.account = account; }
}