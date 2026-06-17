package kireiko.dev.anticheat.api.data;

import kireiko.dev.anticheat.MX;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;

public class Metrics {

    private final MetricsBase metricsBase;

    public Metrics(int serviceId) {
        String serverUUID = UUID.randomUUID().toString();
        boolean enabled = true;
        boolean logErrors = false;
        boolean logSentData = false;
        boolean logResponseStatusText = false;

        metricsBase = new MetricsBase(
                "minestom",
                serverUUID,
                serviceId,
                enabled,
                this::appendPlatformData,
                this::appendServiceData,
                null,
                () -> true,
                (message, error) -> MX.getLogger().log(Level.WARNING, message, error),
                (message) -> MX.getLogger().log(Level.INFO, message),
                logErrors,
                logSentData,
                logResponseStatusText,
                false);
    }

    public void shutdown() {
        metricsBase.shutdown();
    }

    public void addCustomChart(CustomChart chart) {
        metricsBase.addCustomChart(chart);
    }

    private void appendPlatformData(JsonObjectBuilder builder) {
        builder.appendField("playerAmount", net.minestom.server.MinecraftServer.getConnectionManager().getOnlinePlayers().size());
        builder.appendField("onlineMode", 1);
        builder.appendField("minestomVersion", net.minestom.server.MinecraftServer.VERSION_NAME);
        builder.appendField("minestomName", "Minestom");
        builder.appendField("javaVersion", System.getProperty("java.version"));
        builder.appendField("osName", System.getProperty("os.name"));
        builder.appendField("osArch", System.getProperty("os.arch"));
        builder.appendField("osVersion", System.getProperty("os.version"));
        builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
    }

    private void appendServiceData(JsonObjectBuilder builder) {
        builder.appendField("pluginVersion", "5.4");
    }

    public static class MetricsBase {
        public static final String METRICS_VERSION = "3.1.0";
        private static final String REPORT_URL = "https://bStats.org/api/v2/data/%s";
        private final ScheduledExecutorService scheduler;
        private final String platform;
        private final String serverUuid;
        private final int serviceId;
        private final Consumer<JsonObjectBuilder> appendPlatformDataConsumer;
        private final Consumer<JsonObjectBuilder> appendServiceDataConsumer;
        private final Consumer<Runnable> submitTaskConsumer;
        private final Supplier<Boolean> checkServiceEnabledSupplier;
        private final BiConsumer<String, Throwable> errorLogger;
        private final Consumer<String> infoLogger;
        private final boolean logErrors;
        private final boolean logSentData;
        private final boolean logResponseStatusText;
        private final Set<CustomChart> customCharts = new HashSet<>();
        private final boolean enabled;

        public MetricsBase(String platform, String serverUuid, int serviceId, boolean enabled,
                           Consumer<JsonObjectBuilder> appendPlatformDataConsumer,
                           Consumer<JsonObjectBuilder> appendServiceDataConsumer,
                           Consumer<Runnable> submitTaskConsumer,
                           Supplier<Boolean> checkServiceEnabledSupplier,
                           BiConsumer<String, Throwable> errorLogger,
                           Consumer<String> infoLogger,
                           boolean logErrors, boolean logSentData, boolean logResponseStatusText,
                           boolean skipRelocateCheck) {
            ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, task -> {
                Thread thread = new Thread(task, "bStats-Metrics");
                thread.setDaemon(true);
                return thread;
            });
            scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
            this.scheduler = scheduler;
            this.platform = platform;
            this.serverUuid = serverUuid;
            this.serviceId = serviceId;
            this.enabled = enabled;
            this.appendPlatformDataConsumer = appendPlatformDataConsumer;
            this.appendServiceDataConsumer = appendServiceDataConsumer;
            this.submitTaskConsumer = submitTaskConsumer;
            this.checkServiceEnabledSupplier = checkServiceEnabledSupplier;
            this.errorLogger = errorLogger;
            this.infoLogger = infoLogger;
            this.logErrors = logErrors;
            this.logSentData = logSentData;
            this.logResponseStatusText = logResponseStatusText;
            if (enabled) {
                startSubmitting();
            }
        }

        public void addCustomChart(CustomChart chart) {
            this.customCharts.add(chart);
        }

        public void shutdown() {
            scheduler.shutdown();
        }

        private void startSubmitting() {
            final Runnable submitTask = () -> {
                if (!enabled || !checkServiceEnabledSupplier.get()) {
                    scheduler.shutdown();
                    return;
                }
                if (submitTaskConsumer != null) {
                    submitTaskConsumer.accept(this::submitData);
                } else {
                    this.submitData();
                }
            };
            long initialDelay = (long) (1000 * 60 * (3 + Math.random() * 3));
            long secondDelay = (long) (1000 * 60 * (Math.random() * 30));
            scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS);
            scheduler.scheduleAtFixedRate(submitTask, initialDelay + secondDelay, 1000 * 60 * 30, TimeUnit.MILLISECONDS);
        }

        private void submitData() {
            final JsonObjectBuilder baseJsonBuilder = new JsonObjectBuilder();
            appendPlatformDataConsumer.accept(baseJsonBuilder);
            final JsonObjectBuilder serviceJsonBuilder = new JsonObjectBuilder();
            appendServiceDataConsumer.accept(serviceJsonBuilder);
            JsonObjectBuilder.JsonObject[] chartData = customCharts.stream()
                    .map(customChart -> customChart.getRequestJsonObject(errorLogger, logErrors))
                    .filter(Objects::nonNull)
                    .toArray(JsonObjectBuilder.JsonObject[]::new);
            serviceJsonBuilder.appendField("id", serviceId);
            serviceJsonBuilder.appendField("customCharts", chartData);
            baseJsonBuilder.appendField("service", serviceJsonBuilder.build());
            baseJsonBuilder.appendField("serverUUID", serverUuid);
            baseJsonBuilder.appendField("metricsVersion", METRICS_VERSION);
            JsonObjectBuilder.JsonObject data = baseJsonBuilder.build();
            scheduler.execute(() -> {
                try {
                    sendData(data);
                } catch (Exception e) {
                    if (logErrors) {
                        errorLogger.accept("Could not submit bStats metrics data", e);
                    }
                }
            });
        }

        private void sendData(JsonObjectBuilder.JsonObject data) throws Exception {
            if (logSentData) {
                infoLogger.accept("Sent bStats metrics data: " + data.toString());
            }
            String url = String.format(REPORT_URL, platform);
            HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
            byte[] compressedData = compress(data.toString());
            connection.setRequestMethod("POST");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Connection", "close");
            connection.addRequestProperty("Content-Encoding", "gzip");
            connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Metrics-Service/1");
            connection.setDoOutput(true);
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.write(compressedData);
            }
            StringBuilder builder = new StringBuilder();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }
            }
            if (logResponseStatusText) {
                infoLogger.accept("Sent data to bStats and received response: " + builder);
            }
        }

        private static byte[] compress(final String str) throws IOException {
            if (str == null) return null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(outputStream)) {
                gzip.write(str.getBytes(StandardCharsets.UTF_8));
            }
            return outputStream.toByteArray();
        }
    }

    public static class SingleLineChart extends CustomChart {
        private final Callable<Integer> callable;

        public SingleLineChart(String chartId, Callable<Integer> callable) {
            super(chartId);
            this.callable = callable;
        }

        @Override
        protected JsonObjectBuilder.JsonObject getChartData() throws Exception {
            int value = callable.call();
            if (value == 0) return null;
            return new JsonObjectBuilder().appendField("value", value).build();
        }
    }

    public abstract static class CustomChart {
        private final String chartId;

        protected CustomChart(String chartId) {
            if (chartId == null) throw new IllegalArgumentException("chartId must not be null");
            this.chartId = chartId;
        }

        public JsonObjectBuilder.JsonObject getRequestJsonObject(BiConsumer<String, Throwable> errorLogger, boolean logErrors) {
            JsonObjectBuilder builder = new JsonObjectBuilder();
            builder.appendField("chartId", chartId);
            try {
                JsonObjectBuilder.JsonObject data = getChartData();
                if (data == null) return null;
                builder.appendField("data", data);
            } catch (Throwable t) {
                if (logErrors) errorLogger.accept("Failed to get data for custom chart with id " + chartId, t);
                return null;
            }
            return builder.build();
        }

        protected abstract JsonObjectBuilder.JsonObject getChartData() throws Exception;
    }

    public static class JsonObjectBuilder {
        private StringBuilder builder = new StringBuilder();
        private boolean hasAtLeastOneField = false;

        public JsonObjectBuilder() { builder.append("{"); }

        public JsonObjectBuilder appendField(String key, String value) {
            if (value == null) throw new IllegalArgumentException("JSON value must not be null");
            appendFieldUnescaped(key, "\"" + escape(value) + "\"");
            return this;
        }

        public JsonObjectBuilder appendField(String key, int value) {
            appendFieldUnescaped(key, String.valueOf(value));
            return this;
        }

        public JsonObjectBuilder appendField(String key, JsonObject object) {
            if (object == null) throw new IllegalArgumentException("JSON object must not be null");
            appendFieldUnescaped(key, object.toString());
            return this;
        }

        public JsonObjectBuilder appendField(String key, JsonObject[] values) {
            if (values == null) throw new IllegalArgumentException("JSON values must not be null");
            String escapedValues = Arrays.stream(values).map(JsonObject::toString).collect(Collectors.joining(","));
            appendFieldUnescaped(key, "[" + escapedValues + "]");
            return this;
        }

        private void appendFieldUnescaped(String key, String escapedValue) {
            if (builder == null) throw new IllegalStateException("JSON has already been built");
            if (key == null) throw new IllegalArgumentException("JSON key must not be null");
            if (hasAtLeastOneField) builder.append(",");
            builder.append("\"").append(escape(key)).append("\":").append(escapedValue);
            hasAtLeastOneField = true;
        }

        public JsonObject build() {
            if (builder == null) throw new IllegalStateException("JSON has already been built");
            JsonObject object = new JsonObject(builder.append("}").toString());
            builder = null;
            return object;
        }

        private static String escape(String value) {
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                char c = value.charAt(i);
                if (c == '"') builder.append("\\\"");
                else if (c == '\\') builder.append("\\\\");
                else if (c <= '\u000F') builder.append("\\u000").append(Integer.toHexString(c));
                else if (c <= '\u001F') builder.append("\\u00").append(Integer.toHexString(c));
                else builder.append(c);
            }
            return builder.toString();
        }

        public static class JsonObject {
            private final String value;
            private JsonObject(String value) { this.value = value; }
            @Override public String toString() { return value; }
        }
    }
}
