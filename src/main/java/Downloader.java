import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import static com.codeborne.selenide.Selenide.*;

/**
 * Created by Denis on 5/21/2020.
 */
public class Downloader {
    static String STRAVA_TRAININGS_LIST = "https://www.strava.com/athlete/training";
    static String NEXT_PAGE_LOCATOR = "//ul[@class='switches']/li/button[contains(@class,'next_page')]";
    static String THROUGH_FACEBOOK_LOGIN_BUTTON = "//div[@class='facebook']/a";

    static String ACTIVITIES_LOCATOR = "//tbody/tr[@class='training-activity-row']/td/a[@data-field-name='name']";
    static long VERY_BIG_TIMEOUT = 600000;

    static void createNewDir(File currentDir) {
        if (!currentDir.exists()) {
            currentDir.mkdir();
        } else {
            if (!currentDir.isDirectory()) {
                throw new RuntimeException("File with name exists");
            }
        }
    }

    public static void login_strava() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        $(By.xpath(THROUGH_FACEBOOK_LOGIN_BUTTON)).click();
    }

    public static LinkedList<String> read_trainings_from_ui(File file) throws IOException {
        Files.write(Paths.get(file.getAbsolutePath()), ("").getBytes(), StandardOpenOption.CREATE);
        LinkedList<String> activities = new LinkedList<String>();
        boolean nextPageExists = true;
        do {
            for (SelenideElement activity : $$(By.xpath(ACTIVITIES_LOCATOR))) {
                activities.add(activity.getAttribute("href"));
                Files.write(Paths.get(file.getAbsolutePath()), activity.getAttribute("href").getBytes(), StandardOpenOption.APPEND);
                Files.write(Paths.get(file.getAbsolutePath()), "\n".getBytes(), StandardOpenOption.APPEND);
            }
            nextPageExists = $(By.xpath(NEXT_PAGE_LOCATOR)).isEnabled();
            if (nextPageExists) {
                $(By.xpath(NEXT_PAGE_LOCATOR)).click();
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (nextPageExists);
        return activities;
    }

    public static List<String> read_trainings_from_file(File file) throws IOException {
        List<String> activities = new LinkedList<String>();
        activities = Files.readAllLines(Paths.get(file.getAbsolutePath()));
        return activities;
    }

    public static List<String> read_trainings_list() throws IOException {
        List<String> activities = new LinkedList<String>();
        File activities_links = new File("links.txt");
        if (activities_links.exists()) {
            activities = read_trainings_from_file(activities_links);
        } else {
            activities = read_trainings_from_ui(activities_links);
        }
        return activities;
    }

    public static void download_trainings(File currentDir, List<String> trainings) throws IOException {
        File activities_links = new File("downloaded_links.txt");
        File download_log = new File("log.txt");
        List<String> downloaded_activities = new LinkedList<String>();
        if (!download_log.exists()) {
            Files.write(Paths.get(download_log.getAbsolutePath()), ("").getBytes(), StandardOpenOption.CREATE);
        }
        if (!activities_links.exists()) {
            Files.write(Paths.get(activities_links.getAbsolutePath()), ("").getBytes(), StandardOpenOption.CREATE);
        } else {
            downloaded_activities = Files.readAllLines(Paths.get(activities_links.getAbsolutePath()));
        }
        String home = System.getProperty("user.home");
        File download_dir = new File(home + "/Downloads/");
        for (String training : trainings) {
            boolean link_downloaded = false;
            for (String downloaded_link : downloaded_activities) {
                if (training.equalsIgnoreCase(downloaded_link)) {
                    link_downloaded = true;
                    break;
                }
            }
            if (link_downloaded) {
                continue;
            }
            Files.write(Paths.get(download_log.getAbsolutePath()), ("Downloading training: " + training + "\n").getBytes(), StandardOpenOption.APPEND);
            File[] files_before = download_dir.listFiles();
            open(training + "/export_original");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File downloaded_training_file = null;
            for (File file_after : download_dir.listFiles()) {
                boolean file_found = false;
                for (File file_before : files_before) {
                    if (file_after.getAbsolutePath().equalsIgnoreCase(file_before.getAbsolutePath())) {
                        file_found = true;
                        break;
                    }
                }
                if (!file_found) {
                    downloaded_training_file = file_after;
                    break;
                }
            }
            if (downloaded_training_file == null) {
                Files.write(Paths.get(download_log.getAbsolutePath()), ("No file downloaded").getBytes(), StandardOpenOption.APPEND);
                continue;
            }

            String destination_file_name = training.substring(training.lastIndexOf("/")) + ".fit";
            File destination_file = new File(currentDir, destination_file_name);
            if (destination_file.exists()) {
                destination_file.delete();
            }
            Files.move(downloaded_training_file.toPath(), destination_file.toPath());
            Files.write(Paths.get(activities_links.getAbsolutePath()), training.getBytes(), StandardOpenOption.APPEND);
            Files.write(Paths.get(activities_links.getAbsolutePath()), "\n".getBytes(), StandardOpenOption.APPEND);
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        File currentDir = new File("SavedTrainings");
        createNewDir(currentDir);
        Configuration.browser = "chrome";
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        Configuration.timeout = VERY_BIG_TIMEOUT;
        Configuration.closeBrowserTimeoutMs = VERY_BIG_TIMEOUT;
        Configuration.collectionsTimeout = VERY_BIG_TIMEOUT;
        Configuration.openBrowserTimeoutMs = VERY_BIG_TIMEOUT;

        open(STRAVA_TRAININGS_LIST);
        login_strava();
        List<String> trainings = read_trainings_list();
        download_trainings(currentDir, trainings);
    }
}

