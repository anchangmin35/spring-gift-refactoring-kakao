package gift.order;

import gift.member.Member;
import gift.member.MemberRepository;
import gift.option.Option;
import gift.option.OptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final MemberRepository memberRepository;
    private final OrderNotificationSender notificationSender;

    public OrderService(
        OrderRepository orderRepository,
        OptionRepository optionRepository,
        MemberRepository memberRepository,
        OrderNotificationSender notificationSender
    ) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.memberRepository = memberRepository;
        this.notificationSender = notificationSender;
    }

    public Page<Order> getOrders(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable);
    }

    @Transactional
    public Order createOrder(Long memberId, Long optionId, int quantity, String message) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다. id=" + memberId));
        Option option = optionRepository.findById(optionId)
            .orElseThrow(() -> new NoSuchElementException("옵션이 존재하지 않습니다. id=" + optionId));

        option.subtractQuantity(quantity);

        int price = option.getProduct().getPrice() * quantity;
        member.deductPoint(price);

        Order saved = orderRepository.save(new Order(option, memberId, quantity, message));

        notificationSender.send(member, saved, option);
        return saved;
    }
}
