package com.skifer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Arrays;

public class Main {

    private static Connection conn;
    private static Statement stmt;
    private static ResultSet rs;
    private static Object[][] SQLResult;
    private static int[] size ;

    public static void main(String[] args) {

        try {
            Settings settings = new Settings();
            Driver driver = new com.mysql.cj.jdbc.Driver();
            conn = DriverManager.getConnection(settings.getBdPath(), "root", "jojo");
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            size = new int[2];

            logWindow log = new logWindow (settings);      //Создаем объект класса
            log.getOut().getMyButton().addActionListener (new ActionListener() {    /*Для кнопки выбираем событие слушателя, и создаем новое событие в скобках.*/
                public void actionPerformed(ActionEvent e) {
                    try {
                        getNewQuery(log.getOut().getNewQuery());
                        log.setQuery(SQLResult);
                        log.draw();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    } catch (ArrayIndexOutOfBoundsException err){
                        JOptionPane.showMessageDialog(log, "Сотрудник не найден", "Ошибка",JOptionPane.ERROR_MESSAGE);

                    }
                }
            });
            log.getOut().getSubmitChanges().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        log.getOut().Changes();
                        int newColumnCount = SQLResult[0].length - size[0];
                        int oldColumnCount = size[0] - 11;
                        int k = 0;
                        String colNames[] = {"time_start_fact", "time_end_fact", "brake1_start", "brake1_end"};
                        colNames = Arrays.copyOf(colNames, SQLResult[0].length-6);
                        for(int j = 0; j < colNames.length-4; j+=2) {
                            colNames[4 + j] = "brake" + (((j+2)/2)+1) + "_start";
                            colNames[5 + j] = "brake" + (((j+2)/2)+1) + "_end";
                        }
                        colNames = Arrays.copyOf(colNames, colNames.length-1);
                        colNames[colNames.length-1] = "ignore";
                        if (newColumnCount > 0) {
                            if (newColumnCount / 2 == 1)
                                k = 1;
                            for (int i = 1; i <= newColumnCount / 2; i++) {
                                stmt.execute(
                                        "ALTER TABLE employee"
                                                + " ADD COLUMN `brake" + ((newColumnCount + oldColumnCount) / 2 + i) + "_start` TIME NULL DEFAULT NULL AFTER `brake" + ((oldColumnCount / 2) + i) + "_end`,"
                                                + " ADD COLUMN `brake" + ((newColumnCount + oldColumnCount) / 2 + i) + "_end` TIME NULL DEFAULT NULL AFTER `brake" + ((newColumnCount + oldColumnCount) / 2 + i) + "_start`;");
                            }
                        } else if(newColumnCount < 0){
                            newColumnCount = Math.abs(newColumnCount);
                            if (newColumnCount / 2 == 1)
                                k = 1;
                            for(int i = 1; i <= newColumnCount/2;i++) {
                                stmt.execute("ALTER TABLE employee" +
                                        " DROP COLUMN `brake" + ((newColumnCount + oldColumnCount) / 2 + i -k) + "_end`," +
                                        " DROP COLUMN `brake" + ((newColumnCount + oldColumnCount) / 2 + i -k) + "_start`;");
                            }
                        }

                        for(int i = 0; i < SQLResult.length; i++)
                            for(int j = 7; j < SQLResult[i].length; j++) {
                                if(SQLResult[i][j]!=null) {
                                    Object obj;
                                    if(j == SQLResult[i].length-1)
                                        obj = ((boolean)SQLResult[i][j])? 0: 1;
                                    else obj = SQLResult[i][j];
                                    stmt.execute("UPDATE `employee` SET `" + colNames[j - 7] + "` = '" + obj + "' WHERE (`empsign` = '" + SQLResult[i][1] + "');");
                                }
                                }

                    }catch ( SQLException exception) { exception.printStackTrace();}
                }
            });
            rs = stmt.executeQuery("select * from employee");
            rs.last();
            int countRow = rs.getRow();
            int countCol = rs.getMetaData().getColumnCount();
            SQLResult = new Object[countRow][countCol];
            rs.beforeFirst();
            int i = 0;
            while(rs.next())
            {
                SQLResult[i][0] = rs.getInt(1);
                SQLResult[i][1] = rs.getInt(2);
                SQLResult[i][2] = rs.getString(3);
                SQLResult[i][3] = rs.getString(4);
                SQLResult[i][4] = rs.getString(5);
                for(int j = 5; j < countCol-1;j++)
                    SQLResult[i][j] = rs.getTime(j+1);
                SQLResult[i][countCol-1] = rs.getBoolean(countCol);
                i++;
            }
            for(i = 0; i < 2; i++)
                for(int j = 0; j < countCol; j++)
                    System.out.println(SQLResult[i][j]);
            log.setQuery(SQLResult);
            log.setVisible (true);
            log.addWindowListener (new WindowAdapter() {
                public void windowClosing (WindowEvent e) {
                    try { conn.close(); } catch(SQLException se) { /*can't do anything */ }
                    try { stmt.close(); } catch(SQLException se) { /*can't do anything */ }
                    try { rs.close(); } catch(SQLException se) { /*can't do anything */ }
                    e.getWindow().dispose();
                }
            });
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
    }

    public static void getNewQuery(Object[] query) throws SQLException {
        String q = new String();
        try {switch ((int)query[1]){
            case (0):
                q = "empsign";
                break;
            case (1):
                q = "id";
                break;
            case (2):
                q = "last_name";
                break;
            default:
                q = "empsign";
        }}
        catch (NullPointerException e) {q = "id";}
        if (query[0].equals(""))
            rs = stmt.executeQuery("select * from employee");
        else
        {System.out.println("select * from employee where " + q + " = " + query[0]);

        rs = stmt.executeQuery("select * from employee where " + q + " = " + "\"" + query[0] + "\"");}
        rs.last();
        int countRow = rs.getRow();
        int countCol = rs.getMetaData().getColumnCount();
        size[0] = countCol;
        size[1] = countRow;
        SQLResult = new Object[countRow][countCol];
        rs.beforeFirst();
        int i = 0;
        while(rs.next())
        {
            SQLResult[i][0] = rs.getInt(1);
            SQLResult[i][1] = rs.getInt(2);
            SQLResult[i][2] = rs.getString(3);
            SQLResult[i][3] = rs.getString(4);
            SQLResult[i][4] = rs.getString(5);
            for(int j = 5; j < countCol-1;j++)
                SQLResult[i][j] = rs.getTime(j+1);
            SQLResult[i][countCol-1] = rs.getBoolean(countCol);
            i++;
        }
    }
}
