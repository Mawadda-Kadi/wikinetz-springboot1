package de.davaso.wikinetz.service;


import de.davaso.wikinetz.api.IdGenerator;
import java.util.concurrent.atomic.AtomicInteger;

public class SequentialIdGenerator implements IdGenerator {
    private final AtomicInteger seq = new AtomicInteger(1);
    @Override
    public int nextId() { return seq.getAndIncrement(); }
}