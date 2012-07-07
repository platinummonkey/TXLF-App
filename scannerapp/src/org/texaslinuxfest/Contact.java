package org.texaslinuxfest;

public class Contact {
	private long id;
	private String name;
	private String phone_work;
	private String phone_mobile;
	private String title;
	private String company;
	private String www;
	private String email;
	private String address;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public String[] getValues() {
		String[] vlist = {name, email, phone_work, phone_mobile, title, company,
				www, address};
		return vlist;
	}
	public void setValues(String name, String email, String phone_work,
			String phone_mobile, String title, String company, String www,
			String address) {
		this.name = name;
		this.email = email;
		this.phone_work = phone_work;
		this.phone_mobile = phone_mobile;
		this.title = title;
		this.company = company;
		this.www = www;
		this.address = address;
	}
}
