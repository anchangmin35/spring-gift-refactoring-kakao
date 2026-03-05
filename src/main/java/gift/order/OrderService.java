package gift.order;

import gift.member.Member;
import gift.member.MemberRepository;
import gift.option.Option;
import gift.option.OptionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.NoSuchElementException;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OptionRepository optionRepository;
    private final MemberRepository memberRepository;
    private final OrderNotificationSender notificationSender;
    private final TransactionTemplate transactionTemplate;

    public OrderService(
        OrderRepository orderRepository,
        OptionRepository optionRepository,
        MemberRepository memberRepository,
        OrderNotificationSender notificationSender,
        TransactionTemplate transactionTemplate
    ) {
        this.orderRepository = orderRepository;
        this.optionRepository = optionRepository;
        this.memberRepository = memberRepository;
        this.notificationSender = notificationSender;
        this.transactionTemplate = transactionTemplate;
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrders(Long memberId, Pageable pageable) {
        return orderRepository.findByMemberId(memberId, pageable);
    }

    public Order createOrder(Long memberId, Long optionId, int quantity, String message) {
        OrderTransactionResult result = transactionTemplate.execute(status -> {
            Member member = findMember(memberId);
            Option option = findOption(optionId);

            option.subtractQuantity(quantity);
            member.deductPoint(calculatePrice(option, quantity));

            Order saved = orderRepository.save(new Order(option, memberId, quantity, message));
            return new OrderTransactionResult(member, saved, option);
        });

        notificationSender.send(result.member(), result.order(), result.option());
        return result.order();
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new NoSuchElementException("회원이 존재하지 않습니다. id=" + memberId));
    }

    private Option findOption(Long optionId) {
        return optionRepository.findById(optionId)
            .orElseThrow(() -> new NoSuchElementException("옵션이 존재하지 않습니다. id=" + optionId));
    }

    private int calculatePrice(Option option, int quantity) {
        return option.getProduct().getPrice() * quantity;
    }
}
