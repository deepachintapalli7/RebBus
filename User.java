package busbooking;

public class User {
	int userId;
	int age;
	String name;
	String email;
	String phone;
    User(int userId, String name, int age, String phone, String email) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.email = email;
    }
}
