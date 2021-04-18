package com.skifer;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Report extends JDialog{

    private JButton save, cancel;
    private JPanel downPan;
    protected JTextArea text;
    private Object[][] SQLResult;
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    private String reportFoldPath, reportFilePath;

    public Report(Object[][] sql, String otchetPath) throws IOException {
        SQLResult = sql;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Employees sheet");
        if(!(new File(otchetPath).exists()))
            new File(otchetPath).mkdir();
        reportFoldPath = otchetPath;
        reportFilePath = reportFoldPath;

        downPan = new JPanel();
        save = new JButton("Сохранить");
        cancel = new JButton("Отмена");
        downPan.add(save);
        downPan.add(cancel);

        cancel.addActionListener(e -> dispose());

        text = new JTextArea(30, 20);
        text.setFont(new Font("Arial", Font.PLAIN, 14));
        text.setBackground(Color.WHITE);
        text.setForeground(Color.BLACK);
        text.setEditable(false);

        Box pane = new Box(BoxLayout.PAGE_AXIS);

        pane.add(downPan, BorderLayout.SOUTH);
        pane.add(new JScrollPane(text), BorderLayout.NORTH);
        add(new JScrollPane(pane), BorderLayout.CENTER);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 300 );

        Cell cell;
        Row row;
        HSSFCellStyle style = workbook.createCellStyle();

        HSSFFont boldFont= workbook.createFont();
        boldFont.setFontHeightInPoints((short)10);
        boldFont.setFontName("Arial");
        boldFont.setColor(IndexedColors.BLACK.getIndex());
        boldFont.setBold(true);
        boldFont.setItalic(false);

        style.setFont(boldFont);

        row = sheet.createRow(0);

        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Значок");
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Табельный №");
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Имя");
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Фамилия");
        cell = row.createCell(4, CellType.STRING);
        cell.setCellValue("Позиция");
        cell = row.createCell(5, CellType.STRING);
        cell.setCellValue("Начало");
        cell = row.createCell(6, CellType.STRING);
        cell.setCellValue("Конец");
        cell = row.createCell(7, CellType.STRING);
        cell.setCellValue("Факт начало");
        cell = row.createCell(8, CellType.STRING);
        cell.setCellValue("Факт конец");
        cell = row.createCell(9, CellType.STRING);
        cell.setCellValue("Брейк начало");
        cell = row.createCell(10, CellType.STRING);
        cell.setCellValue("Брейк конец");
        for(int i = 0; i < SQLResult[0].length - 12; i+=2)
        {
            cell = row.createCell(11+i, CellType.STRING);
            cell.setCellValue("Перерыв начало " + (i/2+1));
            cell = row.createCell(12+i, CellType.STRING);
            cell.setCellValue("Перерыв конец " + (i/2+1));
        }
        cell = row.createCell(SQLResult[0].length-1, CellType.STRING);
        cell.setCellValue("Не учитывать");
        cell = row.createCell(SQLResult[0].length, CellType.STRING);
        cell.setCellValue("Докладных");
        cell = row.createCell(SQLResult[0].length+1, CellType.STRING);
        cell.setCellValue("Поощрений");
        File dir = new File(reportFoldPath); //path указывает на директорию
        File[] arrFiles = null;
        List<File> reportFilesList = null;
        try{
            arrFiles = dir.listFiles();
            reportFilesList = Arrays.asList(arrFiles);
        }catch (NullPointerException e){}

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String date = dateFormat.format(new Date());
        for (int i = 0; i < SQLResult.length; i++) {
            row = sheet.createRow(i + 1);
            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue((int) SQLResult[i][0]);
            cell = row.createCell(1, CellType.NUMERIC);
            cell.setCellValue((int) SQLResult[i][1]);
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue((String) SQLResult[i][2]);
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue((String) SQLResult[i][3]);
            cell = row.createCell(4, CellType.STRING);
            cell.setCellValue((String) SQLResult[i][4]);
            CreationHelper creationHelper = workbook.getCreationHelper();
            CellStyle cellStyle = workbook.createCellStyle();
            short dateTimeFormat = creationHelper.createDataFormat().getFormat("hh:mm:ss");
            cellStyle.setDataFormat(dateTimeFormat);
            for (int j = 5; j < SQLResult[i].length - 1; j++) {
                cell = row.createCell(j);
                cell.setCellValue((Time) SQLResult[i][j]);
                cell.setCellStyle(cellStyle);
            }
            cell = row.createCell(SQLResult[i].length-1, CellType.BOOLEAN);
            cell.setCellValue((boolean) SQLResult[i][SQLResult[i].length - 1]);

            int reportCount = 0;
            if(reportFilesList!=null)
                for (File reportFileName : reportFilesList) {
                    String strFileName = reportFileName.toString();
                    if (strFileName.contains(String.valueOf((int) SQLResult[i][1]))
                            && strFileName.contains("докладная")
                            && strFileName.contains(date))
                        reportCount++;
                }
            cell = row.createCell(SQLResult[i].length, CellType.NUMERIC);
            cell.setCellValue((int) reportCount);
            reportCount = 0;
            if(reportFilesList!=null)
                for (File reportFileName : reportFilesList) {
                    String strFileName = reportFileName.toString();
                    if (strFileName.contains(String.valueOf((int) SQLResult[i][1]))
                            && strFileName.contains("поощрение")
                            && strFileName.contains(date))
                        reportCount++;
                }

            cell = row.createCell(SQLResult[i].length+1, CellType.NUMERIC);
            cell.setCellValue((int) reportCount);
            if(!((Boolean) SQLResult[i][SQLResult[i].length-1])) {
                int brakeMiniStartMin = 0, brakeMiniEndMin = 0, brakeMiniStartHour = 0, brakeMiniEndHour = 0;
                Date time = new Date();
                if ((((Time) SQLResult[i][5]).getHours() != ((Time) SQLResult[i][7]).getHours()) || Math.abs(((Time) SQLResult[i][5]).getMinutes() - ((Time) SQLResult[i][7]).getMinutes()) > 0 || SQLResult[i][7] == null)
                    text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                            + " отклонение в выходе на смену.\n"));
                if ((((Time) SQLResult[i][6]).getHours() != ((Time) SQLResult[i][8]).getHours()) || Math.abs(((Time) SQLResult[i][5]).getMinutes() - ((Time) SQLResult[i][7]).getMinutes()) > 0 || SQLResult[i][7] == null)
                    text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                            + " отклонение в уходе со смены.\n"));
                if (Math.abs(((Time) SQLResult[i][8]).getHours() - ((Time) SQLResult[i][7]).getHours()) * 60 + (((Time) SQLResult[i][8]).getMinutes() - ((Time) SQLResult[i][5]).getMinutes()) > 540)
                    text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                            + " переработка более 9 часов.\n"));
                if ((SQLResult[i][10] == null || SQLResult[i][9] == null) && time.getHours() - ((Time) SQLResult[i][5]).getHours() > 3)
                    text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                            + " отсутствует запись о брейке.\n"));
                else if ((SQLResult[i][10] != null && SQLResult[i][9] != null) && Math.abs(((Time) SQLResult[i][10]).getMinutes() - ((Time) SQLResult[i][9]).getMinutes()) != 30)
                    text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                            + " неверное время на брейке.\n"));
                for (int j = 11; j < SQLResult[i].length - 1; j += 2) {
                    if (SQLResult[i][j] == null || SQLResult[i][j + 1] == null || SQLResult[i][j].getClass() == (SQLResult[i][j + 1].getClass())) {
                        if (SQLResult[i][j] != null) {
                            brakeMiniStartMin = ((Time) SQLResult[i][j]).getMinutes();
                            brakeMiniStartHour = ((Time) SQLResult[i][j]).getHours();
                        } else
                            text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                                    + " отсутствует запись о начале перерыва.\n"));
                        if (SQLResult[i][j+1] != null) {
                            brakeMiniEndMin = ((Time) SQLResult[i][j + 1]).getMinutes();
                            brakeMiniEndHour = ((Time) SQLResult[i][j + 1]).getHours();
                        } else {
                            brakeMiniEndMin = time.getMinutes();
                            brakeMiniEndHour = time.getHours();
                            text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                                    + " отсутствует запись о конце перерыва, учитывается текущее время.\n"));
                        }


                        if (Math.abs(brakeMiniEndMin - brakeMiniStartMin) < 15)
                            text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                                    + " опаздывает с перерыва на " + (Math.abs(brakeMiniEndHour - brakeMiniStartHour)) + " часов " + Math.abs(brakeMiniEndMin - brakeMiniStartMin) + " минут\n"));
                        if (Math.abs(brakeMiniEndMin - brakeMiniStartMin) > 15)
                            text.setText(text.getText().concat((String) SQLResult[i][2] + (int) SQLResult[i][0]
                                    + " задерживается на перерыве на " + (Math.abs(brakeMiniEndHour - brakeMiniStartHour)) + " часов " + Math.abs(brakeMiniEndMin - brakeMiniStartMin) + " минут\n"));

                    }
                }
            }
        }

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    File file = new File(reportFilePath+"\\Отчёт от " + date + ".xls");
                    file.getParentFile().mkdirs();

                    FileOutputStream outFile = new FileOutputStream(file);
                    workbook.write(outFile);
                    System.out.println("Файл сохранён: " + file.getAbsolutePath());
                    JOptionPane.showMessageDialog(Report.this.getRootPane().getParent(),  "Файл: " + file.getAbsolutePath() + " успешно сохранён.", "Отчёт", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ioException) {
                    JOptionPane.showMessageDialog(Report.this.getRootPane().getParent(),  "Не удаётся открыть файл, возможно он занят другим процессом.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


    }

    public void setSQLResult(Object[][] SQLResult) {
        this.SQLResult = SQLResult;
    }
}
