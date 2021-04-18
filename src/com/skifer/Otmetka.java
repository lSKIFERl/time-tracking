package com.skifer;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Otmetka extends JPanel {

    private JPanel upPan;
    private JTextField signField;
    private JComboBox<String> cbSearchType;
    private Object[][] SQLResult;
    private JComboBox<String> cbLN;
    private String fileName;
    protected boolean fil;
    protected JTextArea docText;
    protected XWPFDocument docxModel;
    protected XWPFParagraph bodyParagraph;
    protected XWPFRun paragraphConfig;
    protected JFileChooser fileChooser;
    private JButton save, otchet;
    protected boolean note;
    private Report rep;
    private String otchetPath, reportPath;

    public Otmetka(Dimension size, String reportPath, String otchetPath)
    {
        this.otchetPath = otchetPath;
        this.reportPath = reportPath;
        docxModel = new XWPFDocument();
        CTSectPr ctSectPr = docxModel.getDocument().getBody().addNewSectPr();
        bodyParagraph = docxModel.createParagraph();
        bodyParagraph.setAlignment(ParagraphAlignment.LEFT);
        paragraphConfig = bodyParagraph.createRun();
        paragraphConfig.setFontSize(14);

        setSize(size);
        upPan = new JPanel();
        Box pane = new Box(BoxLayout.PAGE_AXIS);

        signField = new JTextField(3);

        String[] cbelements = new String[]{"Значок", "Табельный №", "Фамилия", "Отмеченные"};
        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
        for (int i = 0; i < cbelements.length; i++)
            cbModel.addElement((String) cbelements[i]);
        cbSearchType = new JComboBox<String>(cbModel);

        docText = new JTextArea(30, 60);
        docText.setFont(new Font("Dialog", Font.PLAIN, 14));
        System.out.println(this.getSize());

        docText.setLineWrap(true);
        docText.setWrapStyleWord(true);
        fileChooser = new JFileChooser();

        save = new JButton("Сохранить");

        cbLN = new JComboBox<String>();

        JPanel btnGroup = new JPanel();

        ButtonGroup group = new ButtonGroup();
        JRadioButton report = new JRadioButton("Докладная", false);
        group.add(report);
        btnGroup.add(report);
        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                note = !note;
            }
        });

        report = new JRadioButton("Поощрение", true);
        group.add(report);
        note = true;

        report.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                note = !note;
            }
        });

        otchet = new JButton("Отчёт");

        btnGroup.add(report);

        upPan.add(cbSearchType);
        upPan.add(signField);
        upPan.add(btnGroup);
        upPan.add(save);
        upPan.add(otchet);

        pane.add(upPan, BorderLayout.NORTH);
        pane.add((docText), BorderLayout.CENTER);
        add(new JScrollPane(pane), BorderLayout.CENTER);

        setVisible(true);
    }

    public JComboBox<String> getCbSearchType() {
        return cbSearchType;
    }

    public JButton getSave() {
        return save;
    }

    public JButton getOtchet() {
        return otchet;
    }

    public void setReport(){
        try {
            rep = new Report(SQLResult, otchetPath);
            rep.setVisible(true);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void setSQLResult(Object[][] SQLResult) {
        this.SQLResult = SQLResult;
    }

    public void save()
    {
        paragraphConfig.setText(docText.getText());
        FileOutputStream outputStream = null;
        fileName = "";
        if(fil)
            fileName = (String) cbLN.getSelectedItem();
        else {
            int index = cbSearchType.getSelectedIndex();
            for (int i = 0; i < SQLResult.length; i++) {
                if (String.valueOf(SQLResult[i][index]).equals(signField.getText())) {
                    fileName = (String) SQLResult[i][3] + " (" +SQLResult[i][1] + ")";
                    break;
                }
            }
        }
        if(fileName.equals(""))
            JOptionPane.showMessageDialog(Otmetka.this, "Сотрудник не указан", "Ошибка",JOptionPane.ERROR_MESSAGE);
        else {
            try {
                if(note)
                    fileName = fileName.concat(" поощрение от ");
                else
                    fileName = fileName.concat(" докладная от ");
                fileChooser.setCurrentDirectory(new File(otchetPath));
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = new Date();
                fileName = fileName.concat(dateFormat.format(date));
                fileChooser.setDialogTitle("Сохранение файла");
                String s = new String(fileChooser.getCurrentDirectory() + "\\" + fileName + ".docx");
                s = fileSuffix(s, 0);
                System.out.println(s);
                fileChooser.setSelectedFile(new File(s));
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showSaveDialog(Otmetka.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    outputStream = new FileOutputStream(fileChooser.getSelectedFile());
                    JOptionPane.showMessageDialog(Otmetka.this,
                            "Файл '" + fileChooser.getSelectedFile() +
                                    " сохранен");
                    docxModel.write(outputStream);
                    outputStream.close();

                }
            } catch (IOException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            }
        }
    }

    public void cbPaint()
    {
        switch (cbSearchType.getSelectedIndex())
        {
            case (3):{
                upPan.remove(1);
                DefaultComboBoxModel<String> cbLastNames = new DefaultComboBoxModel<String>();
                for (int i = 0; i < SQLResult.length; i++)
                    if((boolean)SQLResult[i][SQLResult[i].length-1])
                        cbLastNames.addElement((String) SQLResult[i][3] + " (" +SQLResult[i][1] + ")");
                cbLN = new JComboBox<String>(cbLastNames);
                upPan.add(cbLN, 1);
                fil = true;
                repaint();
                break;
            }
            case (2):{
                upPan.remove(1);
                DefaultComboBoxModel<String> cbLastNames = new DefaultComboBoxModel<String>();
                for (int i = 0; i < SQLResult.length; i++)
                    cbLastNames.addElement((String) SQLResult[i][3] + " (" +SQLResult[i][1] + ")");
                cbLN = new JComboBox<String>(cbLastNames);
                upPan.add(cbLN, 1);
                fil = true;
                repaint();
                break;
            }
            default:{
                if(upPan.getComponent(1) != signField) {
                    upPan.remove(1);
                    upPan.add(signField, 1);
                    fil = false;
                    repaint();
                }
            }
        }
        setVisible(true);
    }

    protected String fileSuffix(String s, int index)
    {
        if(new File(s).exists() && !(new File(s).isDirectory()))
        {
            System.out.println("(" + String.valueOf(index-1) + ").docx" + s.contains("(" + String.valueOf(index-1) + ").docx"));
            if(index == 0)
                s = new StringBuilder(s).insert(s.length() - 5, " (" + (index+1) + ")").toString();
            else if(s.contains("(" + String.valueOf(index) + ").docx"))
                s = new StringBuffer(s).replace(s.length() - 8 - (int) Math.log10(index), s.length(), "(" + String.valueOf(index + 1) + ").docx").toString();
            else return s;
            index++;
            s = fileSuffix(s, index);
        }
        return s;
    }
}