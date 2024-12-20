package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.service.CryptoCurrencyService;
import com.skillbox.cryptobot.subscriber.Subscriber;
import com.skillbox.cryptobot.subscriber.SubscriberRepository;
import com.skillbox.cryptobot.utils.TextUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class SubscribeCommand implements IBotCommand {

    @Autowired
    private SubscriberRepository subscriberRepository;

    private final CryptoCurrencyService service;

    @Override
    public String getCommandIdentifier() {
        return "subscribe";
    }

    @Override
    public String getDescription() {
        return "Подписывает пользователя на стоимость биткоина";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        StringBuilder messageOutBuilder = new StringBuilder();
        try {
            if (subscriberExists(message)) {
                if (getSubscriberPrice(message) == Float.parseFloat(arguments[0])) {
                    getPriceProcess(absSender, message);
                    oldSubscriptionAction(messageOutBuilder, arguments[0]);
                } else {
                    getPriceProcess(absSender, message);
                    newSubscriptionAction(messageOutBuilder, arguments[0]);
                }
            } else {
                newSubscriptionAction(messageOutBuilder, arguments[0]);
            }
            writeToBaseData(message, Float.parseFloat(arguments[0]));
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
            parsingMistakeMessage(messageOutBuilder);
        }
        try {
            answer.setText(messageOutBuilder.toString());
            absSender.execute(answer);
        } catch (TelegramApiException ex) {
            log.error(ex.getMessage());
        }
    }

    private void newSubscriptionAction(StringBuilder messageOutBuilder, String argument) {
        messageOutBuilder.append(
                String.format(
                        "Новая подписка создана на стоимость: %.2f USD",
                        Float.parseFloat(argument)));
    }

    private void oldSubscriptionAction(StringBuilder messageOutBuilder, String argument) {
        messageOutBuilder.append(
                String.format(
                        "Вы подписаны на стоимость биткоина: %.2f USD",
                        Float.parseFloat(argument)));
    }

    private void parsingMistakeMessage(StringBuilder messageOutBuilder) {
        messageOutBuilder.append("""
                    Используйте: /subscribe [порог_цены] \n
                    Посмотрите текущую цену биткоина /get_price
                    """);
    }

    private void writeToBaseData(Message message, Float data) {
        new Thread(() -> subscriberRepository.replaceCurrentPrice(message.getChatId().toString(), data)).start();
    }

    private void getPriceProcess(AbsSender absSender, Message message) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        try {
            answer.setText("Текущая цена биткоина " + TextUtil.toString(service.getBitcoinPrice()) + " USD");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean subscriberExists(Message message) {
        Optional<Subscriber> subscriber = subscriberRepository.findByUserId(
                message.getChatId().toString());
        return subscriber.filter(value -> value.getCurrentPrice() != null).isPresent();
    }

    private float getSubscriberPrice(Message message) {
        return subscriberRepository.findByUserId(
                message.getChatId().toString()).get().getCurrentPrice();
    }

}