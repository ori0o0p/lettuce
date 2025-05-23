package io.lettuce.core;

import static io.lettuce.TestTags.UNIT_TEST;
import static org.assertj.core.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Value}.
 *
 * @author Mark Paluch
 */
@Tag(UNIT_TEST)
class ValueUnitTests {

    @Test
    void shouldCreateEmptyValueFromOptional() {

        Value<String> value = Value.from(Optional.<String> empty());

        assertThat(value.hasValue()).isFalse();
    }

    @Test
    void shouldCreateEmptyValue() {

        Value<String> value = Value.empty();

        assertThat(value.hasValue()).isFalse();
    }

    @Test
    void shouldCreateNonEmptyValueFromOptional() {

        Value<String> value = Value.from(Optional.of("hello"));

        assertThat(value.hasValue()).isTrue();
        assertThat(value.getValue()).isEqualTo("hello");
    }

    @Test
    void shouldCreateEmptyValueFromValue() {

        Value<String> value = Value.fromNullable(null);

        assertThat(value.hasValue()).isFalse();
        assertThat(value.isEmpty()).isTrue();
    }

    @Test
    void shouldCreateNonEmptyValueFromValue() {

        Value<String> value = Value.fromNullable("hello");

        assertThat(value.hasValue()).isTrue();
        assertThat(value.getValue()).isEqualTo("hello");
    }

    @Test
    void justShouldCreateValueFromValue() {

        Value<String> value = Value.just("hello");

        assertThat(value.hasValue()).isTrue();
        assertThat(value.isEmpty()).isFalse();
    }

    @Test
    void justShouldRejectEmptyValueFromValue() {
        assertThatThrownBy(() -> Value.just(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldCreateNonEmptyValue() {

        Value<String> value = Value.from(Optional.of("hello"));

        assertThat(value.hasValue()).isTrue();
        assertThat(value.getValue()).isEqualTo("hello");
    }

    @Test
    void optionalShouldReturnOptional() {

        Value<String> value = Value.from(Optional.of("hello"));

        assertThat(value.optional()).hasValue("hello");
    }

    @Test
    void emptyValueOptionalShouldReturnOptional() {

        Value<String> value = Value.from(Optional.empty());

        assertThat(value.optional()).isEmpty();
    }

    @Test
    void getValueOrElseShouldReturnValue() {

        Value<String> value = Value.from(Optional.of("hello"));

        assertThat(value.getValueOrElse("world")).isEqualTo("hello");
    }

    @Test
    void getValueOrElseShouldReturnOtherValue() {

        Value<String> value = Value.from(Optional.empty());

        assertThat(value.getValueOrElse("world")).isEqualTo("world");
    }

    @Test
    void orElseThrowShouldReturnValue() {

        Value<String> value = Value.from(Optional.of("hello"));

        assertThat(value.getValueOrElseThrow(IllegalArgumentException::new)).isEqualTo("hello");
    }

    @Test
    void emptyValueGetValueOrElseShouldThrowException() {

        Value<String> value = Value.from(Optional.empty());

        assertThatThrownBy(() -> value.getValueOrElseThrow(IllegalArgumentException::new))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getValueOrElseGetShouldReturnValue() {

        Value<String> value = Value.from(Optional.of("hello"));

        assertThat(value.getValueOrElseGet(() -> "world")).isEqualTo("hello");
    }

    @Test
    void emptyValueGetValueOrElseGetShouldReturnOtherValue() {

        Value<String> value = Value.from(Optional.empty());

        assertThat(value.getValueOrElseGet(() -> "world")).isEqualTo("world");
    }

    @Test
    void mapShouldMapValue() {

        Value<String> value = Value.from(Optional.of("hello"));

        assertThat(value.map(s -> s + "-world").getValue()).isEqualTo("hello-world");
    }

    @Test
    void ifHasValueShouldExecuteCallback() {

        Value<String> value = Value.just("hello");
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        value.ifHasValue(s -> atomicBoolean.set(true));

        assertThat(atomicBoolean.get()).isTrue();
    }

    @Test
    void ifHasValueShouldEmptyCallback() {

        Value<String> value = Value.just("hello");
        AtomicBoolean isPresent = new AtomicBoolean();
        value.ifHasValue(s -> isPresent.set(true));

        assertThat(isPresent).isTrue();
    }

    @Test
    void emptyValueShouldNotExecuteIfHasValueCallback() {

        Value<String> value = Value.empty();
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        value.ifHasValue(s -> atomicBoolean.set(true));

        assertThat(atomicBoolean.get()).isFalse();

        AtomicBoolean isPresent = new AtomicBoolean();
        AtomicBoolean isAbsent = new AtomicBoolean();
        value.ifHasValueOrElse(s -> isPresent.set(true), () -> isAbsent.set(true));

        assertThat(isPresent).isFalse();
        assertThat(isAbsent).isTrue();
    }

    @Test
    void ifEmptyShouldExecuteCallback() {

        Value<String> value = Value.empty();
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        value.ifEmpty(() -> atomicBoolean.set(true));

        assertThat(atomicBoolean.get()).isTrue();
    }

    @Test
    void valueShouldNotExecuteIfEmptyCallback() {

        Value<String> value = Value.just("hello");
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        value.ifEmpty(() -> atomicBoolean.set(true));

        assertThat(atomicBoolean.get()).isFalse();
    }

    @Test
    void emptyValueMapShouldNotMapEmptyValue() {

        Value<String> value = Value.from(Optional.empty());

        assertThat(value.map(s -> s + "-world")).isSameAs(value);
    }

    @Test
    void emptyValueGetEmptyValueShouldThrowException() {
        assertThatThrownBy(() -> Value.from(Optional.<String> empty()).getValue()).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void shouldBeEquals() {

        Value<String> value = Value.from(Optional.of("hello"));
        Value<String> other = Value.fromNullable("hello");
        Value<String> different = Value.fromNullable("different");

        assertThat(value).isEqualTo(other);
        assertThat(value).isNotEqualTo(different);

        assertThat(value.hashCode()).isEqualTo(other.hashCode());
        assertThat(value.hashCode()).isNotEqualTo(different.hashCode());
    }

    @Test
    void toStringShouldRenderCorrectly() {

        Value<String> value = Value.from(Optional.of("hello"));
        Value<String> empty = Value.fromNullable(null);

        assertThat(value.toString()).isEqualTo("Value[hello]");
        assertThat(empty.toString()).isEqualTo("Value.empty");
    }

    @Test
    void emptyValueStreamShouldCreateEmptyStream() {

        Value<String> empty = Value.fromNullable(null);

        assertThat(empty.stream().count()).isEqualTo(0);
    }

    @Test
    void streamShouldCreateAStream() {

        Value<String> empty = Value.fromNullable("hello");

        assertThat(empty.stream().count()).isEqualTo(1);
    }

}
