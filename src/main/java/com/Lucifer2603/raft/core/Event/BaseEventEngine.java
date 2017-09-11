package com.Lucifer2603.raft.core.Event;


import com.Lucifer2603.raft.core.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangchen20
 */
public class BaseEventEngine implements EventEngine {




    // 为何不用类似msoa中的责任连? 感觉有可能一个event的处理,前后会有多个相同的handler处理
    private Map<String, EventHandler> eventHandlers = new HashMap<>();

    public void registerEvent(String eventName, EventHandler handlers) {
        if (eventHandlers.containsKey(eventName)) {
            throw new RuntimeException("Event " + eventName + " already registered!");
        }

        eventHandlers.put(eventName, handlers);
    }



    //
    private ExecutorService asyncExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            30 * 1000,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

//    private ExecutorService singleExecutor = Executors.newSingleThreadExecutor();

    // 使用 single thread pool
    public void publishEvent(final Event e) {

        EventHandler handlers = eventHandlers.get(e.getName());

        if (handlers == null) {
            return;
        }

        handlers.process(e);

    }

    // 使用 thread pool
    public void publishEventAsync(final Event e, final EventCallback callback) {
        EventHandler handlers = eventHandlers.get(e.getName());

        if (handlers == null) {
            return;
        }

        // add to executors
        // todo 使用这个方法,可以有效的将Event传入.
        asyncExecutor.execute(() -> {

            try {
                callback.preInvoke(e);
                handlers.process(e);
                callback.onSuccess(e);
            } catch (Throwable t) {
                callback.onFail(e, t);
            }
        });
    }



    // 代理模式
    public static class HandlerWrapper implements EventHandler {

        private EventHandler delegate;


        public HandlerWrapper prev;
        public HandlerWrapper next;


        public void process(Event e) {

            try {
                delegate.process(e);

                if (next != null && !e.shouldEnd()) {
                    next.process(e);
                }

            } catch (Throwable t) {
                onException(e, t);
                delegate.onException(e, t);
            }
        }

        public void onException(Event e, Throwable t) {
            // todo log
        }

    }
}
