package com.bbva.mcwn.dto.holder;

public class HolderInDTO {
	private String name;
	private String lastName;
	private Long age;
	private String rfc;
	private String curp;
	private AccountInDTO account;

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getLastName() { return lastName; }
	public void setLastName(String lastName) { this.lastName = lastName; }

	public Long getAge() { return age; }
	public void setAge(Long age) { this.age = age; }

	public String getRfc() { return rfc; }
	public void setRfc(String rfc) { this.rfc = rfc; }

	public String getCurp() { return curp; }
	public void setCurp(String curp) { this.curp = curp; }

	public AccountInDTO getAccount() { return account; }
	public void setAccount(AccountInDTO account) { this.account = account; }
}