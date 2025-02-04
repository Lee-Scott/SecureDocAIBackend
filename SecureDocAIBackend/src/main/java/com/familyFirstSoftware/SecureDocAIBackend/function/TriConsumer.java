package com.familyFirstSoftware.SecureDocAIBackend.function;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Lee Scott
 * @version 1.0
 * @license FamilyFirstSoftware, LLC (<a href="https://www.FamilyFirstSoftware.com"> FFS, LLC</a>)
 * @email FamilyFirstSoftware@gmail.com
 * @since 2/3/2025
 */

@FunctionalInterface
public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);

    // Not used TODO: experiment with
    default TriConsumer<T, U, V> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (t, u, v) -> { accept(t, u, v); after.accept(t); };
    }
}

