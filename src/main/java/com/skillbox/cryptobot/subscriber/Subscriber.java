package com.skillbox.cryptobot.subscriber;

import lombok.Data;
import java.util.UUID;

@Data
public class Subscriber {

    private UUID id;
    private String userId;
    private Float currentPrice;

    public static class Fields {
        public static final String id = "id";
        public static final String userId = "user_id";
        public static final String currentPrice = "current_price";
    }


}
