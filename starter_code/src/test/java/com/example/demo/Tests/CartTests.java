package com.example.demo.Tests;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartTests {

    private CartController cartController;

    private UserRepository userRepository;

    private ItemRepository itemRepository;

    private CartRepository cartRepository;

    public void setup() {
        userRepository = mock(UserRepository.class);
        cartRepository = mock(CartRepository.class);
        itemRepository = mock(ItemRepository.class);
        cartController = new CartController(userRepository, cartRepository, itemRepository);
        setupCart();
    }

    private void setupCart() {
        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);

        Item item = new Item();
        item.setId(1L);
        item.setName("Americano");
        BigDecimal price = BigDecimal.valueOf(2.99);
        item.setPrice(price);
        item.setDescription("Espresso with water");
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
    }

    @Test
    public void addToCart_Successful() {
        setup();
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test");

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(200));
        Cart cart = response.getBody();
        assertThat(cart, notNullValue());
        assertThat(cart.getTotal(), comparesEqualTo(BigDecimal.valueOf(5.98)));
    }

    @Test
    public void addToCart_WrongUser() {
        setup();
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("username");

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(404));
    }

    @Test
    public void addToCart_ItemNotFound() {
        setup();
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("test");

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(404));
    }

    @Test
    public void removeFromCart_Successful() {
        setup();
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test");

        ResponseEntity<Cart> response = cartController.addToCart(modifyCartRequest);
        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(200));

        modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("test");

        response = cartController.removeFromCart(modifyCartRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(200));
        Cart cart = response.getBody();
        assertThat(cart, notNullValue());
        assertThat(cart.getTotal(), comparesEqualTo(BigDecimal.valueOf(2.99)));
    }

    @Test
    public void removeFromCart_InvalidUser() {
        setup();
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("username");

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(404));
    }

    @Test
    public void removeFromCart_InvalidItem() {
        setup();
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("test");

        ResponseEntity<Cart> response = cartController.removeFromCart(modifyCartRequest);

        assertThat(response, notNullValue());
        assertThat(response.getStatusCodeValue(), equalTo(404));
    }

}
