package com.microsoft.azure.storage;

import com.microsoft.azure.storage.blob.*;
import com.microsoft.azure.storage.models.*;
import com.microsoft.rest.v2.RestResponse;
import com.microsoft.rest.v2.http.HttpPipeline;
import io.reactivex.observers.TestObserver;
import org.junit.*;
import org.junit.rules.TestName;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

public class ServiceURLTests {

    private static String accountName;

    private static String accountKey;

    private static HttpPipeline pipeline;

    private static ServiceURL serviceURL;

    private static String containerPrefix = "java";

    @Rule
    public final TestName name = new TestName();

    @BeforeClass
    public static void initializeURL() throws UnsupportedEncodingException, InvalidKeyException,
            MalformedURLException {
        ServiceURLTests.accountName = System.getenv("ACCOUNT_NAME");
        ServiceURLTests.accountKey = System.getenv("ACCOUNT_KEY");

        ServiceURLTests.pipeline = StorageURL.CreatePipeline(new SharedKeyCredentials(ServiceURLTests.accountName,
                ServiceURLTests.accountKey), new PipelineOptions());

        ServiceURLTests.serviceURL = new ServiceURL(new URL("http://" + accountName + ".blob.core.windows.net"),
                pipeline);
    }

    @After
    public void cleanup() {
        // Ensure that the service properties are reset.
        ServiceURLTests.serviceURL.setPropertiesAsync(new StorageServiceProperties()
                .withLogging(new Logging().withVersion("1.0")
                        .withRetentionPolicy(new RetentionPolicy().withEnabled(false)))
                .withCors(new ArrayList<CorsRule>())
                .withDefaultServiceVersion("2016-05-31")
                .withHourMetrics(new Metrics().withVersion("1.0").withEnabled(false)
                        .withRetentionPolicy(new RetentionPolicy().withEnabled(false)))
                .withMinuteMetrics(new Metrics().withVersion("1.0").withEnabled(false)
                        .withRetentionPolicy(new RetentionPolicy().withEnabled(false)))).blockingGet();

        // Delete any containers generated by these tests.
        List<Container> containers = ServiceURLTests.serviceURL.listConatinersAsync(ServiceURLTests.containerPrefix,
                null, null, null).blockingGet().body().containers();
        for (Container container : containers) {
            ContainerURL containerURL = ServiceURLTests.serviceURL.createContainerURL(container.name());
            containerURL.deleteAsync(null).blockingGet();
        }
    }

    public static String generateContainerName(String testName) {
        return new StringBuilder(ServiceURLTests.containerPrefix).append(testName).append(System.currentTimeMillis())
                .toString();
    }

    // SAMPLE
    @Test
    public void TestGetSetPropertiesFull() {
        RetentionPolicy retentionPolicy = new RetentionPolicy().withDays(5).withEnabled(true);
        Logging logging = new Logging().withRead(true).withVersion("1.0")
                .withRetentionPolicy(retentionPolicy);
        ArrayList<CorsRule> corsRules = new ArrayList<>();
        corsRules.add(new CorsRule().withAllowedMethods("GET,PUT,HEAD").withAllowedOrigins("*")
                .withAllowedHeaders("x-ms-version").withExposedHeaders("x-ms-client-request-id")
                .withMaxAgeInSeconds(10));
        String defaultServiceVersion = "2016-05-31";
        Metrics hourMetrics = new Metrics().withEnabled(true).withVersion("1.0").withRetentionPolicy(retentionPolicy);
        Metrics minuteMetrics = new Metrics().withEnabled(true).withVersion("1.0").withRetentionPolicy(retentionPolicy);

        ServiceURLTests.serviceURL.setPropertiesAsync(new StorageServiceProperties()
                .withLogging(logging).withCors(corsRules).withDefaultServiceVersion(defaultServiceVersion)
                .withHourMetrics(hourMetrics).withMinuteMetrics(minuteMetrics)).blockingGet();

        StorageServiceProperties receivedProperties = ServiceURLTests.serviceURL.getPropertiesAsync()
                .blockingGet().body();

        Assert.assertEquals(true, receivedProperties.logging().read());
        Assert.assertEquals(false, receivedProperties.logging().delete());
        Assert.assertEquals(false, receivedProperties.logging().write());
        Assert.assertEquals("1.0", receivedProperties.logging().version());
        Assert.assertEquals(5, receivedProperties.logging().retentionPolicy().days().intValue());
        Assert.assertEquals(true, receivedProperties.logging().retentionPolicy().enabled());

        Assert.assertEquals(1, receivedProperties.cors().size());
        Assert.assertEquals("GET,PUT,HEAD", receivedProperties.cors().get(0).allowedMethods());
        Assert.assertEquals("x-ms-version", receivedProperties.cors().get(0).allowedHeaders());
        Assert.assertEquals("*", receivedProperties.cors().get(0).allowedOrigins() );
        Assert.assertEquals("x-ms-client-request-id", receivedProperties.cors().get(0).exposedHeaders());
        Assert.assertEquals(10, receivedProperties.cors().get(0).maxAgeInSeconds());

        Assert.assertEquals("2016-05-31", receivedProperties.defaultServiceVersion());

        Assert.assertEquals(true, receivedProperties.hourMetrics().enabled());
        Assert.assertEquals(false, receivedProperties.hourMetrics().includeAPIs());
        Assert.assertEquals(true, receivedProperties.hourMetrics().retentionPolicy().enabled());
        Assert.assertEquals(5, receivedProperties.hourMetrics().retentionPolicy().days().intValue());
        Assert.assertEquals("1.0", receivedProperties.hourMetrics().version());

        Assert.assertEquals(true, receivedProperties.minuteMetrics().enabled());
        Assert.assertEquals(false, receivedProperties.minuteMetrics().includeAPIs());
        Assert.assertEquals(false, receivedProperties.minuteMetrics().retentionPolicy().enabled());
        Assert.assertEquals(5, receivedProperties.minuteMetrics().retentionPolicy().days().intValue());
        Assert.assertEquals("1.0", receivedProperties.minuteMetrics().version());
    }

    // SAMPLE
    @Test
    public void TestListContainersFull() {
        List<Container> containers = ServiceURLTests.serviceURL.listConatinersAsync("java", null,
                null, null).blockingGet().body().containers();
        Assert.assertEquals(0, containers.size());

        String containerName = generateContainerName(this.name.toString());
        ContainerURL containerURL = ServiceURLTests.serviceURL.createContainerURL(containerName);

    }
}