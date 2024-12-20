package com.skillbox.cryptobot.subscriber;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
@Primary
@RequiredArgsConstructor
public class SubscriberRepository {

    @Autowired
    private JdbcTemplate template;

    public List<Subscriber> findAll() {
        PreparedStatementCreator creator = connection -> {
            String sql = "SELECT * FROM subscribers.subscribers";
            PreparedStatement ps = connection.prepareStatement(sql);
            return ps;
        };
        return template.query(creator, new SubscriberRowMapper());
    }

    public Optional<Subscriber> findById(UUID id) {
        PreparedStatementCreator creator = connection -> {
            String sql = "SELECT * FROM subscribers.subscribers WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, id);
            return ps;
        };
        Subscriber subscriber = DataAccessUtils.singleResult(
                template.query(
                        creator,
                        new ArgumentPreparedStatementSetter(new Object[] {id}),
                        new RowMapperResultSetExtractor<>(new SubscriberRowMapper(), 1)
                )
        );
        return Optional.ofNullable(subscriber);
    }

    public Optional<Subscriber> findByUserId(String userId) {
        PreparedStatementCreator creator = connection -> {
            String sql = "SELECT * FROM subscribers.subscribers WHERE user_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);
            return ps;
        };
        Subscriber subscriber = DataAccessUtils.singleResult(
                template.query(
                        creator,
                        new ArgumentPreparedStatementSetter(new Object[] {userId}),
                        new RowMapperResultSetExtractor<>(new SubscriberRowMapper(), 1)
                )
        );
        return Optional.ofNullable(subscriber);
    }


    public Subscriber save(Subscriber subscriber) {
        Optional<Subscriber> existingSubscriber = findByUserId(subscriber.getUserId());
        if (existingSubscriber.isPresent()) {
            return null;
        } else {
            PreparedStatementCreator creator = connection -> {
                String sql = "INSERT INTO subscribers.subscribers (id, user_id, current_price) VALUES (?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, Objects.requireNonNullElse(subscriber.getId(), UUID.randomUUID()));
                ps.setString(2, subscriber.getUserId());
                ps.setObject(3, subscriber.getCurrentPrice());
                return ps;
            };
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(creator, keyHolder);
            subscriber.setId((UUID) keyHolder.getKeys().get("id"));
            return subscriber;
        }
    }

    public void replaceCurrentPrice(String userId, Float currentPrice) {
        PreparedStatementCreator creator = connection -> {
            String sql = "UPDATE subscribers.subscribers SET current_price = ? WHERE user_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setFloat(1, currentPrice);
            ps.setObject(2, userId);
            return ps;
        };
        template.update(creator);
    }

    public void delete(UUID id) {
        PreparedStatementCreator creator = connection -> {
            String sql = "DELETE FROM subscribers.subscribers WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setObject(1, id);
            return ps;
        };
        template.update(creator);
    }

    public void deleteByUserId(String userId) {
        PreparedStatementCreator creator = connection -> {
            String sql = "DELETE FROM subscribers.subscribers WHERE user_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, userId);
            return ps;
        };
        template.update(creator);
    }

}
