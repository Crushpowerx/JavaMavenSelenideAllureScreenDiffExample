package steps;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import org.testng.Assert;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

public class Steps {

    @Step
    public static void makeScreenshotExample(String expectedName) throws IOException {
        String expectedPath = "target/";
        String pathname = expectedPath + expectedName + ".png";
        Screenshot actual = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(getWebDriver());
        ImageIO.write(actual.getImage(), "png", new File(pathname));
    }

    @Step
    public static void makeImageDiff(String expectedName) throws IOException {
        // объявляем переменные
        String expectedPath = "target/";
        String screenPath = "target/";
        String actualName = "actual.png";
        String diffName = "diff.png";
        String fullPathNameExpected = expectedPath + expectedName + ".png";
        String fullPathNameActual = screenPath + actualName;
        String fullPathNameDiff = screenPath + diffName;

        // активируем плагин аллюра для дифф
        Allure.label("testType", "screenshotDiff");

        // делаем скриншот
        Screenshot actual = new AShot()
                .coordsProvider(new WebDriverCoordsProvider())
                .takeScreenshot(getWebDriver());
        ImageIO.write(actual.getImage(), "png", new File(fullPathNameActual));

        // сравниваем его с примером и сохраняем дифф
        Screenshot expected = new Screenshot(ImageIO.read(new File(fullPathNameExpected)));
        ImageDiff diff = new ImageDiffer().makeDiff(actual, expected);
        BufferedImage diffImage = diff.getMarkedImage();
        ImageIO.write(diffImage, "png", new File(fullPathNameDiff));

        // прикрепляем дифф в аллюр отчёт
        BufferedImage bImage1 = ImageIO.read(new File(fullPathNameDiff));
        ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
        ImageIO.write(bImage1, "png", bos1);
        byte[] data1 = bos1.toByteArray();
        Allure.addAttachment("diff", new ByteArrayInputStream(data1));

        // прикрепляем фактичесский скриншот в аллюр отчёт
        BufferedImage bImage2 = ImageIO.read(new File(fullPathNameActual));
        ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
        ImageIO.write(bImage2, "png", bos2);
        byte[] data2 = bos2.toByteArray();
        Allure.addAttachment("actual", new ByteArrayInputStream(data2));

        // прикрепляем скриншот-пример в аллюр отчёт
        BufferedImage bImage3 = ImageIO.read(new File(fullPathNameExpected));
        ByteArrayOutputStream bos3 = new ByteArrayOutputStream();
        ImageIO.write(bImage3, "png", bos3);
        byte[] data3 = bos3.toByteArray();
        Allure.addAttachment("expected", new ByteArrayInputStream(data3));

        // фейлим тест, если при сравнение разница в пикселях больше допустимой
        Assert.assertEquals(diff.getDiffSize(), 0);
    }

}
