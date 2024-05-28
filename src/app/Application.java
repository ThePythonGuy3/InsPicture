package app;

import com.github.jacksonbrienen.jwfd.JWindowsFileDialog;
import components.*;
import dialogs.*;
import misc.*;
import org.apache.commons.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class Application
{
    public static JFrame mainFrame;
    public static ReviewImageElement reviewImageElement;
    public static JLabel zoomRectSliderTitle, zoomAmountSliderTitle, labelsTitle, currentImageLabel, currentLabelLabel;
    public static JSlider zoomRectSlider, zoomAmountSlider;
    public static JPanel labelsPanel;
    public static JButton addLabelButton, previousImageButton, nextImageButton, directoryButton, setExtensionsButton, saveButton, loadButton, startButton, pauseButton, stopButton, exportButton;
    public static JScrollPane labelsScrollPane;
    private static File directory;
    private static ArrayList<FileGroup> files;
    private static Mode mode = Mode.NOMODE;
    private static int currentImage = 0;
    private static ProgressDialog progressDialog;
    private static boolean stop = false;

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException
    {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        ColorUIResource transparent = new ColorUIResource(new Color(0, 0, 0, 0));

        UIManager.put("Button.focus", transparent);
        UIManager.put("ToggleButton.focus", transparent);
        UIManager.put("CheckBox.focus", transparent);
        UIManager.put("TabbedPane.focus", transparent);
        UIManager.put("RadioButton.focus", transparent);
        UIManager.put("ComboBox.focus", transparent);

        mainFrame = new JFrame();
        mainFrame.setTitle("InsPicture - Picture Inspector");
        mainFrame.setIconImages(Vars.appIcons);

        progressDialog = new ProgressDialog(mainFrame);
        progressDialog.GetButton().addActionListener(e -> {
            stop = true;
        });

        mainFrame.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        // Review Image Element
        reviewImageElement = new ReviewImageElement();
        reviewImageElement.setFocusable(true);

        reviewImageElement.setBackground(Color.WHITE);
        reviewImageElement.setBorder(new LineBorder(new Color(130, 135, 144), 1));

        //reviewImageElement.SetImage(misc.Vars.test);

        reviewImageElement.addMouseWheelListener(new MouseAdapter()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                int finalZoom = (int) Math.clamp(reviewImageElement.GetExtraZoom() - Math.signum(e.getPreciseWheelRotation()), zoomAmountSlider.getMinimum(), zoomAmountSlider.getMaximum());
                zoomAmountSlider.setValue(finalZoom);
            }
        });

        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 10;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 10.0;
        constraints.weighty = 1.0;
        mainFrame.add(reviewImageElement, constraints);
        // --

        // Zoom Side Slider Title
        zoomRectSliderTitle = new JLabel("Zoom Area Side Length (200px)");

        constraints.gridx = 1;
        constraints.gridheight = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 0.0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.insets.bottom = 5;
        mainFrame.add(zoomRectSliderTitle, constraints);
        // --

        // Zoom Side Slider
        zoomRectSlider = new AbsoluteSlider(100, 600, 200);

        zoomRectSlider.setToolTipText("Set the zoom area side length in pixels");

        zoomRectSlider.setMajorTickSpacing(100);
        zoomRectSlider.setMinorTickSpacing(25);

        zoomRectSlider.setPaintTicks(true);
        zoomRectSlider.setPaintLabels(true);

        zoomRectSlider.setSnapToTicks(true);

        zoomRectSlider.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                reviewImageElement.ForceZoomedState(true);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                reviewImageElement.ForceZoomedState(false);
            }
        });

        zoomRectSlider.addChangeListener(e -> {
            int amount = ((int) (zoomRectSlider.getValue() / 25f)) * 25;
            zoomRectSliderTitle.setText("Zoom Area Side Length (" + amount + "px)");

            reviewImageElement.ForceSoftZoomedState(true);

            reviewImageElement.SetZoomSide(amount);
            reviewImageElement.repaint();
        });
        reviewImageElement.SetZoomSide(zoomRectSlider.getValue());

        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets.bottom = 10;
        constraints.insets.top = 0;
        mainFrame.add(zoomRectSlider, constraints);
        // --

        // Zoom Amount Slider Title
        zoomAmountSliderTitle = new JLabel("Zoom Amount (Original)");

        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets.bottom = 5;
        constraints.insets.top = 10;
        mainFrame.add(zoomAmountSliderTitle, constraints);
        // --

        // Zoom Side Slider
        zoomAmountSlider = new AbsoluteSlider(1, 10, 1);

        zoomAmountSlider.setToolTipText("Set the zoom amount");

        zoomAmountSlider.setMajorTickSpacing(1);

        zoomAmountSlider.setPaintTicks(true);
        zoomAmountSlider.setPaintLabels(true);

        zoomAmountSlider.setSnapToTicks(true);

        zoomAmountSlider.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                reviewImageElement.ForceZoomedState(true);
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                reviewImageElement.ForceZoomedState(false);
            }
        });

        zoomAmountSlider.addChangeListener(e -> {
            int value = zoomAmountSlider.getValue();
            zoomAmountSliderTitle.setText("Zoom Amount (" + (value == 1 ? "Original" : value + "x") + ")");

            reviewImageElement.ForceSoftZoomedState(true);

            reviewImageElement.SetExtraZoom(value);
        });

        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets.bottom = 10;
        constraints.insets.top = 0;
        mainFrame.add(zoomAmountSlider, constraints);
        // --

        // Labels ScrollPane Title
        labelsTitle = new JLabel("Labels");

        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets.bottom = 5;
        constraints.insets.top = 10;
        mainFrame.add(labelsTitle, constraints);
        // --

        // Labels Panel + ScrollPane
        labelsPanel = new JPanel();
        labelsPanel.setLayout(new BoxLayout(labelsPanel, BoxLayout.Y_AXIS));

        labelsPanel.add(new LabelElement("Keep", KeyEvent.VK_ENTER));
        labelsPanel.add(new LabelElement("Discard", KeyEvent.VK_ESCAPE));

        labelsPanel.setBackground(Color.WHITE);
        labelsScrollPane = new JScrollPane(labelsPanel);

        constraints.gridy = 5;
        constraints.anchor = GridBagConstraints.NORTH;
        constraints.insets.bottom = 10;
        constraints.insets.top = 0;
        constraints.weighty = 1.0;
        constraints.fill = GridBagConstraints.BOTH;
        mainFrame.add(labelsScrollPane, constraints);

        // --

        // Add Label Button
        addLabelButton = new JButton("+");

        addLabelButton.addActionListener(e -> {
            LabelElement labelElement = new LabelElement();
            labelElement.AllowDelete(mode == Mode.NOMODE);

            labelsPanel.add(labelElement);
            labelsPanel.revalidate();
            labelsPanel.repaint();
        });

        constraints.gridy = 6;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        mainFrame.add(addLabelButton, constraints);
        // --

        // Navigation Panel + Buttons + Label
        JPanel navigationPanel = new JPanel();

        navigationPanel.setLayout(new BoxLayout(navigationPanel, BoxLayout.X_AXIS));

        previousImageButton = new JButton("<");
        previousImageButton.addActionListener(e -> {
            if (currentImage > 1)
            {
                currentImage--;
                SetImage();
            }
        });

        currentImageLabel = new JLabel("0/0");

        nextImageButton = new JButton(">");
        nextImageButton.addActionListener(e -> {
            NextImage();
        });

        currentLabelLabel = new JLabel("Label: NONE");

        navigationPanel.add(previousImageButton);
        navigationPanel.add(Box.createHorizontalStrut(5));
        navigationPanel.add(currentImageLabel);
        navigationPanel.add(Box.createHorizontalStrut(5));
        navigationPanel.add(nextImageButton);
        navigationPanel.add(Box.createHorizontalStrut(5));
        navigationPanel.add(currentLabelLabel);
        navigationPanel.add(Box.createGlue());

        constraints.gridy = 7;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        mainFrame.add(navigationPanel, constraints);
        // --

        // Save/Load Panel + Buttons
        JPanel saveLoadPanel = new JPanel();

        saveLoadPanel.setLayout(new GridBagLayout());

        GridBagConstraints saveLoadPanelConstraints = new GridBagConstraints();

        saveButton = new JButton("Save");

        saveButton.addActionListener(e -> {
            String selection = JWindowsFileDialog.showSaveDialog(mainFrame, "InsPicture - Select Directory", Vars.saveExtension);

            if (selection != null)
            {
                String output = GenerateSaveFile();

                PrintWriter writer = null;
                try
                {
                    writer = new PrintWriter(selection, StandardCharsets.UTF_8);
                    writer.print(output);
                    writer.close();
                } catch (Exception ignored)
                {
                }
            }
        });

        loadButton = new JButton("Load");

        loadButton.addActionListener(e -> {
            String selection = JWindowsFileDialog.showOpenDialog(mainFrame, "InsPicture - Select Directory", Vars.saveExtension);

            if (selection != null)
            {
                try
                {
                    LoadSaveFile(FileUtils.readFileToString(new File(selection), StandardCharsets.UTF_8));
                } catch (Exception ignored)
                {
                }
            }
        });

        saveLoadPanelConstraints.weightx = 1.0;
        saveLoadPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        saveLoadPanel.add(saveButton, saveLoadPanelConstraints);

        saveLoadPanelConstraints.gridx = 1;
        saveLoadPanel.add(loadButton, saveLoadPanelConstraints);

        constraints.gridy = 8;
        mainFrame.add(saveLoadPanel, constraints);
        // --

        // Set Extensions Button
        setExtensionsButton = new JButton("Configure Extensions");

        setExtensionsButton.addActionListener(e -> {
            new ConfigureExtensionsDialog(mainFrame);
        });

        constraints.gridy = 9;
        mainFrame.add(setExtensionsButton, constraints);
        // --

        // Directory Button
        directoryButton = new JButton("Selected Directory: NONE");
        directoryButton.setHorizontalAlignment(SwingConstants.LEFT);

        directoryButton.addActionListener(e -> {
            String selection = JWindowsFileDialog.showDirectoryDialog(mainFrame, "InsPicture - Select Directory");

            SetDirectory(selection);
        });

        constraints.gridx = 0;
        constraints.gridy = 10;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 10.0;
        mainFrame.add(directoryButton, constraints);
        // --

        // Control Panel + Buttons
        JPanel controlPanel = new JPanel();

        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints controlPanelConstraints = new GridBagConstraints();
        startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            if (directory != null && directory.exists())
            {
                UpdateMode(Mode.STARTED);

                files = Utils.WalkDirectory(directory, Vars.allowedExtensions.toArray(new String[0]));
                currentImage = 1;

                SetImage();
            }
        });

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> {
            if (mode == Mode.PAUSED)
            {
                pauseButton.setText("Pause");
                UpdateMode(Mode.STARTED);
            }
            else
            {
                pauseButton.setText("Resume");
                UpdateMode(Mode.PAUSED);
            }
        });

        stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> {
            int confirm = JOptionPane.showOptionDialog(mainFrame, "Are you sure you want to stop? All progress will be lost.", "InsPicture - Confirm Stop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Yes", "No"}, "No");
            if (confirm == JOptionPane.OK_OPTION)
            {
                UpdateMode(Mode.NOMODE);
                files = null;
                SetImage();
            }
        });

        exportButton = new JButton("EXPORT");
        exportButton.addActionListener(e -> {
            int confirm = JOptionPane.showOptionDialog(mainFrame, "Are you sure you want to export? All files will be copied to their destination.", "InsPicture - Confirm Export", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Yes", "No"}, "No");
            if (confirm == JOptionPane.OK_OPTION)
            {
                String target = JWindowsFileDialog.showDirectoryDialog(mainFrame, "InsPicture - Choose Export Output");
                if (target != null)
                {
                    File targetFile = new File(target);

                    if (targetFile.isDirectory() && (targetFile.list() == null || targetFile.list().length == 0))
                    {
                        for (LabelElement labelElement : GetLabelElements())
                        {
                            File newDir = new File(targetFile, labelElement.GetLabel());
                            newDir.mkdirs();
                        }

                        File unlabeledDir = new File(targetFile, "Unlabeled");
                        unlabeledDir.mkdirs();

                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                int p = 0;
                                for (FileGroup fileGroup : files)
                                {
                                    if (stop)
                                        break;

                                    for (File file : fileGroup.GetFiles())
                                    {
                                        try
                                        {
                                            Files.copy(file.toPath(), new File(targetFile, (fileGroup.GetLabel() == null ? "Unlabeled" : fileGroup.GetLabel()) + Vars.fileSeparator + FilenameUtils.getName(file.toString())).toPath());
                                        } catch (Exception ignored)
                                        {
                                        }
                                    }

                                    progressDialog.SetProgress((int) (((float) (p + 1) / (float) files.size()) * 100f));

                                    p++;
                                }

                                progressDialog.setVisible(false);

                                JOptionPane.showMessageDialog(mainFrame, stop ? "Export Cancelled." : "Export finished.", "InsPicture - Finished", JOptionPane.INFORMATION_MESSAGE);

                                stop = false;
                            }
                        }).start();

                        SwingUtilities.invokeLater(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                progressDialog.setVisible(true);
                            }
                        });
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(mainFrame, "Target directory must be empty.", "InsPicture - Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        controlPanelConstraints.weightx = 1.0;
        controlPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        controlPanel.add(startButton, controlPanelConstraints);
        controlPanelConstraints.gridx = 1;
        controlPanel.add(pauseButton, controlPanelConstraints);
        controlPanelConstraints.gridx = 2;
        controlPanel.add(stopButton, controlPanelConstraints);
        controlPanelConstraints.gridx = 3;
        controlPanel.add(exportButton, controlPanelConstraints);

        constraints.gridx = 1;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;
        mainFrame.add(controlPanel, constraints);
        // --

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setMinimumSize(new Dimension(screenSize.width / 2, screenSize.height / 2));
        mainFrame.setLocation((screenSize.width - mainFrame.getWidth()) / 2, (screenSize.height - mainFrame.getHeight()) / 2);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Outside Click Unfocus
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event.getSource() instanceof JTextField) && (((MouseEvent) event).getModifiersEx() & Vars.buttonsDownMask) != 0)
                mainFrame.requestFocus();
        }, AWTEvent.MOUSE_EVENT_MASK);

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            KeyEvent e = (KeyEvent) event;

            if (mainFrame.hasFocus() && mode == Mode.STARTED && e.getID() == KeyEvent.KEY_PRESSED)
            {
                for (LabelElement labelElement : GetLabelElements())
                {
                    if (e.getKeyCode() == labelElement.GetKeyCode())
                    {
                        files.get(currentImage - 1).SetLabel(labelElement.GetLabel());

                        NextImage();
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);

        UpdateMode(mode);

        mainFrame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if (mode == Mode.NOMODE)
                {
                    mainFrame.dispose();
                }
                else
                {
                    int confirm = JOptionPane.showOptionDialog(mainFrame, "Are you sure you want to exit? All progress will be lost.", "InsPicture - Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, new Object[]{"Yes", "No"}, "No");
                    if (confirm == JOptionPane.OK_OPTION)
                    {
                        mainFrame.dispose();
                    }
                }
            }
        });

        reviewImageElement.setFocusable(false);

        mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    public static void UpdateMode(Mode modeIn)
    {
        mode = modeIn;

        switch (mode)
        {
            case NOMODE ->
            {
                zoomRectSlider.setFocusable(true);
                zoomAmountSlider.setFocusable(true);
                labelsScrollPane.setFocusable(true);

                previousImageButton.setEnabled(false);
                previousImageButton.setFocusable(true);
                nextImageButton.setEnabled(false);
                nextImageButton.setFocusable(true);

                saveButton.setEnabled(false);
                saveButton.setFocusable(true);
                loadButton.setEnabled(true);
                loadButton.setFocusable(true);

                setExtensionsButton.setEnabled(true);
                setExtensionsButton.setFocusable(true);

                startButton.setEnabled(true);
                startButton.setFocusable(true);
                pauseButton.setEnabled(false);
                pauseButton.setFocusable(true);
                stopButton.setEnabled(false);
                stopButton.setFocusable(true);
                exportButton.setEnabled(false);
                exportButton.setFocusable(true);

                directoryButton.setEnabled(true);
                directoryButton.setFocusable(true);
                addLabelButton.setEnabled(true);
                addLabelButton.setFocusable(true);

                for (LabelElement labelElement : GetLabelElements())
                {
                    labelElement.AllowDelete(true);
                    labelElement.AllowEdit(true);
                    labelElement.AllowKey(true);
                }
            }

            case STARTED ->
            {
                zoomRectSlider.setFocusable(false);
                zoomAmountSlider.setFocusable(false);
                labelsScrollPane.setFocusable(false);

                previousImageButton.setEnabled(true);
                previousImageButton.setFocusable(false);
                nextImageButton.setEnabled(true);
                nextImageButton.setFocusable(false);

                saveButton.setEnabled(false);
                saveButton.setFocusable(false);
                loadButton.setEnabled(false);
                loadButton.setFocusable(false);

                setExtensionsButton.setEnabled(false);
                setExtensionsButton.setFocusable(false);

                startButton.setEnabled(false);
                startButton.setFocusable(false);
                pauseButton.setEnabled(true);
                pauseButton.setFocusable(false);
                stopButton.setEnabled(true);
                stopButton.setFocusable(false);
                exportButton.setEnabled(true);
                exportButton.setFocusable(false);

                directoryButton.setEnabled(false);
                directoryButton.setFocusable(false);
                addLabelButton.setEnabled(false);
                addLabelButton.setFocusable(false);

                for (LabelElement labelElement : GetLabelElements())
                {
                    labelElement.AllowDelete(false);
                    labelElement.AllowEdit(false);
                    labelElement.AllowKey(false);
                }

                mainFrame.requestFocus();
            }

            case PAUSED ->
            {
                zoomRectSlider.setFocusable(true);
                zoomAmountSlider.setFocusable(true);
                labelsScrollPane.setFocusable(true);

                previousImageButton.setEnabled(true);
                previousImageButton.setFocusable(true);
                nextImageButton.setEnabled(true);
                nextImageButton.setFocusable(true);

                saveButton.setEnabled(true);
                saveButton.setFocusable(true);
                loadButton.setEnabled(false);
                loadButton.setFocusable(true);

                setExtensionsButton.setEnabled(false);
                setExtensionsButton.setFocusable(true);

                startButton.setEnabled(false);
                startButton.setFocusable(true);
                pauseButton.setEnabled(true);
                pauseButton.setFocusable(true);
                stopButton.setEnabled(true);
                stopButton.setFocusable(true);
                exportButton.setEnabled(true);
                exportButton.setFocusable(true);

                directoryButton.setEnabled(false);
                directoryButton.setFocusable(true);
                addLabelButton.setEnabled(true);
                addLabelButton.setFocusable(true);

                for (LabelElement labelElement : GetLabelElements())
                {
                    labelElement.AllowDelete(false);
                    labelElement.AllowEdit(false);
                    labelElement.AllowKey(true);
                }
            }
        }
    }

    public static void NextImage()
    {
        if (currentImage < files.size())
        {
            currentImage++;
        }

        SetImage();
    }

    public static void SetDirectory(String directoryString)
    {
        if (directoryString != null)
        {
            directoryButton.setText("Selected Directory: " + directoryString);
            directory = new File(directoryString);
        }
    }

    public static void SetImage()
    {
        UpdateFiles();

        reviewImageElement.SetImage(null);
        if (mode != Mode.NOMODE && !files.isEmpty() && (currentImage <= files.size()))
        {
            FileGroup fileGroup = files.get(currentImage - 1);
            File displayable = fileGroup.GetDisplayableFormat();

            if (displayable != null)
            {
                try
                {
                    reviewImageElement.SetImage(ImageIO.read(displayable));
                } catch (Exception ignored)
                {
                }
            }
        }

        UpdateUI();
    }

    public static void UpdateFiles()
    {
        if (files != null && !files.isEmpty())
        {
            currentImageLabel.setText(currentImage + "/" + files.size());
        }
        else
        {
            currentImage = 0;
            currentImageLabel.setText("0/0");
        }
    }

    public static ArrayList<LabelElement> GetLabelElements()
    {
        ArrayList<LabelElement> labelElements = new ArrayList<>();

        for (Component component : labelsPanel.getComponents())
            if (component instanceof LabelElement labelElement)
                labelElements.add(labelElement);

        return labelElements;
    }

    public static String GenerateSaveFile()
    {
        StringBuilder output = new StringBuilder();

        output.append(directory.toString());
        output.append("\n");
        output.append(currentImage);
        output.append("\n");

        ArrayList<LabelElement> labelElements = GetLabelElements();

        output.append(labelElements.size());
        output.append("\n");

        for (LabelElement labelElement : labelElements)
        {
            output.append(labelElement.GetLabel());
            output.append("\n");
            output.append(labelElement.GetKeyCode());
            output.append("\n");
        }

        output.append(files.size());
        output.append("\n");

        for (FileGroup fileGroup : files)
        {
            output.append(fileGroup.GetName());
            output.append("\n");
            output.append(String.join(",", Arrays.stream(fileGroup.GetFiles()).map(File::toString).toList().toArray(new String[0])));
            output.append("\n");
            output.append(fileGroup.GetLabel());
            output.append("\n");
        }

        output.append(output.toString().hashCode());

        return output.toString();
    }

    public static void LoadSaveFile(String contents)
    {
        boolean error = false;

        try
        {
            String[] contentArray = contents.split("\n");
            int hashCode = Integer.parseInt(contentArray[contentArray.length - 1]);

            String checkText = String.join("\n", Arrays.copyOfRange(contentArray, 0, contentArray.length - 1)) + "\n";

            if (checkText.hashCode() == hashCode)
            {
                SetDirectory(contentArray[0]);
                currentImage = Integer.parseInt(contentArray[1]);

                ArrayList<LabelElement> loadedLabelElements = new ArrayList<>();
                int labelCount = Integer.parseInt(contentArray[2]);
                int currentId = 3;
                for (int i = 0; i < labelCount; i++)
                {
                    LabelElement labelElement = new LabelElement(contentArray[currentId]);

                    int keyCode = Integer.parseInt(contentArray[currentId + 1]);
                    if (keyCode != -1) labelElement.SetKeyCode(keyCode);

                    loadedLabelElements.add(labelElement);
                    currentId += 2;
                }

                ArrayList<FileGroup> loadedFiles = new ArrayList<>();
                int fileCount = Integer.parseInt(contentArray[currentId]);
                currentId++;
                for (int i = 0; i < fileCount; i++)
                {
                    FileGroup fileGroup = new FileGroup(contentArray[currentId]);
                    fileGroup.SetLabel(contentArray[currentId + 2].equals("null") ? null : contentArray[currentId + 2]);

                    String[] fileGroupFiles = contentArray[currentId + 1].split(",");
                    for (String fileGroupFile : fileGroupFiles)
                    {
                        fileGroup.AddFile(new File(fileGroupFile));
                    }

                    loadedFiles.add(fileGroup);

                    currentId += 3;
                }

                labelsPanel.removeAll();

                for (LabelElement labelElement : loadedLabelElements)
                {
                    labelsPanel.add(labelElement);
                }

                files = loadedFiles;

                UpdateMode(Mode.STARTED);
                SetImage();
            }
            else
            {
                error = true;
            }
        } catch (Exception e)
        {
            error = true;
        }

        if (error)
        {
            JOptionPane.showMessageDialog(mainFrame, "The file provided is not valid.", "InsPicture - Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void UpdateUI()
    {
        if (files != null)
        {
            FileGroup fileGroup = files.get(currentImage - 1);

            currentLabelLabel.setText("Label: " + (fileGroup.GetLabel() == null ? "NONE" : fileGroup.GetLabel()));
        }
        else
        {
            currentLabelLabel.setText("Label: NONE");
        }
    }

    public enum Mode
    {
        NOMODE,
        STARTED,
        PAUSED
    }
}