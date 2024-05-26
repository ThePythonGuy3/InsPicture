package dialogs;

import misc.Vars;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ConfigureExtensionsDialog extends JDialog
{
    private final JLabel infoLabel;
    private final JScrollPane scrollPane;
    private final JTextArea extensionsField;

    public ConfigureExtensionsDialog(JFrame owner)
    {
        super(owner, ModalityType.APPLICATION_MODAL);

        setIconImage(owner.getIconImage());
        setTitle("InsPicture - Configure Extensions");

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        infoLabel = new JLabel("One file extension per line");

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1.0;
        add(infoLabel, constraints);

        extensionsField = new JTextArea();
        scrollPane = new JScrollPane(extensionsField);

        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1.0;
        add(scrollPane, constraints);

        for (String extension : Vars.allowedExtensions)
        {
            extensionsField.append(extension + "\n");
        }

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setResizable(false);
        setMinimumSize(new Dimension(250, 400));
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.width - getWidth()) / 4);

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                ArrayList<String> newAllowedExtensions = new ArrayList<>();

                for (String extension : extensionsField.getText().split("\\r?\\n"))
                {
                    newAllowedExtensions.add(extension.replaceAll("\\s?", ""));
                }

                Vars.allowedExtensions = newAllowedExtensions;
            }
        });

        setVisible(true);
    }
}
