package com.churchevents.model.tokens;

import com.churchevents.model.Subscriber;
import com.churchevents.model.User;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
public class SubscriptionToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="token_id")
    private Long tokenId;

    @Column(name="subscription_token")
    private String subscriptionToken;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @OneToOne(targetEntity = Subscriber.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "subscriber_id")
    private Subscriber subscriber;

    public SubscriptionToken(Subscriber subscriber){
        this.subscriptionToken = UUID.randomUUID().toString();
        this.createdDate = new Date();
        this.subscriber = subscriber;
    }

    public SubscriptionToken() {
    }

    public Long getTokenId() {
        return tokenId;
    }

    public void setTokenId(Long tokenId) {
        this.tokenId = tokenId;
    }

    public String getSubscriptionToken() {
        return subscriptionToken;
    }

    public void setSubscriptionToken(String subscriptionToken) {
        this.subscriptionToken = subscriptionToken;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }
}
