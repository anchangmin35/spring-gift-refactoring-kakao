package gift.auth;

import org.springframework.stereotype.Component;

@Component
public class KakaoLoginHandler implements SocialLoginHandler {
    private final KakaoLoginClient kakaoLoginClient;

    public KakaoLoginHandler(KakaoLoginClient kakaoLoginClient) {
        this.kakaoLoginClient = kakaoLoginClient;
    }

    @Override
    public SocialLoginResult login(String authorizationCode) {
        KakaoLoginClient.KakaoTokenResponse token = kakaoLoginClient.requestAccessToken(authorizationCode);
        KakaoLoginClient.KakaoUserResponse user = kakaoLoginClient.requestUserInfo(token.accessToken());
        return new SocialLoginResult(user.email(), token.accessToken());
    }
}
