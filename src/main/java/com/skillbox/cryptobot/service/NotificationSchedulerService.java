package com.skillbox.cryptobot.service;

import com.skillbox.cryptobot.bot.CryptoBot;
import com.skillbox.cryptobot.subscriber.Subscriber;
import com.skillbox.cryptobot.subscriber.SubscriberRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeoutException;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationSchedulerService {

    @Autowired
    private SubscriberRepository repository;

    @Autowired
    private CryptoBot cryptoBot;

    private final CryptoCurrencyService service;

    @Scheduled(fixedRateString = "PT10M")
    public void notificationPriceMath(){
        try{
            List<Subscriber> subscriberList = repository.findAll();
            float currentPrice = (float) service.getBitcoinPrice();
            subscriberList.forEach(e -> {
                if (priceMatching(e.getCurrentPrice(), currentPrice, 10)) {
                    SendMessage message = new SendMessage();
                    message.setChatId(e.getUserId());
                    message.setText(MessageFormat.format("Пора покупать, стоимость биткоина {0} USD", currentPrice));
                    try {
                        cryptoBot.execute(message);
                    } catch (TelegramApiException ex) {
                        log.error("Error sending message "+ex.getMessage());
                    }
                }
            });
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }

    public static boolean priceMatching(double a, double b, double range) {
        double min = Math.min(b - range, b + range);
        double max = Math.max(b - range, b + range);
        return a >= min && a <= max;
    }

}
