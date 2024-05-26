package components;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.event.*;

public class AbsoluteSlider extends JSlider
{
    public AbsoluteSlider(int min, int max, int value)
    {
        super(min, max, value);

        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent e)
            {
                // Click for absolute positioning
                JSlider slider = (JSlider) e.getSource();
                BasicSliderUI ui = (BasicSliderUI) slider.getUI();
                int value = ui.valueForXPosition(e.getX());
                slider.setValue(value);
            }
        });
    }
}
