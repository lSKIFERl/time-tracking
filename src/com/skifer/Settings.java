package com.skifer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class Settings extends JPanel{
    private String bdPath, reportPath, otchetPath, configPath;
    private JButton reportBtn, otchetBtn, configBtnOpen, configBtnSave;
    private JTextField bdField, reportField, otchetField, configField;

    public Settings() {

        GridLayout grid = new GridLayout(4, 3, 15, 10);

        JPanel pan = new JPanel();
        pan.setLayout(grid);

        configField = new JTextField(20);
        JPanel configPan = new JPanel();
        configPan.setLayout(new GridLayout(0, 2));
        configBtnOpen = new JButton("Выбрать");
        configBtnSave = new JButton("Сохранить");
        configPan.add(configBtnOpen);
        configPan.add(configBtnSave);
        pan.add(new JLabel("Конфигурации: "));
        pan.add(configField);
        pan.add(configPan);

        otchetBtn = new JButton("Выбрать");
        otchetField = new JTextField(20);
        pan.add(new JLabel("Папка для отчётов: "));
        pan.add(otchetField);
        pan.add(otchetBtn);

        reportBtn = new JButton("Выбрать");
        reportField = new JTextField(20);
        pan.add(new JLabel("Папка для докладных: "));
        pan.add(reportField);
        pan.add(reportBtn);

        bdField = new JTextField(20);
        pan.add(new JLabel("Адрес базы данных: "));
        pan.add(bdField);

        add(pan);

        configPath = System.getProperty("user.dir") + "\\Autotime\\config.txt";
        reportPath = "\\Autotime\\Отчётность\\Докладные и поощрительные";
        otchetPath = "\\Autotime\\Отчётность\\";
        bdPath = "jdbc:mysql://localhost:3306/empdb?serverTimezone=Europe/Moscow&useSSL=false";

        try {
            File file = new File(configPath);
            if(!file.exists()) {
                new File(System.getProperty("user.dir") + "\\Autotime\\").mkdir();
                file.createNewFile();
            }
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            if (line != null) {
                reportPath = line;
                line = reader.readLine();
            }
            if (line != null) {
                otchetPath = line;
                line = reader.readLine();
            }
            if (line != null) {
                bdPath = line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFileChooser fileChooser = new JFileChooser();

        configBtnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор конфигурации");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(Settings.this);
                if (result == JFileChooser.APPROVE_OPTION ) {
                    configPath = fileChooser.getSelectedFile().getPath();
                    try {
                        File file = new File(configPath);
                        FileReader fr = new FileReader(file);
                        BufferedReader reader = new BufferedReader(fr);
                        String line = reader.readLine();
                        if (line != null) {
                            reportPath = line;
                            line = reader.readLine();
                        }
                        if (line != null) {
                            otchetPath = line;
                            line = reader.readLine();
                        }
                        if (line != null) {
                            bdPath = line;
                        }
                        reportField.setText(reportPath);
                        otchetField.setText(otchetPath);
                        bdField.setText(bdPath);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(Settings.this,
                            fileChooser.getSelectedFile() + " установлен как файл конфигураций");
                }
            }
        });

        configBtnSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Сохранение конфигураций");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showSaveDialog(Settings.this);
                if (result == JFileChooser.APPROVE_OPTION ) {
                    File file = new File(configPath);
                    file.getParentFile().mkdirs();

                    try {
                        FileOutputStream outFile = new FileOutputStream(file);
                        outFile.write((reportPath).getBytes(), 0, reportPath.length());
                        outFile.write("\r\n".getBytes());
                        outFile.write((otchetPath).getBytes(), 0, otchetPath.length());
                        outFile.write("\r\n".getBytes());
                        outFile.write((bdPath).getBytes(), 0, bdPath.length());
                        outFile.write("\r\n".getBytes());
                        outFile.close();
                        JOptionPane.showMessageDialog(Settings.this,
                                "Файл " + fileChooser.getSelectedFile() +
                                        " сохранен");
                    } catch (FileNotFoundException fileNotFoundException) {
                        JOptionPane.showMessageDialog(Settings.this,  "Не удаётся открыть файл, возможно он занят другим процессом.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        reportBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор директории для докладных");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(Settings.this);
                if (result == JFileChooser.APPROVE_OPTION ) {
                    JOptionPane.showMessageDialog(Settings.this,
                            fileChooser.getSelectedFile());
                    reportPath = fileChooser.getSelectedFile().getPath();
                    reportField.setText(reportPath);
                }
            }
        });

        otchetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle("Выбор директории для отчётов");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(Settings.this);
                if (result == JFileChooser.APPROVE_OPTION ) {
                    JOptionPane.showMessageDialog(Settings.this,
                            fileChooser.getSelectedFile());
                    otchetPath = fileChooser.getSelectedFile().getPath();
                    otchetField.setText(otchetPath);
                }
            }
        });

        configField.setText(configPath);
        reportField.setText(reportPath);
        otchetField.setText(otchetPath);
        bdField.setText(bdPath);

    }

    public String getBdPath() {
        return bdPath;
    }

    public String getReportPath() {
        return reportPath;
    }

    public String getOtchetPath() {
        return otchetPath;
    }
}
