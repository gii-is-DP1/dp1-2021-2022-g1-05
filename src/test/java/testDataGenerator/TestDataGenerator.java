package testDataGenerator;

import org.springframework.samples.parchisYOca.gooseBoard.GooseBoard;
import org.springframework.samples.parchisYOca.gooseMatch.GooseMatch;
import org.springframework.samples.parchisYOca.ludoBoard.LudoBoard;
import org.springframework.samples.parchisYOca.ludoMatch.LudoMatch;
import org.springframework.samples.parchisYOca.player.Player;
import org.springframework.samples.parchisYOca.playerGooseStats.PlayerGooseStats;
import org.springframework.samples.parchisYOca.playerLudoStats.PlayerLudoStats;
import org.springframework.samples.parchisYOca.user.User;

import java.util.Date;
import java.util.Random;
import java.util.Set;
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
	public static GooseMatch generateGooseLobby(String code) {
		GooseBoard board = new GooseBoard();
		GooseMatch match = new GooseMatch();
		match.setBoard(board);
		match.setStartDate(new Date());
		match.setMatchCode(code);
		return match;
	}
	public static Player generatePlayer(User user) {
		Player player = new Player();
		player.setUser(user);
		return player;
	}
	public static PlayerGooseStats generateGooseStats(Player player) {
		PlayerGooseStats stats = new PlayerGooseStats();
		stats.setPlayer(player);
		return stats;
	}
	public static GooseMatch generateGooseMatch(String code, Set<PlayerGooseStats> playerStats, Integer id) {
		GooseBoard board = new GooseBoard();
		GooseMatch match = new GooseMatch();
		match.setBoard(board);
		match.setClosedLobby(1);
		match.setStartDate(new Date());
		match.setMatchCode(code);
		match.setStats(null);
		match.setId(id);
		return match;
	}
	public static LudoMatch generateLudoMatch(String code) {
		LudoMatch match = new LudoMatch();
		LudoBoard board = new LudoBoard();
		match.setBoard(board);
		match.setMatchCode(code);
		match.setStartDate(new Date());
		return match;
	}
	public static PlayerLudoStats generatePlayerLudoStats(Player player) {
		PlayerLudoStats stats = new PlayerLudoStats();
		stats.setPlayer(player);
		stats.setIsOwner(1);
		return stats;
	}
}
