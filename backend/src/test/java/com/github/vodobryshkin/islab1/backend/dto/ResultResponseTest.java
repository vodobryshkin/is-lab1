package com.github.vodobryshkin.islab1.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Проверяет JSON служебных ответов: страницы, ID, CSRF-токена и пользователя.
 */
class ResultResponseTest {
    private final JsonOf json = new JsonOf();

    @AfterEach
    void close() {
        json.close();
    }

    @Test
    void writes_page_in_snake_case() {
        PageResponse<IdResponse> sut = new PageResponse<>(List.of(new IdResponse(3)), 2, 1, 5);

        assertThat(json.serialize(sut)).isEqualTo(json.parse(
                "{\"items\":[{\"id\":3}],\"page\":2,\"page_size\":1,\"total_items\":5}"));
    }

    @Test
    void writes_null_organization_of_created_worker() {
        assertThat(json.serialize(new CreateWorkerResponse(42, null)))
                .isEqualTo(json.parse("{\"id\":42,\"organization_id\":null}"));
    }

    @Test
    void writes_csrf_token_and_current_user() {
        assertThat(List.of(json.serialize(new CsrfTokenResponse("token")),
                json.serialize(new CurrentUserResponse(7, "vodobryshkin"))))
                .containsExactly(json.parse("{\"csrf_token\":\"token\"}"),
                        json.parse("{\"id\":7,\"username\":\"vodobryshkin\"}"));
    }

    @Test
    void hides_password_in_credentials_text() {
        assertThat(new CredentialsRequest("user_1", "secret_1").toString())
                .contains("user_1")
                .doesNotContain("secret_1");
    }
}
