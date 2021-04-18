package com.skifer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

public class output extends JPanel {
    private JPanel upPan;
    private Box tabelPan;
    private JButton myButton, addBrake, dellBrake, dellCell, submitChanges;
    private Object[][] SQLResult;
    private Object[] columnsHeader = new String[] {"Значок", "Табельный №", "Имя", "Фамилия","Позиция", "Начало", "Конец", "Факт. начало", "Факт. конец", "Брейк начало", "Брейк конец", "Не учитывать"};
    private TableColumnModel columnModel;
    private JTable table;
    private Object[] newQuery;
    private JTextField signField;
    private int size[];

    public output() {
        setLayout(new BorderLayout());
        String[] cbelements = new String[]{"Значок", "Табельный №", "Фамилия"};
        newQuery = new Object[2];

        upPan = new JPanel();
        tabelPan = new Box(BoxLayout.PAGE_AXIS);

        signField = new JTextField(3);
        signField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newQuery[0] = signField.getText();
            }
        });
        myButton = new JButton("Найти");
        addBrake = new JButton("Добавить перерыв");
        addBrake.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table.isEditing())
                    table.getCellEditor(table.getSelectedRow(), table.getSelectedColumn()).stopCellEditing();
                int length = columnModel.getColumnCount();
                for(int i = 0; i < SQLResult.length; i++) {
                    Object temp = SQLResult[i][SQLResult[i].length-1];
                    SQLResult[i] = Arrays.copyOf(SQLResult[i], length - 1);
                    SQLResult[i] = Arrays.copyOf(SQLResult[i], length + 2);
                    SQLResult[i][SQLResult[i].length-1] = temp;
                }


                for(int i = 0; i < length; i++)
                    System.out.println(table.getValueAt(0, i) + " " + SQLResult[0][i]);

                size[0]+=2;

                paintTable();
            }
        });

        dellBrake = new JButton("Убрать перерыв");
        dellBrake.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(columnModel.getColumnCount()>12) {

                    int length = SQLResult[0].length;

                    for (int i = 0; i < SQLResult.length; i++) {
                        Object temp = SQLResult[i][SQLResult[i].length-1];
                        SQLResult[i] = Arrays.copyOf(SQLResult[i], SQLResult[i].length - 3);
                        SQLResult[i] = Arrays.copyOf(SQLResult[i], SQLResult[i].length + 1);

                        SQLResult[i][SQLResult[i].length-1] = temp;
                        System.out.println(SQLResult[i][SQLResult[i].length-1]);

                    }
                        columnsHeader = Arrays.copyOf(columnsHeader, columnsHeader.length-3);
                        columnsHeader = Arrays.copyOf(columnsHeader, columnsHeader.length+1);
                        columnsHeader[columnsHeader.length-1] = "Не учитывать";
                    size[0]-=2;
                    paintTable();
                }

            }
        });

        dellCell = new JButton("Обнулить");
        dellCell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(table.getSelectedColumn() > 6) {
                    table.getCellEditor(table.getSelectedRow(), table.getSelectedColumn()).stopCellEditing();
                    table.setValueAt(null, table.getSelectedRow(), table.getSelectedColumn());
                }
            }
        });

        submitChanges = new JButton("Сохранить");

        DefaultComboBoxModel<String> cbModel = new DefaultComboBoxModel<String>();
        for (int i = 0; i < cbelements.length; i++)
            cbModel.addElement((String) cbelements[i]);
        JComboBox<String> cbSearchType = new JComboBox<String>(cbModel);
        cbSearchType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newQuery[1] = cbSearchType.getSelectedIndex();
            }
        });

        table = new JTable(SQLResult, columnsHeader);
        columnModel = table.getColumnModel();
        table.setColumnModel(columnModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        tabelPan.add(new JScrollPane(table));
        table.setFillsViewportHeight(true);

        upPan.add(cbSearchType);
        upPan.add(signField);
        upPan.add(myButton);
        add(upPan, BorderLayout.NORTH);
        setVisible(true);
    }

    public void paintTable()
    {
        tabelPan.removeAll();
        size = new int[2];
        size[0] = SQLResult[0].length;
        size[1] = SQLResult.length;
        int l = columnsHeader.length;
        columnsHeader = Arrays.copyOf(columnsHeader, size[0]);
        if(size[0] > l) {
            for(int i = l; i < size[0]; i+=2)
            {
                columnsHeader[i-1] = "Начало перерыва " + ((i-11)/2+1);
                columnsHeader[i] = "Конец перерыва " + ((i-11)/2+1);
            }
        }
        columnsHeader[columnsHeader.length-1] = "Не учитывать";
        table = new JTable(this.SQLResult, columnsHeader){
            @Override
            public boolean isCellEditable(int row, int col) {
                if(col < 7)
                    return false;
                return true;
            }
        };
        columnModel = table.getColumnModel();
        for(int i = 7; i < columnModel.getColumnCount(); i++) {
            columnModel.getColumn(i).setCellEditor(new DateCellEditor());
            columnModel.getColumn(i).setCellRenderer(new TimeRenderer());
        }
        columnModel.getColumn(columnModel.getColumnCount()-1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
        columnModel.getColumn(columnModel.getColumnCount()-1).setCellRenderer(table.getDefaultRenderer(Boolean.class));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setFillsViewportHeight(true);
        table.setColumnModel(columnModel);
        tabelPan.add(new JScrollPane(table));
        add(tabelPan);

        upPan.add(addBrake);
        upPan.add(dellBrake);
        upPan.add(dellCell);
        upPan.add(submitChanges);
    }

    public JButton getMyButton() {
        return myButton;
    }

    public JButton getSubmitChanges() {
        return submitChanges;
    }

    public Object[] getNewQuery() {
        newQuery[0] = signField.getText();
        return this.newQuery;
    }

    public void setSQLResult(Object SQLResult[][]) {
        this.SQLResult = SQLResult;
    }

    public void Changes() {
        if(table.isEditing())
        table.getCellEditor(table.getSelectedRow(),table.getSelectedColumn()).stopCellEditing();
        paintTable();
    }

    private class DateCellEditor extends AbstractCellEditor implements TableCellEditor
    {
        private JSpinner editor;
        public DateCellEditor() {
           Time t = new Time(0,0,0);

            SpinnerDateModel model = new SpinnerDateModel(t, null, null, Calendar.HOUR_OF_DAY);
            model.setValue(t);
            editor = new JSpinner(model);
            JSpinner.DateEditor de = new JSpinner.DateEditor(editor, "HH:mm:ss");
            DateFormatter formatter = (DateFormatter) de.getTextField().getFormatter();
            formatter.setAllowsInvalid(false);
            formatter.setOverwriteMode(true);
            editor.setEditor(de);
        }
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            Time t;
            try{t = new Time(((Date)value).getTime());}
            catch (NullPointerException e){}
            catch (ClassCastException e1) {}
            finally {
                t = new Time(0,0,0);
            }
            editor.setValue(t);
            return editor;
        }
        public Object getCellEditorValue() {
            return new Time(((Date)editor.getValue()).getTime());
        }
    }

    private class TimeRenderer extends DefaultTableCellRenderer {

        private final DateFormat TIME_FORMAT = DateFormat.getTimeInstance();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean selected, boolean focused, int row, int column) {
            try {
                super.getTableCellRendererComponent(table, value, selected, focused, row, column);
                setText(TIME_FORMAT.format(new Time(((Date)value).getTime())));
            }catch (NullPointerException e) {}
            catch (ClassCastException e1)
            {
                table.setValueAt(null, row, column);
            }
            catch (ArrayIndexOutOfBoundsException arr){
            }
            return this;
        }
    }
}