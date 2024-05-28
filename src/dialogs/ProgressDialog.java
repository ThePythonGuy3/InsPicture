package dialogs;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog extends JDialog
{
    private final JProgressBar progressBar;
    private final JButton cancelButton;

    public ProgressDialog(JFrame owner)
    {
        super(owner, Dialog.ModalityType.APPLICATION_MODAL);

        setIconImage(owner.getIconImage());
        setTitle("InsPicture - Exporting");

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        progressBar = new JProgressBar();

        constraints.insets = new Insets(10, 10, 5, 10);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 1.0;
        progressBar.setPreferredSize(new Dimension(300, 20));
        add(progressBar, constraints);

        cancelButton = new JButton("Cancel");

        constraints.insets = new Insets(5, 10, 10, 10);
        constraints.gridy = 1;
        constraints.weightx = 0.5;
        add(cancelButton, constraints);

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setResizable(false);
        pack();
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.width - getWidth()) / 4);
    }

    public void SetProgress(int progress)
    {
        progressBar.setValue(progress);
        progressBar.repaint();
    }

    public JButton GetButton()
    {
        return cancelButton;
    }
}
