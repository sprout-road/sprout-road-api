package com.sprout.api.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TempAuthService {

    private final UserJpaRepository userJpaRepository;

    public void signUp() {
        if (userJpaRepository.count() == 0) {
            User user = User.create("새싹이");
            userJpaRepository.save(user);
        }
    }
}
