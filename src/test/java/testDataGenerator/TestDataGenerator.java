package testDataGenerator;

import org.springframework.samples.parchisYOca.user.User;
import java.util.Random;
import java.util.stream.IntStream;

public class TestDataGenerator {
	
	private static final Integer Length =new Random().ints(10, 51).findFirst().getAsInt();
	private static final IntStream RND = new Random().ints(0, 27);
	
	
	
	
	public static User generateUser() {
		String chars = "aDaDSjsnojNuDuHHeSJdikjjabjt";
		User u = new User();
		String name = "";
		String password = "";
		for(int i = 0; i<Length; ++i) {
			name = name + chars.charAt(RND.findFirst().getAsInt());
			password = password + chars.charAt(RND.findFirst().getAsInt());
		}
		password = password +1;
		u.setUsername(name);
		u.setPassword(password);
		return u;

	}
	public static User generateUser(String name, String psswrd) {
		User u = new User();
		u.setUsername(name);
		u.setPassword(psswrd);
		return u;
	}
}
