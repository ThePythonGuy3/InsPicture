package misc;

import com.github.jacksonbrienen.jwfd.FileExtension;
import org.apache.commons.io.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class Vars
{
    public static final Image appIcon;
    public static final ArrayList<Image> appIcons;
    public static final String[] displayableExtensions = {"jpg", "jpeg", "png", "gif", "bmp"};
    public static final FileExtension saveExtension = new FileExtension("InsPicture save files", "inspicture");
    public static final int buttonsDownMask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
    public static final String fileSeparator = FileSystems.getDefault().getSeparator();
    private static final String[] extraExtensions = {"crw", "cr2", "cr3"};
    public static ArrayList<String> allowedExtensions;

    static
    {
        Image appIcon_ = null;

        ArrayList<Image> images = new ArrayList<>();

        try
        {
            appIcon_ = ImageIO.read(new File("./resources/icon.png"));

            try (Stream<Path> stream = Files.walk(new File("./resources/iconRes/").toPath()))
            {
                stream.filter(e ->
                        Files.exists(e)
                                && Files.isRegularFile(e)
                                && FilenameUtils.getExtension(e.toString()).equals("png")
                                && FilenameUtils.getName(e.toString()).startsWith("icon")
                ).forEach(e -> {
                    File file = e.toFile();
                    try
                    {
                        Image image = ImageIO.read(file);
                        images.add(image);
                    } catch (Exception ignored)
                    {
                    }
                });
            }
        } catch (Exception ignored)
        {
        }

        appIcon = appIcon_;
        appIcons = images;
    }

    static
    {
        allowedExtensions = new ArrayList<>(Stream.concat(Arrays.stream(displayableExtensions), Arrays.stream(extraExtensions)).toList());
    }

    //public static final BufferedImage test;

    /*static
    {
        try
        {
            test = ImageIO.read(new File("resources/test.jpg"));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }*/
}
