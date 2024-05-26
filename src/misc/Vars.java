package misc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.stream.Stream;

public class Vars
{
    public static final Image appIcon;
    public static final String[] displayableExtensions = {"jpg", "jpeg", "png", "gif", "bmp"};
    public static final int buttonsDownMask = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
    public static final String fileSeparator = FileSystems.getDefault().getSeparator();
    private static final String[] extraExtensions = {"crw", "cr2", "cr3"};
    public static ArrayList<String> allowedExtensions;

    static
    {
        Image appIcon_ = null;

        try
        {
            appIcon_ = ImageIO.read(new File("./resources/icon.png"));
        } catch (Exception ignored)
        {
        }

        appIcon = appIcon_;
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
