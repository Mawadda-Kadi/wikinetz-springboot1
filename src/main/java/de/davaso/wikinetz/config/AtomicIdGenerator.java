package de.davaso.wikinetz.config;


import de.davaso.wikinetz.api.IdGenerator;


// Simple Id generator
class AtomicIdGenerator implements IdGenerator {
    private final java.util.concurrent.atomic.AtomicInteger seq = new java.util.concurrent.atomic.AtomicInteger(0);
    @Override public int nextId() { return seq.incrementAndGet(); }
}


