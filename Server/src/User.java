public class User {
    String username;
    String encryptedPassword;

    public User(String username, String encryptedPassword) {
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }
}