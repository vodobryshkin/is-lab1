package com.github.vodobryshkin.islab1.backend;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Основной класс веб-приложения, который задаёт mapping для всего API.
 *
 * @author vodobryshkin
 * @since 18.09.2026 12:50
 */
@ApplicationPath("/api")
public class BackendApplication extends Application {
}