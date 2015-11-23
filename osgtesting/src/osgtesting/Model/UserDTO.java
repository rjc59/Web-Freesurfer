package osgtesting.Model;

public class UserDTO {
	private String userName;
	private String pass;
	private String name;
	private String surname;
	private String email;
	private String inst;
	private String phone;
	private String salt;
	private String id;
	
	public UserDTO(String id, String userName, String pass, String name, String surname,
			String email, String inst, String phone,String salt) {
		super();
		this.userName = userName;
		this.pass = pass;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.inst = inst;
		this.phone = phone;
		this.salt=salt;
		this.id = id;
	}
	public UserDTO() {
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getInst() {
		return inst;
	}
	public void setInst(String inst) {
		this.inst = inst;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}

}
