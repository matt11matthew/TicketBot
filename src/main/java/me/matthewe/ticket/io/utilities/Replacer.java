package me.matthewe.ticket.io.utilities;

@FunctionalInterface
public interface Replacer<T> {
    T replace( T t);
}