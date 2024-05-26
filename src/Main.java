import com.github.jacksonbrienen.jwfd.JWindowsFileDialog;
import components.*;
import dialogs.ConfigureExtensionsDialog;
import misc.*;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main
{
    public static JFrame mainFrame;
    public static ReviewImageElement reviewImageElement;
    public static JLabel zoomRectSliderTitle, zoomAmountSliderTitle, labelsTitle, currentImageLabel, currentLabelLabel;
    public static JSlider zoomRectSlider, zoomAmountSlider;
    public static JPanel labelsPanel;
    public static JButton addLabelButton, previousImageButton, nextImageButton, directoryButton, setExtensionsButton, startButton, pauseButton, stopButton, exportButton;
    public static JScrollPane labelsScrollPane;
    private static File directory;
    private static ArrayList<FileGroup> files;
    private static Mode mode = Mode.NOMODE;
    private static int currentImage = 0;

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
        mainFrame.setIconImage(Vars.appIcon);

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
        constraints.gridheight = 9;
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
        constraints.insets.bottom = 10;
        constraints.insets.top = 0;
        mainFrame.add(zoomRectSlider, constraints);
        // --

        // Zoom Amount Slider Title
        zoomAmountSliderTitle = new JLabel("Zoom Amount (Original)");

        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.SOUTH;
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
        constraints.insets.bottom = 10;
        constraints.insets.top = 0;
        mainFrame.add(zoomAmountSlider, constraints);
        // --

        // Labels ScrollPane Title
        labelsTitle = new JLabel("Labels");

        constraints.gridy = 4;
        constraints.anchor = GridBagConstraints.SOUTH;
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

        addLabelButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                if (addLabelButton.isEnabled())
                {
                    LabelElement labelElement = new LabelElement();
                    labelElement.AllowDelete(mode == Mode.NOMODE);

                    labelsPanel.add(labelElement);
                    labelsPanel.revalidate();
                    labelsPanel.repaint();
                }
            }
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

        // Set Extensions Button
        setExtensionsButton = new JButton("Configure Extensions");

        setExtensionsButton.addActionListener(e -> {
            new ConfigureExtensionsDialog(mainFrame);
        });

        constraints.gridy = 8;
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
        constraints.gridy = 9;
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
                        for (Component component : labelsPanel.getComponents())
                        {
                            if (component instanceof LabelElement labelElement)
                            {
                                File newDir = new File(targetFile, labelElement.GetLabel());
                                newDir.mkdirs();
                            }
                        }

                        File unlabeledDir = new File(targetFile, "Unlabeled");
                        unlabeledDir.mkdirs();

                        for (FileGroup fileGroup : files)
                        {
                            for (File file : fileGroup.GetFiles())
                            {
                                try
                                {
                                    Files.copy(file.toPath(), new File(targetFile, (fileGroup.GetLabel() == null ? "Unlabeled" : fileGroup.GetLabel()) + Vars.fileSeparator + FilenameUtils.getName(file.toString())).toPath());
                                } catch (Exception ignored)
                                {
                                }
                            }
                        }

                        JOptionPane.showMessageDialog(mainFrame, "Export finished.", "InsPicture - Finished", JOptionPane.INFORMATION_MESSAGE);
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

            if (mainFrame.hasFocus() && mode == Mode.STARTED && !(event.getSource() instanceof JTextField) && !(event.getSource() instanceof JButton && e.getKeyCode() == KeyEvent.VK_ENTER) && !(event.getSource() instanceof JSlider && (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT)) && e.getID() == KeyEvent.KEY_PRESSED)
            {
                for (Component component : labelsPanel.getComponents())
                {
                    if (component instanceof LabelElement labelElement && e.getKeyCode() == labelElement.GetKeyCode())
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
                previousImageButton.setEnabled(false);
                nextImageButton.setEnabled(false);

                setExtensionsButton.setEnabled(true);

                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                exportButton.setEnabled(false);

                directoryButton.setEnabled(true);
                addLabelButton.setEnabled(true);

                for (Component component : labelsPanel.getComponents())
                    if (component instanceof LabelElement labelElement)
                    {
                        labelElement.AllowDelete(true);
                        labelElement.AllowEdit(true);
                        labelElement.AllowKey(true);
                    }
            }

            case STARTED ->
            {
                previousImageButton.setEnabled(true);
                nextImageButton.setEnabled(true);

                setExtensionsButton.setEnabled(false);

                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                exportButton.setEnabled(true);

                directoryButton.setEnabled(false);
                addLabelButton.setEnabled(false);

                for (Component component : labelsPanel.getComponents())
                    if (component instanceof LabelElement labelElement)
                    {
                        labelElement.AllowDelete(false);
                        labelElement.AllowEdit(false);
                        labelElement.AllowKey(false);
                    }
            }

            case PAUSED ->
            {
                previousImageButton.setEnabled(true);
                nextImageButton.setEnabled(true);

                setExtensionsButton.setEnabled(false);

                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                exportButton.setEnabled(true);

                directoryButton.setEnabled(false);
                addLabelButton.setEnabled(true);

                for (Component component : labelsPanel.getComponents())
                    if (component instanceof LabelElement labelElement)
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
            SetImage();
        }
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

            currentLabelLabel.setText("Label: " + (fileGroup.GetLabel() == null ? "NONE" : fileGroup.GetLabel()));
        }
    }

    public static void UpdateFiles()
    {
        if (files != null && !files.isEmpty())
        {
            if (currentImage > files.size())
                currentImage = files.size();

            currentImageLabel.setText(currentImage + "/" + files.size());
        }
        else
        {
            currentImage = 0;
            currentImageLabel.setText("0/0");
        }
    }

    public enum Mode
    {
        NOMODE,
        STARTED,
        PAUSED
    }
}