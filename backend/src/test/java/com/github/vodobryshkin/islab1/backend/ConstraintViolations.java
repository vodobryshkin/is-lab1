package com.github.vodobryshkin.islab1.backend;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Нарушения Jakarta Validation в виде строк «поле Ограничение», например "x Min":
 * тест проверяет и поле, и тип нарушения, не завися от порядка в наборе.
 */
public final class ConstraintViolations implements AutoCloseable {
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    /**
     * Проверяет объект вместе с вложенными объектами @Valid.
     *
     * @param object проверяемый объект
     * @return нарушения в виде «поле Ограничение»
     */
    public Set<String> of(Object object) {
        return factory.getValidator().validate(object).stream()
                .map(violation -> violation.getPropertyPath() + " "
                        + violation.getConstraintDescriptor().getAnnotation()
                                .annotationType().getSimpleName())
                .collect(Collectors.toSet());
    }

    @Override
    public void close() {
        factory.close();
    }
}
