package com.github.vodobryshkin.islab1.backend.security;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.AutoApplySession;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStoreHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Механизм входа через серверную HTTP-сессию.
 * Логин и пароль проверяются только при вызове из ресурса входа, то есть после проверки CSRF.
 */
@ApplicationScoped
@AutoApplySession
public class SessionAuthenticationMechanism implements HttpAuthenticationMechanism {
    private final IdentityStoreHandler identityStoreHandler;

    /**
     * Создаёт механизм.
     *
     * @param identityStoreHandler проверка учётных данных хранилищами
     */
    @Inject
    public SessionAuthenticationMechanism(IdentityStoreHandler identityStoreHandler) {
        this.identityStoreHandler = identityStoreHandler;
    }

    /**
     * Нужен CDI для создания клиентского прокси, прикладной код его не вызывает.
     */
    protected SessionAuthenticationMechanism() {
        this.identityStoreHandler = null;
    }

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request,
            HttpServletResponse response, HttpMessageContext context)
            throws AuthenticationException {
        Credential credential = context.isAuthenticationRequest()
                ? context.getAuthParameters().getCredential()
                : null;

        if (credential != null) {
            CredentialValidationResult result = identityStoreHandler.validate(credential);
            if (result.getStatus() == CredentialValidationResult.Status.VALID) {
                return context.notifyContainerAboutLogin(result);
            }
            return AuthenticationStatus.SEND_FAILURE;
        }

        if (context.isProtected()) {
            return context.responseUnauthorized();
        }

        return context.doNothing();
    }
}
