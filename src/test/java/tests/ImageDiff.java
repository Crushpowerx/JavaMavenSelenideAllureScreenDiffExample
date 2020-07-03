package tests;

import org.testng.annotations.Test;
import runners.Debug;

import java.io.IOException;

import static com.codeborne.selenide.Selenide.open;
import static steps.Steps.makeImageDiff;
import static steps.Steps.makeScreenshotExample;

public class ImageDiff extends Debug {

    @Test
    public void testImageDiff() throws IOException {
        open("https://google.com/");
        makeScreenshotExample("expected");
        open("https://google.com/doodles/");
        makeImageDiff("expected");
    }

}
