package com.github.vodobryshkin.islab1.backend.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Проверяет DATA_CHANGED на развёрнутом сервере настоящими WebSocket-клиентами.
 * Запускается командой ./gradlew serverTest при работающем сервере.
 */
@Tag("server")
class DataChangedEventsTest {
    private static final String BASE_URL =
            System.getenv().getOrDefault("IS_LAB1_BASE_URL", "http://localhost:8080/api/v1");
    private static final String ORIGIN = "http://localhost:5173";
    private static final String DATA_CHANGED = "{\"type\":\"DATA_CHANGED\"}";
    private static final Duration DELIVERY = Duration.ofSeconds(5);
    private static final Duration SILENCE = Duration.ofMillis(700);
    private static final String COORDINATES = "{\"x\": 1, \"y\": 2}";
    private static final String POST = "POST";
    private static final String COORDINATES_PATH = "/coordinates";
    private static final String WORKERS_PATH = "/workers";
    private static final String ALICE = "alice";
    private static final String BOB = "bob";

    private final String run = Long.toString(System.nanoTime(), 36);
    private final List<Client> clients = new ArrayList<>();

    @AfterEach
    void close_clients() {
        clients.forEach(Client::close);
    }

    @Test
    void sends_one_data_changed_to_every_client_including_initiator() throws Exception {
        Client alice = connected(ALICE);
        Client bob = connected(BOB);

        int status = alice.send(POST, COORDINATES_PATH, COORDINATES).statusCode();

        assertAll(
                () -> assertThat(status).isEqualTo(201),
                () -> assertThat(alice.next(DELIVERY)).isEqualTo(DATA_CHANGED),
                () -> assertThat(bob.next(DELIVERY)).isEqualTo(DATA_CHANGED),
                () -> assertThat(alice.next(SILENCE)).as("одно уведомление на транзакцию").isNull(),
                () -> assertThat(bob.next(SILENCE)).isNull());
    }

    @Test
    void notifies_about_change_made_in_another_session() throws Exception {
        Client alice = connected(ALICE);
        Client bob = connected(BOB);
        long id = bob.created(COORDINATES_PATH, COORDINATES);
        alice.drain();
        bob.drain();

        int status = bob.send("DELETE", COORDINATES_PATH + "/" + id, null).statusCode();

        assertAll(
                () -> assertThat(status).isEqualTo(204),
                () -> assertThat(alice.next(DELIVERY)).isEqualTo(DATA_CHANGED));
    }

    @Test
    void sends_nothing_when_operation_is_refused_or_changes_nothing() throws Exception {
        Client alice = connected(ALICE);
        long organization = alice.created("/organizations", organization("refused"));
        long coordinates = alice.created(COORDINATES_PATH, COORDINATES);
        long worker = alice.created(WORKERS_PATH, worker(coordinates, organization));
        String hire = "{\"organization_id\": " + organization + "}";
        alice.send(POST, WORKERS_PATH + "/" + worker + "/hire", hire);
        alice.drain();

        int deleteUsed = alice.send("DELETE", COORDINATES_PATH + "/" + coordinates, null)
                .statusCode();
        int missingWorker = alice.send(POST, WORKERS_PATH,
                worker(Long.MAX_VALUE, organization)).statusCode();
        int repeatedHire = alice.send(POST, WORKERS_PATH + "/" + worker + "/hire", hire)
                .statusCode();
        int sameSalaries = alice.send(POST, "/organizations/" + organization
                + "/index_salaries", "{\"coefficient\": 1}").statusCode();

        assertAll(
                () -> assertThat(List.of(deleteUsed, missingWorker, repeatedHire, sameSalaries))
                        .containsExactly(409, 404, 204, 200),
                () -> assertThat(alice.next(SILENCE)).isNull());
    }

    @Test
    void sends_one_notification_for_nested_creation_and_mass_indexation() throws Exception {
        Client alice = connected(ALICE);
        Client bob = connected(BOB);
        long organization = alice.created("/organizations", organization("indexed"));
        long coordinates = alice.created(COORDINATES_PATH, COORDINATES);
        for (int i = 0; i < 3; i++) {
            alice.created(WORKERS_PATH, worker(coordinates, organization));
        }
        alice.drain();
        bob.drain();

        alice.created(WORKERS_PATH, workerWithNewObjects());
        String afterNestedCreation = bob.next(DELIVERY);
        String extraAfterNestedCreation = bob.next(SILENCE);
        int indexation = alice.send(POST, "/organizations/" + organization + "/index_salaries",
                "{\"coefficient\": 2}").statusCode();

        assertAll(
                () -> assertThat(afterNestedCreation).isEqualTo(DATA_CHANGED),
                () -> assertThat(extraAfterNestedCreation).isNull(),
                () -> assertThat(indexation).isEqualTo(200),
                () -> assertThat(bob.next(DELIVERY)).isEqualTo(DATA_CHANGED),
                () -> assertThat(bob.next(SILENCE)).as("одно уведомление на всю индексацию")
                        .isNull());
    }

    @Test
    void ends_connections_of_session_on_logout_and_keeps_other_sessions() throws Exception {
        Client alice = connected(ALICE);
        Client bob = connected(BOB);

        int status = alice.send(POST, "/auth/logout", null).statusCode();

        assertAll(
                () -> assertThat(status).isEqualTo(204),
                () -> assertThat(alice.next(DELIVERY)).contains("\"code\":\"SESSION_ENDED\""),
                () -> assertThat(alice.closeCode()).isEqualTo(4001),
                () -> assertThat(bob.next(SILENCE)).as("другая сессия не затронута").isNull());
    }

    @Test
    void closes_connection_which_sends_message() throws Exception {
        Client alice = connected(ALICE);

        alice.socket.sendText("hello", true);

        assertAll(
                () -> assertThat(alice.next(DELIVERY)).contains("\"code\":\"INVALID_MESSAGE\""),
                () -> assertThat(alice.closeCode()).isEqualTo(4002));
    }

    @Test
    void keeps_notifying_other_clients_when_one_connection_fails() throws Exception {
        Client carol = connected("carol");
        Client alice = connected(ALICE);
        Client bob = connected(BOB);
        carol.close();

        alice.created(COORDINATES_PATH, COORDINATES);

        assertAll(
                () -> assertThat(alice.next(DELIVERY)).isEqualTo(DATA_CHANGED),
                () -> assertThat(bob.next(DELIVERY)).isEqualTo(DATA_CHANGED));
    }

    private Client connected(String name)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Client client = new Client(name + "_" + run);
        clients.add(client);
        assertThat(client.next(DELIVERY)).isEqualTo("{\"type\":\"AUTHENTICATED\"}");
        return client;
    }

    private String organization(String name) {
        String body =
                """
                {"full_name": "%s %s", "annual_turnover": 10, "employees_count": 1, "rating": 1,
                 "type": null, "official_address": {"street": "Official", "town": null},
                 "postal_address": {"street": "Postal", "town": null}}
                """;
        return body.formatted(name, run);
    }

    private String worker(long coordinates, long organization) {
        String body =
                """
                {"name": "Worker", "coordinates": {"id": %d}, "salary": 100, "rating": 1,
                 "start_date": "2026-10-01T09:00:00Z", "position": null, "status": null,
                 "person": null, "organization_id": %d}
                """;
        return body.formatted(coordinates, organization);
    }

    private String workerWithNewObjects() {
        String body =
                """
                {"name": "Nested", "coordinates": {"x": 1, "y": 1}, "salary": 100, "rating": 1,
                 "start_date": "2026-10-01T09:00:00Z", "position": null, "status": null,
                 "person": {"eye_color": null, "hair_color": "BLUE",
                   "location": {"x": 1, "y": 1, "z": 1}, "birthday": "2000-01-01T00:00:00Z",
                   "weight": 50, "nationality": "SPAIN"},
                 "organization": %s}
                """;
        return body.formatted(organization("nested"));
    }

    /**
     * Пользователь с собственной HTTP-сессией, CSRF-токеном и WebSocket-соединением.
     */
    private final class Client {
        private final CookieManager cookies = new CookieManager();
        private final HttpClient http = HttpClient.newBuilder().cookieHandler(cookies).build();
        private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();
        private final CompletableFuture<Integer> closed = new CompletableFuture<>();
        private final WebSocket socket;
        private String csrf;

        Client(String username)
                throws IOException, InterruptedException, ExecutionException, TimeoutException {
            String credentials = "{\"username\": \"" + username + "\", \"password\": \"secret_1\"}";
            csrf = json(send("GET", "/auth/csrf", null)).getString("csrf_token");
            assertThat(send(POST, "/auth/register", credentials).statusCode()).isEqualTo(201);
            assertThat(send(POST, "/auth/login", credentials).statusCode()).isEqualTo(200);
            csrf = json(send("GET", "/auth/csrf", null)).getString("csrf_token");
            String sessionId = cookies.getCookieStore().getCookies().stream()
                    .filter(cookie -> "JSESSIONID".equals(cookie.getName()))
                    .map(HttpCookie::getValue)
                    .findFirst()
                    .orElseThrow();
            socket = HttpClient.newHttpClient().newWebSocketBuilder()
                    .header("Cookie", "JSESSIONID=" + sessionId)
                    .header("Origin", ORIGIN)
                    .buildAsync(URI.create(BASE_URL.replaceFirst("^http", "ws") + "/events"),
                            new Listener())
                    .get(DELIVERY.toSeconds(), TimeUnit.SECONDS);
        }

        HttpResponse<String> send(String method, String path, String body)
                throws IOException, InterruptedException {
            HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(BASE_URL + path))
                    .header("X-CSRF-Token", csrf == null ? "" : csrf)
                    .method(method, body == null
                            ? HttpRequest.BodyPublishers.noBody()
                            : HttpRequest.BodyPublishers.ofString(body));
            if (body != null) {
                request.header("Content-Type", "application/json");
            }
            return http.send(request.build(), HttpResponse.BodyHandlers.ofString());
        }

        long created(String path, String body) throws IOException, InterruptedException {
            HttpResponse<String> response = send(POST, path, body);
            assertThat(response.statusCode()).as(response.body()).isEqualTo(201);
            return json(response).getJsonNumber("id").longValue();
        }

        String next(Duration timeout) throws InterruptedException {
            return messages.poll(timeout.toMillis(), TimeUnit.MILLISECONDS);
        }

        /**
         * Принимает уведомления подготовки, пока они идут, чтобы проверка видела только свои.
         */
        void drain() throws InterruptedException {
            while (next(SILENCE) != null) {
                messages.clear();
            }
        }

        int closeCode() throws InterruptedException, ExecutionException, TimeoutException {
            return closed.get(DELIVERY.toSeconds(), TimeUnit.SECONDS);
        }

        void close() {
            socket.abort();
        }

        private JsonObject json(HttpResponse<String> response) {
            try (JsonReader reader = Json.createReader(new StringReader(response.body()))) {
                return reader.readObject();
            }
        }

        private final class Listener implements WebSocket.Listener {
            private String text = "";

            @Override
            public CompletionStage<?> onText(WebSocket webSocket, CharSequence data,
                    boolean last) {
                text += data;
                if (last) {
                    messages.add(text);
                    text = "";
                }
                webSocket.request(1);
                return null;
            }

            @Override
            public CompletionStage<?> onClose(WebSocket webSocket, int statusCode,
                    String reason) {
                closed.complete(statusCode);
                return null;
            }
        }
    }
}
