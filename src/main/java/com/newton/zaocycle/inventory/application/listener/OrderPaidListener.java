package com.newton.zaocycle.inventory.application.listener;

import com.newton.zaocycle.inventory.domain.model.BriquetteBatch;
import com.newton.zaocycle.inventory.domain.model.BuyerOrder;
import com.newton.zaocycle.inventory.domain.port.BriquetteBatchRepository;
import com.newton.zaocycle.inventory.domain.port.BuyerOrderRepository;
import com.newton.zaocycle.payment.application.event.OrderPaid;
import com.newton.zaocycle.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.util.List;

@Component("inventoryOrderPaidListener")
public class OrderPaidListener {

    private static final Logger log = LoggerFactory.getLogger(OrderPaidListener.class);

    private final BuyerOrderRepository orderRepository;
    private final BriquetteBatchRepository batchRepository;

    public OrderPaidListener(BuyerOrderRepository orderRepository,
                             BriquetteBatchRepository batchRepository) {
        this.orderRepository = orderRepository;
        this.batchRepository = batchRepository;
    }

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void on(OrderPaid event) {
        BuyerOrder order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new NotFoundException("Order not found: " + event.orderId()));
        order.markPaid(event.mpesaTransactionId());
        orderRepository.save(order);
        deductFromBatches(order.totalKg());
    }

    private void deductFromBatches(BigDecimal kgNeeded) {
        List<BriquetteBatch> available = batchRepository.findWithRemainingStockOrderedByOldest();
        BigDecimal remaining = kgNeeded;
        for (BriquetteBatch batch : available) {
            if (remaining.signum() <= 0) break;
            BigDecimal take = batch.kgRemaining().min(remaining);
            batch.deduct(take);
            batchRepository.save(batch);
            remaining = remaining.subtract(take);
        }
        if (remaining.signum() > 0) {
            // Stock shortfall after payment — ops must investigate and reconcile manually
            log.error("STOCK SHORTFALL: {} kg short after payment for order", remaining);
        }
    }
}
