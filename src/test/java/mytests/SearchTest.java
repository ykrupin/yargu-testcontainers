package mytests;

import com.codeborne.selenide.WebDriverRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.File;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Selenide.*;

public class SearchTest {
  private final static Logger LOG = LoggerFactory.getLogger(SearchTest.class);
  @Rule
  public BrowserWebDriverContainer browser =
      new BrowserWebDriverContainer()
          .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL, new File("target"))
          .withCapabilities(DesiredCapabilities.firefox()); // DesiredCapabilities.chrome()
  @Rule
  public GenericContainer app = new GenericContainer(DockerImageName.parse("getting-started"))
          .withExposedPorts(3000)
          .withLogConsumer(new Slf4jLogConsumer(LOG))
          .waitingFor(Wait.forLogMessage(".*Listening on port .*\\n", 1));

  @Before
  public void setUp() {
    RemoteWebDriver driver = browser.getWebDriver();
    System.out.println(browser.getVncAddress());
    WebDriverRunner.setWebDriver(driver);
  }

  @After
  public void tearDown() {
    WebDriverRunner.closeWebDriver();
  }

  @Test
  public void search() {
    System.out.println("App in container: " + app.getHost() + ":" + app.getFirstMappedPort());
    open("https://duckduckgo.com/");
    $(By.name("q")).val("codeborne").pressEnter();
    $$(".results .result").shouldHave(sizeGreaterThan(5));
  }
}
