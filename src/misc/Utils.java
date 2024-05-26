package misc;

import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Utils
{
    public static boolean Contains(String[] array, String lookFor)
    {
        boolean included = false;

        for (String object : array)
        {
            if (object.equalsIgnoreCase(lookFor))
            {
                included = true;
                break;
            }
        }

        return included;
    }

    public static ArrayList<FileGroup> WalkDirectory(File directory, String[] allowedExtensions)
    {
        try
        {
            ArrayList<FileGroup> output = new ArrayList<>();

            try (Stream<Path> stream = Files.walk(directory.toPath()))
            {
                stream.filter(e ->
                        Files.exists(e)
                                && Files.isRegularFile(e)
                                && Contains(allowedExtensions, FilenameUtils.getExtension(e.toString()))
                ).forEach(e -> {
                    File file = e.toFile();
                    String fileString = file.toString();

                    String fileGroupName = FilenameUtils.getFullPathNoEndSeparator(fileString) + Vars.fileSeparator + FilenameUtils.getBaseName(fileString);

                    boolean found = false;
                    for (int i = 0; i < output.size() && !found; i++)
                    {
                        FileGroup fileGroup = output.get(i);
                        if (fileGroup.GetName().equals(fileGroupName))
                        {
                            fileGroup.AddFile(file);
                            found = true;
                        }
                    }

                    if (output.isEmpty() || !found)
                    {
                        FileGroup newFileGroup = new FileGroup(fileGroupName);
                        newFileGroup.AddFile(file);

                        output.add(newFileGroup);
                    }
                });
            }

            return output;
        } catch (Exception ignored)
        {
        }

        return null;
    }

    public static boolean IsDescendantOf(Component ancestor, Component descendant)
    {
        Component parent = descendant;

        while (parent != null)
        {
            if (parent == ancestor)
            {
                return true;
            }

            parent = parent.getParent();
        }

        return false;
    }
}
