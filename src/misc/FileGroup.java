package misc;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

public class FileGroup
{
    private final ArrayList<File> files;
    private final String name;
    private String label;

    public FileGroup(String name)
    {
        this.name = name;
        files = new ArrayList<>();
    }

    public String GetName()
    {
        return name;
    }

    public File[] GetFiles()
    {
        return files.toArray(new File[0]);
    }

    public void AddFile(File file)
    {
        files.add(file);
    }

    public void SetLabel(String label)
    {
        this.label = label;
    }

    public String GetLabel()
    {
        return label;
    }

    public File GetDisplayableFormat()
    {
        for (File file : files)
        {
            if (Utils.Contains(Vars.displayableExtensions, FilenameUtils.getExtension(file.toString())))
            {
                return file;
            }
        }

        return null;
    }

    @Override
    public String toString()
    {
        ArrayList<String> extensions = new ArrayList<>();

        for (File file : files)
        {
            extensions.add(FilenameUtils.getExtension(file.toString()));
        }

        return "{" + name + ", " + extensions + "}";
    }
}
