package components;

import dialogs.SelectKeyDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class LabelElement extends JComponent
{
    private final JTextField nameField;
    private final JButton removeButton;
    private final JButton keyButton;
    private final LabelElement thisReference = this;
    private int keyCode = -1;

    public LabelElement()
    {
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();

        removeButton = new JButton("-")
        {
            @Override
            public Dimension getPreferredSize()
            {
                return new Dimension(super.getPreferredSize().height, super.getPreferredSize().height);
            }
        };
        removeButton.setMargin(new Insets(0, 1, 0, 0));

        removeButton.addActionListener(e -> {
            Container parent = getParent();
            parent.remove(thisReference);
            parent.revalidate();
            parent.repaint();
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 10, 0, 5);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;

        add(removeButton, constraints);

        nameField = new JTextField("Label");

        constraints.gridx = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(0, 5, 0, 5);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;

        add(nameField, constraints);

        constraints.gridx = 2;
        constraints.fill = GridBagConstraints.NONE;
        constraints.insets = new Insets(0, 5, 0, 10);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.weightx = 0.0;

        keyButton = new JButton("Key");

        keyButton.addActionListener(e -> {
            SelectKeyDialog selectKeyDialog = new SelectKeyDialog((JFrame) getTopLevelAncestor());
            int keyCode = selectKeyDialog.GetKeyCode();

            if (keyCode != -1)
            {
                SetKeyCode(keyCode);
            }
        });

        add(keyButton, constraints);

        setMinimumSize(new Dimension(0, nameField.getPreferredSize().height + 20));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, nameField.getPreferredSize().height + 20));
    }

    public LabelElement(String label, int keyCode)
    {
        this();
        nameField.setText(label);
        SetKeyCode(keyCode);
    }

    public LabelElement(String label)
    {
        this();
        nameField.setText(label);
    }

    public void SetKeyCode(int keyCode)
    {
        this.keyCode = keyCode;
        keyButton.setText(KeyEvent.getKeyText(keyCode));
    }

    public int GetKeyCode()
    {
        return keyCode;
    }

    public void SetLabel(String label)
    {
        nameField.setText(label);
    }

    public String GetLabel()
    {
        return nameField.getText().replaceAll("(?i)^unlabeled$", "-Unlabeled-");
    }

    public void AllowDelete(boolean allow)
    {
        removeButton.setEnabled(allow);
    }

    public void AllowEdit(boolean allow)
    {
        nameField.setEnabled(allow);
        nameField.setEditable(allow);
    }

    public void AllowKey(boolean allow)
    {
        keyButton.setEnabled(allow);
    }
}
