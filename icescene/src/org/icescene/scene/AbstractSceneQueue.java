package org.icescene.scene;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.icelib.QueueExecutor;
import org.icescene.IcesceneApp;

public abstract class AbstractSceneQueue<K, O extends SceneQueueLoadable<K>> {

    private static final Logger LOG = Logger.getLogger(AbstractSceneQueue.class.getName());
    protected final IcesceneApp app;
    protected final Map<K, O> loaded = new ConcurrentHashMap<K, O>();
    protected final Map<K, Future> queued = new ConcurrentHashMap<K, Future>();
    private boolean closed;
    private ExecutorService executor;
    private boolean stopExecutorOnClose = true;
    private boolean pause = false;

    public AbstractSceneQueue(IcesceneApp app) {
        this.app = app;
    }
    
    public void pause() {
    	if(pause) {
    		throw new IllegalStateException("Already paused.");
    	}
    	pause= true;
    }
    public void resume() {
    	if(!pause) {
    		throw new IllegalStateException("Already paused.");
    	}
    	pause= false;
    }

    public boolean isStopExecutorOnClose() {
        return stopExecutorOnClose;
    }

    public void setStopExecutorOnClose(boolean stopExecutorOnClose) {
        this.stopExecutorOnClose = stopExecutorOnClose;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    public String getTaskName(K key) {
        return key.toString();
    }

    public Future<O> load(final K key) {
    	if(pause) {
    		throw new IllegalStateException("Paused, loader is not accepting request.");
    	}
        if(closed) {
            throw new IllegalStateException("Executor is closed.");
        }
        if (!isValid(key)) {
            LOG.info(String.format("Not loading %s because its not valid", key));
            return null;
        }
        if (loaded.containsKey(key)) {
            final O instance = loaded.get(key);
            // Already loaded
            return new Future<O>() {
                public boolean cancel(boolean mayInterruptIfRunning) {
                    return false;
                }

                public boolean isCancelled() {
                    return false;
                }

                public boolean isDone() {
                    return true;
                }

                public O get() throws InterruptedException, ExecutionException {
                    return instance;
                }

                public O get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                    return instance;
                }
            };
        } else if (queued.containsKey(key)) {
            return queued.get(key);
        } else {
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine(String.format("Queuing for %s @ %s", this.getClass(), key));
            }
            if (executor == null) {
                LOG.info("Creating new executor for loader");
                executor = Executors.newFixedThreadPool(1, new QueueExecutor.DaemonThreadFactory(getClass().getName() + "Executor"));
            }
            final Future<O> future = executor.submit(new Callable<O>() {
                @Override
                public String toString() {
                    return getTaskName(key);
                }

                public O call() throws Exception {
                	if(pause) {
                		throw new IllegalStateException("Paused, loader is not accepting request.");
                	}
                    if (closed) {
                		throw new IllegalStateException("Closed, loader is not accepting request.");
                    }
                    long started = System.currentTimeMillis();
                    if (LOG.isLoggable(Level.FINE)) {
                        LOG.fine(String.format("Loading for %s @ %s", AbstractSceneQueue.this.getClass(), key));
                    }
                    try {
                        final O instance = doReload(key);
                        if (!closed) {
                            loaded.put(key, instance);
                        }
                        return instance;
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, "Failed to load.", e);
                        return null;
                    } finally {
                        if (LOG.isLoggable(Level.FINE)) {
                            LOG.fine(String.format("Loaded for %s @ %s (took %s ms)", AbstractSceneQueue.this.getClass(), key, System.currentTimeMillis() - started));
                        }
                        queued.remove(key);
                    }

                }
            });
            queued.put(key, future);
            return future;
        }
    }

    public O get(K s) {
        return loaded.get(s);
    }

    public boolean isLoaded(K location) {
        return loaded.containsKey(location);
    }

    public void unload(K page) {
        queued.remove(page);
        O i = loaded.remove(page);
        if (i != null) {
            try {
                doUnload(i);
            }
            finally {
                i.unload();
            }
        }
        else {
        	LOG.warning("Unloading non-existant page " + page);
        }
    }

    public Collection<O> getLoaded() {
        return loaded.values();
    }

    public void unloadAll() {
        if (LOG.isLoggable(Level.INFO)) {
            LOG.info(String.format("Unloading all in %s", this.getClass()));
        }
        for (O pi : loaded.values()) {
            doUnload(pi);
        }
        loaded.clear();
    }

    protected abstract O doUnload(final O instance);

    protected abstract O doReload(final K page);
    
    public void close() {
        closed = true;
        if (executor != null) {
            if (stopExecutorOnClose) {
                executor.shutdown();
            }
            unloadAll();
            try {
                if (stopExecutorOnClose) {
                    executor.awaitTermination(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException ex) {
            }
        } else {
            unloadAll();
        }
    }

    public boolean isAnyLoaded() {
        return !loaded.isEmpty();
    }

    public void clearQueue() {
        queued.clear();
    }

    protected boolean isValid(K page) {
        return true;
    }
}
