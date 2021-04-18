package com.skifer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class logWindow extends JFrame {

    private Settings settings;
    private Object SQLResult[][];
    private output out;
    public logWindow(Settings settings) {

        super("Autotime");
        this.settings = settings;
        setSize (800, 400);
        out = new output();
        out.setSQLResult(SQLResult);
        System.out.println(this.getSize());
        Otmetka otmetka = new Otmetka(this.getSize(), settings.getReportPath(), settings.getOtchetPath());

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);

        tabs.addTab("Расписание", out);
        tabs.addTab("Отметить", otmetka);
        tabs.addTab("Настройки", settings);
        tabs.setMnemonicAt(0, String.valueOf(1).charAt(0));
        add(tabs);

        otmetka.getCbSearchType().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                otmetka.setSQLResult(SQLResult);
                otmetka.cbPaint();
                repaint();
            }
        });

        otmetka.getOtchet().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                otmetka.setSQLResult(SQLResult);
                otmetka.setReport();
            }
        });


        otmetka.getSave().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                otmetka.setSQLResult(SQLResult);
                otmetka.save();
            }
        });

    }

    public output getOut() {
        return this.out;
    }

    public void draw()
    {
        out.setSQLResult(SQLResult);
        out.paintTable();
        repaint();
    }

    public void setQuery(Object[][] rs) {
        this.SQLResult = rs;
    }
}
