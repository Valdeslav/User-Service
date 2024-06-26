package com.valdeslav.user.repository;

import com.valdeslav.user.model.RefreshToken;
import com.valdeslav.user.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByValue(String value);

    void deleteByUserAndValue(User user, String value);

    void deleteAllByExpiryDateBefore(Date date);
}
