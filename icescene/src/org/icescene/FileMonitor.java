package org.icescene;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class FileMonitor implements Runnable {

    private static final Logger LOG = Logger.getLogger(FileMonitor.class.getName());
    private boolean stop;
    private final WatchService watcher;
    private Map<String, List<Listener>> listeners = new ConcurrentHashMap<String, List<Listener>>();

    public interface Monitor {

        void stop();
    }
    private final Object lock = new Object();

    public interface Listener {

        void fileUpdated(File file);

        void fileCreated(File file);

        void fileDeleted(File file);
    }

    public FileMonitor() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
    }

    public Monitor monitorDirectory(final File directory, final Listener listener) throws IOException {
        synchronized (lock) {
            if (!directory.isDirectory()) {
                throw new IOException("Not a directory.");
            }
            LOG.info(String.format("Monitoring %s", directory.getAbsolutePath()));
            Path path = FileSystems.getDefault().getPath(directory.getAbsolutePath());
            WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
            List<Listener> l = listeners.get(directory.getAbsolutePath());
            if (l == null) {
                l = new ArrayList<Listener>();
                listeners.put(directory.getAbsolutePath(), l);
            }
            l.add(listener);
            final List<Listener> fl = l;
            return new Monitor() {
                public void stop() {
                    synchronized (lock) {
                        LOG.info(String.format("Stopping monitoring %s", directory.getAbsolutePath()));
                        fl.remove(listener);
                        if (fl.isEmpty()) {
                            listeners.remove(directory.getAbsolutePath());
                        }
                    }
                }
            };
        }
    }

    public void start() {
        final Thread thread = new Thread(this, "FileMonitor");
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        stop = true;
    }

    public void run() {
        while (!stop) {
            try {
                WatchKey watchKey = watcher.poll(60, TimeUnit.SECONDS);
                if (watchKey != null) {
                    List<WatchEvent<?>> events = watchKey.pollEvents();
                    for (WatchEvent event : events) {
                        Path watchedPath = (Path) watchKey.watchable();
                        //returns the event type

                        WatchEvent.Kind eventKind = event.kind();
                        //returns the context of the event

                        Path target = (Path) event.context();
                        synchronized (lock) {
                            File f = new File(watchedPath.toFile(), target.toString());
                            List<Listener> l = listeners.get(f.getParentFile().getAbsolutePath());
                            if (l != null) {
                                LOG.info("Got file event f = " + f + " / " + eventKind + " (" + eventKind.getClass() + ")");
                                for (int i = l.size() - 1; i >= 0; i--) {
                                    if (eventKind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
                                        l.get(i).fileCreated(f);
                                    } else if (eventKind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                                        l.get(i).fileDeleted(f);
                                    } else if (eventKind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                                        l.get(i).fileUpdated(f);
                                    }
                                }
                            }
                        }
                    }
                    if (!watchKey.reset()) {
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
