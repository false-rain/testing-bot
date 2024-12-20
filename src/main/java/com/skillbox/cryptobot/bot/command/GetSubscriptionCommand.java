package com.skillbox.cryptobot.bot.command;

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

import java.text.MessageFormat;

@Service
@Slf4j
@AllArgsConstructor
public class GetSubscriptionCommand implements IBotCommand {

    @Autowired
    private SubscriberRepository subscriberRepository;

    @Override
    public String getCommandIdentifier() {
        return "get_subscription";
    }

    @Override
    public String getDescription() {
        return "Возвращает текущую подписку";
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId());
        try {
            answer.setText(processMessageText(getValue(message)));
            absSender.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error occurred in /get_subscription command", e);
        }
    }

    private String getValue(Message message) {
        return subscriberRepository.findByUserId(
                message.getChatId().toString()).get().getCurrentPrice().toString();
    }

    private String processMessageText(String price) {
        return MessageFormat
                .format(
                        """
                                Вы подписанны на {0} \n
                                Используйте: /subscribe [порог_цены] чтобы поменять цену
                                """, price
                );
    }

}