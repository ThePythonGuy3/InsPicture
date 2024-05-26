package dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectKeyDialog extends JDialog
{
    private final JLabel text;

    private int keyCode = -1;

    public SelectKeyDialog(JFrame owner)
    {
        super(owner, ModalityType.APPLICATION_MODAL);

        setIconImage(owner.getIconImage());
        setTitle("InsPicture - Select key");

        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        text = new JLabel("Press any key...");

        constraints.anchor = GridBagConstraints.CENTER;
        add(text, constraints);

        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() != KeyEvent.VK_UNDEFINED)
                {
                    keyCode = e.getKeyCode();

                    setVisible(false);
                }
            }
        });

        pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setResizable(false);
        setMinimumSize(new Dimension(250, 100));
        setLocation((screenSize.width - getWidth()) / 2, (screenSize.width - getWidth()) / 4);

        setVisible(true);
    }

    public int GetKeyCode()
    {
        return keyCode;
    }
}
