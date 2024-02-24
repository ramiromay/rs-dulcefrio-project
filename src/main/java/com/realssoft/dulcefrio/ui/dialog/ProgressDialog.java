package com.realssoft.dulcefrio.ui.dialog;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.util.UIScale;
import com.realssoft.dulcefrio.ui.rs.PathRS;
import com.realssoft.dulcefrio.ui.utils.SVGUtils;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import java.awt.Dimension;

public class ProgressDialog extends JDialog
{

    private JPanel mainPanel;
    private JLabel titleState;
    private JProgressBar progressBar;
    private JLabel message;

    public ProgressDialog()
    {
        super(FrameInstance.getInstance(), true);
        configureComponents();
        configureLayout();
        configureProperties();
    }

    private void configureProperties()
    {
        setResizable(false);
        setSize(UIScale.scale(new Dimension(400, 130)));
        setLocationRelativeTo(FrameInstance.getInstance());
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public void setIconAndTitle(String title, String imagePath)
    {
        setIconImage(((ImageIcon)SVGUtils.getSVGIcon(PathRS.SVG_MENU_ITEM.formatted(imagePath))).getImage());
        setTitle(title);
    }

    private void configureComponents()
    {
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(0,10,10,10));
        setContentPane(mainPanel);

        titleState = new JLabel();
        titleState.putClientProperty(FlatClientProperties.STYLE, ""
                + "font:bold;"
                + "foreground:$blackColor-1;"
        );
        mainPanel.add(titleState);

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.putClientProperty(FlatClientProperties.STYLE, ""
                + "arc: 999;"
        );
        mainPanel.add(progressBar);

        message = new JLabel();
        mainPanel.add(message);
    }

    private void configureLayout()
    {
        GroupLayout layout = new GroupLayout(mainPanel);
        mainPanel.setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(titleState, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(progressBar, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
                .addComponent(message, GroupLayout.DEFAULT_SIZE,
                        100, Short.MAX_VALUE)
        );

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(titleState, GroupLayout.DEFAULT_SIZE,
                        50, GroupLayout.PREFERRED_SIZE)
                .addComponent(progressBar, GroupLayout.DEFAULT_SIZE,
                        22, GroupLayout.PREFERRED_SIZE)
                .addComponent(message, GroupLayout.DEFAULT_SIZE,
                        50, GroupLayout.PREFERRED_SIZE)
        );
    }

    public void setMaximum(int maximum)
    {
        progressBar.setMaximum(maximum);
    }

    public void setCompleteState(String title, String message)
    {
        titleState.setText(title);
        this.message.setText(message);
        progressBar.putClientProperty(FlatClientProperties.STYLE, ""
                + "foreground:#65c728;"
        );
    }

    public void setWaitingState(String title, String message)
    {
        titleState.setText(title);
        this.message.setText(message);
    }

    public void setUndefined(String message)
    {
        titleState.setText("Procesando...");
        this.message.setText(message);
        progressBar.setIndeterminate(true);
    }

    public void updateProgress(String message, int process)
    {
        this.message.setText(message);
        progressBar.setValue(process);
    }

}
