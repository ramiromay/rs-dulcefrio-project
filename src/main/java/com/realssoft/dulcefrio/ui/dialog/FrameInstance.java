package com.realssoft.dulcefrio.ui.dialog;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.swing.JFrame;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FrameInstance
{

    @Getter
    private static JFrame instance;

    public static void setInstance(JFrame frameMain)
    {
        instance = frameMain;
    }

}
