package com.github.vodobryshkin.islab1.backend.security;

import com.github.vodobryshkin.islab1.backend.service.UserService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

/**
 * Проверяет логин и пароль по пользователям в БД.
 * Имя вызывающего — логин; ролей нет, все вошедшие пользователи равноправны.
 */
@ApplicationScoped
public class DatabaseIdentityStore implements IdentityStore {
    private final UserService userService;

    /**
     * Создаёт хранилище учётных данных.
     *
     * @param userService проверка паролей
     */
    @Inject
    public DatabaseIdentityStore(UserService userService) {
        this.userService = userService;
    }

    /** Нужен CDI для создания клиентского прокси; прикладной код его не вызывает. */
    protected DatabaseIdentityStore() {
        this.userService = null;
    }

    /**
     * Проверяет учётные данные; вызывается {@link IdentityStore#validate} по их типу.
     *
     * @param credential логин и пароль
     * @return {@code VALID} с логином или {@code INVALID}
     */
    public CredentialValidationResult validate(UsernamePasswordCredential credential) {
        return userService
                .checkCredentials(credential.getCaller(), credential.getPasswordAsString())
                .map(user -> new CredentialValidationResult(user.getUsername()))
                .orElse(CredentialValidationResult.INVALID_RESULT);
    }
}
