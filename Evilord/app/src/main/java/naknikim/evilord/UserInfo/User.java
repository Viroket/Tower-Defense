package naknikim.evilord.UserInfo;

/**
 * Created by irenus on 3/16/2017.
 */

public class User {
    public int gold;
    private long bestTimeScore;

    public User(int gold) {
        this.gold = gold;
        bestTimeScore = -1;
    }
}
