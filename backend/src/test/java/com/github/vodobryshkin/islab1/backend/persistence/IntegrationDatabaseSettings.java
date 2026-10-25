package com.github.vodobryshkin.islab1.backend.persistence;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Параметры подключения к тестовой БД из переменных окружения.
 *
 * <p>Без переменных используется H2 в памяти. Любая другая БД допускается, только если
 * имя базы оканчивается на {@code _test}: тесты очищают все таблицы, поэтому подключение
 * к рабочей базе, например {@code studs}, отклоняется до первого запроса.
 */
@Getter
final class IntegrationDatabaseSettings {
    private static final String URL_VARIABLE = "IS_LAB1_TEST_DB_URL";
    private static final String USER_VARIABLE = "IS_LAB1_TEST_DB_USER";
    private static final String PASSWORD_VARIABLE = "IS_LAB1_TEST_DB_PASSWORD";

    private static final String DEFAULT_URL =
            "jdbc:h2:mem:islab1_test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
    private static final String DEFAULT_USER = "sa";

    private static final Pattern H2_URL = Pattern.compile("^jdbc:h2:mem:([^;]+)(;.*)?$");
    private static final Pattern POSTGRESQL_URL =
            Pattern.compile("^jdbc:postgresql://[^/]+/([^?;]+)([?;].*)?$");
    private static final String TEST_DATABASE_SUFFIX = "_test";

    private final String url;
    private final String user;
    private final String password;
    @Getter(AccessLevel.NONE)
    private final boolean h2;

    IntegrationDatabaseSettings(Map<String, String> environment) {
        this.url = environment.getOrDefault(URL_VARIABLE, DEFAULT_URL);
        this.user = environment.getOrDefault(USER_VARIABLE, DEFAULT_USER);
        this.password = environment.getOrDefault(PASSWORD_VARIABLE, "");

        Matcher h2Matcher = H2_URL.matcher(url);
        Matcher postgresqlMatcher = POSTGRESQL_URL.matcher(url);
        String databaseName;
        if (h2Matcher.matches()) {
            databaseName = h2Matcher.group(1);
            this.h2 = true;
        } else if (postgresqlMatcher.matches()) {
            databaseName = postgresqlMatcher.group(1);
            this.h2 = false;
        } else {
            throw new IllegalStateException(
                    "Only in-memory H2 and PostgreSQL test databases are supported.");
        }

        if (!databaseName.endsWith(TEST_DATABASE_SUFFIX)) {
            throw new IllegalStateException("Refusing to use database '" + databaseName
                    + "': the test database name must end with " + TEST_DATABASE_SUFFIX + ".");
        }
    }

    boolean isH2() {
        return h2;
    }
}
