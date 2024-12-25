package bgu.spl.mics;

import java.util.*;
import java.util.concurrent.*;

/**
 * Implementation of the MessageBus interface with separate queues for Events and Broadcasts
 * using synchronization mechanisms such as synchronized blocks and BlockingQueue.
 */
public class MessageBusImpl implements MessageBus {

    // Maps to hold the subscriptions for events and broadcasts.
    private ConcurrentHashMap<Class<? extends Event<?>>, ConcurrentLinkedQueue<MicroService>> eventSubscribers;
    private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastSubscribers;

    // Queues for each MicroService, separate for events and broadcasts.
    private Map<MicroService, BlockingQueue<Message>> microServiceQueues;

    private static MessageBusImpl instance;
    public static MessageBusImpl getInstance() {
        if (instance == null) {
            instance = new MessageBusImpl();
        }

        return instance;
    }

    // Constructor
    public MessageBusImpl() {
        eventSubscribers = new ConcurrentHashMap<>();
        broadcastSubscribers = new ConcurrentHashMap<>();
        microServiceQueues = new HashMap<>();
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
        eventSubscribers.compute(type, (t, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }
            
            queue.add(m);
            return queue;
        });
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
        broadcastSubscribers.compute(type, (t, queue) -> {
            if (queue == null) {
                queue = new ConcurrentLinkedQueue<>();
            }

            queue.add(m);
            return queue;
        });
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = (Future<T>) e.getFuture();
        if (future != null) {
            future.resolve(result); // Resolve the result for the event.
        }
    }

    @Override
    public void sendBroadcast(Broadcast b) {
        ConcurrentLinkedQueue<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
        if (subscribers != null) {
            subscribers.forEach(m -> putMessageInQueue(m, b));
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {
        Future<T> future = new Future<>();
        e.setFuture(future);

        Queue<MicroService> subscribers = eventSubscribers.get(e.getClass());
        if (subscribers == null) {
            return null;
        }
        synchronized (subscribers) {
            MicroService m = subscribers.poll();
            if (m == null) {
                return null; // No subscribers for the event
            }
            putMessageInQueue(m, e);
            subscribers.add(m);
        }

        return future;
    }

    @Override
    public void register(MicroService m) {
        // Create a queue for the new MicroService
        microServiceQueues.put(m, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(MicroService m) {
        // Remove the MicroService and its queue
        microServiceQueues.remove(m);
        eventSubscribers.values().forEach(queue -> queue.remove(m));
        broadcastSubscribers.values().forEach(queue -> queue.remove(m));
    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        BlockingQueue<Message> queue = microServiceQueues.get(m);
        if (queue == null) {
            throw new IllegalStateException("MicroService is not registered.");
        }
        // Block until a message is available in the MicroService's queue
        return queue.take();
    }

    // Helper method to add a message to the MicroService's queue
    private void putMessageInQueue(MicroService m, Message msg) {
        try {
            microServiceQueues.get(m).put(msg); // Blocking if queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption if needed
        }
    }
}
