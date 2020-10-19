package br.com.sauran.matcher.utils;

import org.slf4j.Logger;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AsyncInfoMonitor {
	
    private static final ScheduledExecutorService POOL = Executors.newSingleThreadScheduledExecutor(
            task -> new Thread(task, "AsyncInfoMonitor")
    );

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AsyncInfoMonitor.class);
    private static int availableProcessors = Runtime.getRuntime().availableProcessors();
    private static double cpuUsage = 0;
    private static long freeMemory = 0;
    private static double lastProcessCpuTime = 0;
    private static long lastSystemTime = 0;
    private static long maxMemory = 0;
    private static boolean started = false;
    private static int threadCount = 0;
    private static long totalMemory = 0;
    private static double vpsCPUUsage = 0;
    private static long vpsFreeMemory = 0;
    private static long vpsMaxMemory = 0;
    private static long vpsUsedMemory = 0;

    public static int getAvailableProcessors() {
        check();
        return availableProcessors;
    }

    public static double getCpuUsage() {
        check();
        return cpuUsage;
    }

    public static long getFreeMemory() {
        check();
        return freeMemory;
    }

    public static long getMaxMemory() {
        check();
        return maxMemory;
    }

    public static int getThreadCount() {
        check();
        return threadCount;
    }

    public static long getTotalMemory() {
        check();
        return totalMemory;
    }

    public static double getInstanceCPUUsage() {
        check();
        return vpsCPUUsage;
    }

    public static long getVpsFreeMemory() {
        check();
        return vpsFreeMemory;
    }

    public static long getVpsMaxMemory() {
        check();
        return vpsMaxMemory;
    }

    public static long getVpsUsedMemory() {
        check();
        return vpsUsedMemory;
    }

    public static void start() {
        if (started) throw new IllegalStateException("Already Started.");

        log.debug("Started AsyncInfoMonitor... Monitoring system statistics since now!");
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        ThreadMXBean thread = ManagementFactory.getThreadMXBean();
        Runtime r = Runtime.getRuntime();

        lastSystemTime = System.nanoTime();
        lastProcessCpuTime = calculateProcessCpuTime(os);

        POOL.scheduleAtFixedRate(() -> {
            threadCount = thread.getThreadCount();
            availableProcessors = r.availableProcessors();
            freeMemory = Runtime.getRuntime().freeMemory();
            maxMemory = Runtime.getRuntime().maxMemory();
            totalMemory = Runtime.getRuntime().totalMemory();
            cpuUsage = calculateCpuUsage(os);
            vpsCPUUsage = getInstanceCPUUsage(os);
            vpsFreeMemory = calculateVPSFreeMemory(os);
            vpsMaxMemory = calculateVPSMaxMemory(os);
            vpsUsedMemory = vpsMaxMemory - vpsFreeMemory;
        }, 1, 1, TimeUnit.SECONDS);
        started = true;
    }

    private static double calculateCpuUsage(OperatingSystemMXBean os) {
        long systemTime = System.nanoTime();
        double processCpuTime = calculateProcessCpuTime(os);

        double cpuUsage = (processCpuTime - lastProcessCpuTime) / ((double) (systemTime - lastSystemTime));

        lastSystemTime = systemTime;
        lastProcessCpuTime = processCpuTime;

        return cpuUsage / availableProcessors;
    }

    @SuppressWarnings("restriction")
	private static double calculateProcessCpuTime(OperatingSystemMXBean os) {
        return ((com.sun.management.OperatingSystemMXBean) os).getProcessCpuTime();
    }

    @SuppressWarnings("restriction")
	private static long calculateVPSFreeMemory(OperatingSystemMXBean os) {
        return ((com.sun.management.OperatingSystemMXBean) os).getFreePhysicalMemorySize();
    }

    @SuppressWarnings("restriction")
	private static long calculateVPSMaxMemory(OperatingSystemMXBean os) {
        return ((com.sun.management.OperatingSystemMXBean) os).getTotalPhysicalMemorySize();
    }

    private static void check() {
        if (!started) throw new IllegalStateException("AsyncInfoMonitor not started");
    }

    @SuppressWarnings("restriction")
	private static double getInstanceCPUUsage(OperatingSystemMXBean os) {
        vpsCPUUsage = ((com.sun.management.OperatingSystemMXBean) os).getSystemCpuLoad() * 100;
        return vpsCPUUsage;
    }
}
