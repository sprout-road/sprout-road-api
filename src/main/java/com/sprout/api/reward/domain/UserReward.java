package com.sprout.api.reward.domain;

import com.sprout.api.common.entity.TimeBaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_rewards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserReward extends TimeBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @OneToOne(fetch = FetchType.EAGER)
    private Reward reward;
    private Integer count;

    public UserReward(Long userId, Reward reward) {
        this.userId = userId;
        this.reward = reward;
        this.count = 0;
    }

    public void incrementCount() {
        this.count++;
    }
}
