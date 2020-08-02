package UnitaryTests;

import es.codeurjc.shop.domain.ShopException;
import es.codeurjc.shop.domain.customer.Customer;
import es.codeurjc.shop.domain.customer.CustomerCreditLimitExceededException;
import es.codeurjc.shop.domain.customer.CustomerService;
import es.codeurjc.shop.domain.product.Product;
import es.codeurjc.shop.domain.product.ProductService;
import es.codeurjc.shop.domain.product.ProductStockWithdrawExceededException;
import es.codeurjc.shop.domain.purchase.Purchase;
import es.codeurjc.shop.domain.purchase.PurchaseRepository;
import es.codeurjc.shop.domain.purchase.PurchaseService;
import es.codeurjc.shop.notification.NotificationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PurchaseServiceUnitaryTest {

    @Test
    public void createPurchaseHasCreditAndStockTest() throws ShopException {
        CustomerService custService = mock(CustomerService.class);
        ProductService prodService = mock(ProductService.class);
        PurchaseRepository purRepository = mock(PurchaseRepository.class);
        NotificationService notiService = mock(NotificationService.class);

        long customerId = 1;
        long productId = 1;

        PurchaseService purchService = new PurchaseService(purRepository, custService, prodService, notiService);

        when(custService.getCustomer(customerId))
                .thenReturn(new Customer("TestCustomer", 5000));

        when(prodService.getProduct(productId))
                .thenReturn(new Product("TestProduct", 20, 2));

        when(prodService.getProductCost(productId))
                .thenReturn(Long.valueOf(20));

        doNothing().when(custService).reserveCredit(customerId, 20);
        doNothing().when(notiService).notify(anyString());

        Purchase purchase = new Purchase(customerId, productId);

        when(purRepository.save(purchase))
                .thenReturn(purchase);

        purchService.createPurchase(customerId, productId);

        verify(notiService, times(1)).notify(anyString());
        assertDoesNotThrow(() -> purchService.createPurchase(customerId, productId));
    }

    @Test
    public void notCreatePurchaseWithNoCredit() throws ShopException {
        CustomerService custService = mock(CustomerService.class);
        ProductService prodService = mock(ProductService.class);
        PurchaseRepository purRepository = mock(PurchaseRepository.class);
        NotificationService notiService = mock(NotificationService.class);

        PurchaseService purchService = new PurchaseService(purRepository, custService, prodService, notiService);

        long customerId = 1;
        long productId = 1;

        when(custService.getCustomer(customerId))
                .thenReturn(new Customer("TestCustomer", 0));

        when(prodService.getProduct(productId))
                .thenReturn(new Product("TestProduct", 20, 2));

        when(prodService.getProductCost(productId))
                .thenReturn(Long.valueOf(20));

        doNothing().when(prodService).withdrawProduct(productId);

        doThrow(CustomerCreditLimitExceededException.class).when(custService).reserveCredit(1, 20);

        assertThrows(CustomerCreditLimitExceededException.class, () -> purchService.createPurchase(customerId, productId));
    }

    @Test
    public void notCreatePurchaseWithNoStock() throws ShopException {
        CustomerService custService = mock(CustomerService.class);
        ProductService prodService = mock(ProductService.class);
        PurchaseRepository purRepository = mock(PurchaseRepository.class);
        NotificationService notiService = mock(NotificationService.class);

        PurchaseService purchService = new PurchaseService(purRepository, custService, prodService, notiService);

        long customerId = 1;
        long productId = 1;

        when(custService.getCustomer(customerId))
                .thenReturn(new Customer("TestCustomer", 300));

        when(prodService.getProduct(productId))
                .thenReturn(new Product("TestProduct", 20, 0));

        when(prodService.getProductCost(productId))
                .thenReturn(Long.valueOf(20));

        doNothing().when(prodService).withdrawProduct(productId);

        doThrow(ProductStockWithdrawExceededException.class).when(prodService).withdrawProduct(productId);

        assertThrows(ProductStockWithdrawExceededException.class, () -> purchService.createPurchase(customerId, productId));
    }
}
