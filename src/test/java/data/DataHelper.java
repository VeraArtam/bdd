package data;

public class DataHelper {

    private DataHelper() {
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static String getVerificationCodeFor(AuthInfo authInfo) {
        return "12345";
    }
}