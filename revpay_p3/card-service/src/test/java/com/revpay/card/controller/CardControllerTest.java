package com.revpay.card.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revpay.card.dto.AddCardRequest;
import com.revpay.card.dto.CardResponse;
import com.revpay.card.service.CardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @Test
    void addCardReturnsCreated() throws Exception {
        AddCardRequest request = new AddCardRequest();
        request.setCardHolderName("Nikhil");
        request.setCardNumber("4111111111111111");
        request.setExpiryDate("12/28");
        request.setCvv("123");
        request.setPaymentMethodType("CREDIT");
        request.setSetAsDefault(true);

        CardResponse response = CardResponse.builder()
                .id(7L)
                .cardHolderName("Nikhil")
                .lastFourDigits("1111")
                .expiryDate("12/28")
                .cardType("VISA")
                .paymentMethodType("CREDIT")
                .isDefault(true)
                .build();

        when(cardService.addCard(eq(8L), any(AddCardRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/cards")
                        .header("X-User-Id", "8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.cardType").value("VISA"));
    }

    @Test
    void getCardsReturnsOk() throws Exception {
        CardResponse response = CardResponse.builder()
                .id(7L)
                .cardHolderName("Nikhil")
                .lastFourDigits("1111")
                .expiryDate("12/28")
                .cardType("VISA")
                .paymentMethodType("CREDIT")
                .isDefault(true)
                .build();

        when(cardService.getCards(8L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/cards")
                        .header("X-User-Id", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(7));
    }

    @Test
    void deleteCardReturnsOk() throws Exception {
        mockMvc.perform(delete("/api/v1/cards/7")
                        .header("X-User-Id", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Card deleted successfully"));

        verify(cardService).deleteCard(8L, 7L);
    }

    @Test
    void setDefaultCardReturnsOk() throws Exception {
        CardResponse response = CardResponse.builder()
                .id(7L)
                .cardHolderName("Nikhil")
                .lastFourDigits("1111")
                .expiryDate("12/28")
                .cardType("VISA")
                .paymentMethodType("CREDIT")
                .isDefault(true)
                .build();

        when(cardService.setDefaultCard(8L, 7L)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/cards/7/default")
                        .header("X-User-Id", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.default").value(true));
    }

    @Test
    void validateCardReturnsOk() throws Exception {
        when(cardService.validateCard(8L, 7L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/cards/7/validate")
                        .header("X-User-Id", "8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }
}
