package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class ReviewImageElement extends JLabel
{
    private BufferedImage image;
    private boolean enableZoom = false, zoomed = false, forcedZoom = false;
    private float ratio = 1, invRatio = 1;
    private int zoomX = 0, zoomY = 0, extraZoom = 1;
    private int intendedZoomSide = 200, zoomSide = 1;
    private int rectX = 0, rectY = 0, rectW = 0, rectH = 0;

    public ReviewImageElement()
    {
        ReviewImageElementMouseListener mouseListener = new ReviewImageElementMouseListener();

        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);

        setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

        setHorizontalAlignment(SwingConstants.CENTER);
        setText("No image selected or image can't be displayed.");

        setFocusable(false);
    }

    public void SetImage(BufferedImage image)
    {
        this.image = image;
        if (image != null)
        {
            ratio = (float) image.getHeight() / image.getWidth();
            invRatio = 1f / ratio;
            AdjustZoomSide();
        }
        repaint();
    }

    public void SetZoomSide(int zoomSide)
    {
        if (image != null)
        {
            intendedZoomSide = zoomSide;
            this.zoomSide = Math.max(1, Math.min(Math.min(Math.min(Math.min(zoomSide, rectW), rectH), image.getWidth()), image.getHeight()));
        }
    }

    public void AdjustZoomSide()
    {
        SetZoomSide(intendedZoomSide);
    }

    public void ForceZoomedState(boolean state)
    {
        forcedZoom = state;
        zoomed = false;
        repaint();
    }

    public void ForceSoftZoomedState(boolean state)
    {
        if (state)
        {
            if (!zoomed)
            {
                zoomX = getWidth() / 2;
                zoomY = getHeight() / 2;
            }

            zoomed = state;
        }

        repaint();
    }

    public void SetExtraZoom(int extraZoom)
    {
        this.extraZoom = extraZoom;
        repaint();
    }

    public int GetExtraZoom()
    {
        return extraZoom;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(getBackground());
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (image != null)
        {
            int w, h;
            if (getWidth() * ratio <= getHeight())
            {
                w = getWidth();
                h = (int) (getWidth() * ratio);
            }
            else
            {
                w = (int) (getHeight() * invRatio);
                h = getHeight();
            }

            rectX = (getWidth() - w) / 2;
            rectY = (getHeight() - h) / 2;
            rectW = w;
            rectH = h;

            AdjustZoomSide();

            g2d.drawImage(image, (getWidth() - w) / 2, (getHeight() - h) / 2, w, h, null);

            if (zoomed || forcedZoom)
            {
                int zoomX = forcedZoom ? getWidth() / 2 : this.zoomX;
                int zoomY = forcedZoom ? getHeight() / 2 : this.zoomY;

                int zoomedX = zoomX - zoomSide / 2;
                int zoomedY = zoomY - zoomSide / 2;

                int sliceX = (int) (((float) (zoomX - rectX) / (float) rectW) * image.getWidth()) - zoomSide / 2;
                int sliceY = (int) (((float) (zoomY - rectY) / (float) rectH) * image.getHeight()) - zoomSide / 2;

                sliceX = Math.clamp(sliceX, 0, image.getWidth() - zoomSide);
                sliceY = Math.clamp(sliceY, 0, image.getHeight() - zoomSide);

                zoomedX = Math.clamp(zoomedX, rectX, rectX + rectW - zoomSide);
                zoomedY = Math.clamp(zoomedY, rectY, rectY + rectH - zoomSide);

                float ratio = (float) zoomSide / (float) extraZoom;
                BufferedImage zoomedImage = image.getSubimage((int) (sliceX + (zoomSide - ratio) / 2f), (int) (sliceY + (zoomSide - ratio) / 2f), (int) ratio, (int) ratio);
                g2d.drawImage(zoomedImage, zoomedX, zoomedY, zoomSide, zoomSide, null);
            }
        }
        else
        {
            super.paintComponent(g2d);
        }
    }

    protected void UpdateZoom(MouseEvent e, boolean enableZoom)
    {
        this.enableZoom = enableZoom;
        UpdateZoom(e);
    }

    protected void UpdateZoom(MouseEvent e)
    {
        zoomX = e.getX();
        zoomY = e.getY();

        zoomed = enableZoom && (zoomX >= rectX && zoomY >= rectY && zoomX < (rectX + rectW) && zoomY < (rectY + rectH));
        repaint();
    }

    private class ReviewImageElementMouseListener extends MouseAdapter
    {
        @Override
        public void mouseEntered(MouseEvent e)
        {
            UpdateZoom(e, true);
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            UpdateZoom(e, false);
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
            UpdateZoom(e);
        }

        @Override
        public void mouseMoved(MouseEvent e)
        {
            UpdateZoom(e);
        }
    }
}
