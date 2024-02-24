package com.realssoft.dulcefrio.ui.utils;

import com.formdev.flatlaf.FlatClientProperties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.DocumentFilter;
import javax.swing.text.NumberFormatter;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ComponentsUtils
{


    public static void setRendering(Graphics2D graphics2D)
    {
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics2D.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_DITHERING,
                RenderingHints.VALUE_DITHER_ENABLE);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }

    @Contract("_ -> new")
    public static @NotNull DefaultTableModel getTableModel(Class... types)
    {
        return  new DefaultTableModel() {

            public Class getColumnClass(int i)
            {
                return types[i];
            }
            @Override
            public boolean isCellEditable(int filas, int columnas)
            {
                return false;
            }

        };
    }

    public static @NotNull JButton createButton(String path)
    {
        JButton button = new JButton();
        button.setIcon(SVGUtils.getSVGIcon(path));
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.putClientProperty(FlatClientProperties.STYLE, "" +
                "background:$background.card" +
                "borderWidth:0;" +
                "focusWidth:0;" +
                "innerFocusWidth:0;" +
                "arc:999;" +
                "hoverBackground:$SearchBar;"
        );
        return button;
    }

    public static JSpinner createSpinner()
    {
        // Se crea un SpinnerNumberModel para manejar los valores del JSpinner
        // Los parámetros del constructor son (valorInicial, valorMínimo, valorMáximo, paso)
        SpinnerNumberModel priceSpinnerModel = new SpinnerNumberModel(0d, 0d, null, 1d);


        // Se crea un JSpinner y se le asigna el SpinnerNumberModel
        JSpinner priceSpinner = new JSpinner(priceSpinnerModel);

        // Se crea un NumberEditor para personalizar el formato del JSpinner
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(priceSpinner, "#,##0.00");

        // Se obtiene el JTextField del NumberEditor para personalizar su apariencia
        JFormattedTextField textField = editor.getTextField();


        // Se establece el número de columnas para el JTextField
        textField.setColumns(12);

        // Se establece el editor personalizado en el JSpinner
        priceSpinner.setEditor(editor);
        return priceSpinner;
    }



}
