package com.skillbox.cryptobot.subscriber;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SubscriberRowMapper implements RowMapper<Subscriber> {

    @Override
    public Subscriber mapRow(ResultSet rs, int rowNum) throws SQLException {
        Subscriber subscriber = new Subscriber();
        subscriber.setId(UUID.fromString(rs.getString(Subscriber.Fields.id)));
        subscriber.setUserId(rs.getString(Subscriber.Fields.userId));
        subscriber.setCurrentPrice(rs.getFloat(Subscriber.Fields.currentPrice));
        return subscriber;
    }
}
