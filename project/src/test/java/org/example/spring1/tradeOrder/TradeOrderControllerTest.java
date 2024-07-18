package org.example.spring1.tradeOrder;

import org.example.spring1.exception.InsufficientFundsException;
import org.example.spring1.security.JwtUtilsService;
import org.example.spring1.tradeOrder.model.dto.TradeMessageDTO;
import org.example.spring1.tradeOrder.model.dto.TradeOrderDTO;
import org.example.spring1.user.UserDetailsImplService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(TradeOrderController.class)
@ExtendWith(MockitoExtension.class)
public class TradeOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeOrderService tradeOrderService;

    @MockBean
    private JwtUtilsService jwtUtilsService;

    @MockBean
    private UserDetailsImplService userDetailsImplService;

    private TradeOrderDTO tradeOrderDTO;

    @BeforeEach
    void setUp() {
        tradeOrderDTO = new TradeOrderDTO();
        tradeOrderDTO.setSymbol("BTC");
        tradeOrderDTO.setQuantity(BigDecimal.valueOf(2));
        tradeOrderDTO.setPrice(BigDecimal.valueOf(50000));
        tradeOrderDTO.setOrderType("BUY");
    }

    @Test
    @WithMockUser
    public void createTradeOrder_InsufficientFunds() throws Exception {
        when(tradeOrderService.getCurrentUserId()).thenReturn(1L);
        when(tradeOrderService.createTradeOrderFromDTO(anyLong(), any(TradeOrderDTO.class)))
                .thenThrow(new InsufficientFundsException("Insufficient funds to place order"));

        mockMvc.perform(post("/api/tradeOrders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"symbol\":\"BTC\",\"quantity\":\"2\",\"price\":\"50000\",\"orderType\":\"BUY\"}"))
                .andExpect(status().is4xxClientError());
    }
}
