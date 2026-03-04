package gift.order;

import gift.member.Member;
import gift.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KakaoNotificationSender implements OrderNotificationSender {
    private static final Logger log = LoggerFactory.getLogger(KakaoNotificationSender.class);

    private final KakaoMessageClient kakaoMessageClient;

    public KakaoNotificationSender(KakaoMessageClient kakaoMessageClient) {
        this.kakaoMessageClient = kakaoMessageClient;
    }

    @Override
    public void send(Member member, Order order, Option option) {
        if (member.getSocialAccessToken() == null) {
            return;
        }
        try {
            kakaoMessageClient.sendToMe(member.getSocialAccessToken(), order, option.getProduct());
        } catch (Exception e) {
            log.warn("카카오 메시지 전송 실패: memberId={}", member.getId(), e);
        }
    }
}
