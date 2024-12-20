package com.skillbox.cryptobot.bot.command;

import com.skillbox.cryptobot.subscriber.Subscriber;
import com.skillbox.cryptobot.subscriber.SubscriberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


@Service
@AllArgsConstructor
@Slf4j
public class StartCommand implements IBotCommand {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public String getCommandIdentifier() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Запускает бота";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        answer.setText(processMessageText());
        try {
            absSender.execute(answer);
            writeToBase(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /start command", e);
        }
    }

    private void writeToBase(Message message) {
        new Thread(() -> {
            Subscriber subscriber = new Subscriber();
            subscriber.setUserId(message.getChatId().toString());
            subscriberRepository.save(subscriber);
        }).start();
    }

    private String processMessageText() {
        return """
                Привет! Данный бот помогает отслеживать стоимость биткоина.
                Поддерживаемые команды:
                 /subscribe [число] - подписаться на стоимость биткоина в USD
                 /get_price - получить стоимость биткоина
                 /get_subscription - получить текущую подписку
                 /unsubscribe - отменить подписку на стоимость
                """;
    }
}