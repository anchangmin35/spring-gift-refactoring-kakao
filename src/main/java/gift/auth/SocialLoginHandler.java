package gift.auth;

public interface SocialLoginHandler {
    SocialLoginResult login(String authorizationCode);
}
