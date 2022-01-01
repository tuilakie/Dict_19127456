package hcmus.java.Views;

import hcmus.java.Slang;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * hcmus.java
 * Created by Nguyen Thanh Kien 19127456
 * Date 23/12/2021 - 11:16 CH
 * Description: ...
 */
public class MainView extends JFrame {
    public static MainView instance;
    private JTabbedPane MenuTabPane;
    private JPanel ContentPanel;
    private JTable SlangTable;
    private JComboBox SearchOpt;
    private JTextField SearchTextField;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton ADDButton;
    private JButton DELETEButton;
    private JButton EDITButton;
    private JButton RESETButton;
    private JList History;
    public static DefaultTableModel DataModel;
    public static DefaultTableModel SearchModelView;
    public static MainView getInstance() {
        if (instance == null) {
            instance = new MainView();
        }
        return instance;
    }

    private MainView() {
        createDataModel();
        CreateTableView();
        SlangTable.setModel(DataModel);
        createListHistory();
        searchButton.addActionListener(e -> {
            String search = SearchTextField.getText();
            String opt = (String) SearchOpt.getSelectedItem();
            if (search.length() > 0) {
                createSearchModelView(search, opt);
                SlangTable.setModel(SearchModelView);
                UpdateListHistory();
                //show dialog count result line
                JOptionPane.showMessageDialog(null, "Found " + SearchModelView.getRowCount() + " results");
            }
        });

        refreshButton.addActionListener(e -> {
            SlangTable.setModel(DataModel);
            JOptionPane.showMessageDialog(null, "Refresh Successfully");
        });

        ADDButton.addActionListener(e -> {
            // input dialog 2 textfield
            JTextField slang = new JTextField();
            JTextField def = new JTextField();
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Slang Word:"));
            panel.add(slang);
            panel.add(new JLabel("Definition:"));
            panel.add(def);
            //set size
            panel.setPreferredSize(new Dimension(450, 120));
            int result = JOptionPane.showConfirmDialog(null, panel, "Add new Slang Word", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                // if exist slang word in dictionary confirm dialog with 2 button overwrite or duplicate
                if(!Slang.getInstance().AddWord(slang.getText(), def.getText())){
                    String[] options = {"Overwrite", "Duplicate"};
                    int n = JOptionPane.showOptionDialog(null, "Slang Word already exist, do you want to overwrite or duplicate?", "Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if(n == 0){
                        Slang.getInstance().OverwriteWord(slang.getText(), def.getText());
                        JOptionPane.showMessageDialog(null, "Overwrite Successfully");
                    }
                    else if(n == 1){
                        Slang.getInstance().DuplicateWord(slang.getText(), def.getText());
                        JOptionPane.showMessageDialog(null, "Duplicate Successfully");
                    }
                    SlangTable.setModel(DataModel);
                }
                else{
                    JOptionPane.showMessageDialog(null, "Add Successfully");
                }
            }
        });

        DELETEButton.addActionListener(e -> {
            System.out.println("DELETE");
            if(SlangTable.getSelectedRow() == -1){
                JOptionPane.showMessageDialog(null, "Please select a row to delete");
            }
            else{
                Slang.getInstance().DeleteWord(SlangTable.getValueAt(SlangTable.getSelectedRow(), 0).toString(), SlangTable.getValueAt(SlangTable.getSelectedRow(), 1).toString());
                JOptionPane.showMessageDialog(null, "Delete Successfully");
                SlangTable.setModel(DataModel);
            }

        });

        EDITButton.addActionListener(e -> {
            if(SlangTable.getSelectedRow() == -1){
                JOptionPane.showMessageDialog(null, "Please select a row to edit");
                return;
            }
            // input dialog 2 textfield
            JTextField slang = new JTextField();
            JTextField def = new JTextField();
            slang.setText(SlangTable.getValueAt(SlangTable.getSelectedRow(), 0).toString());
            def.setText(SlangTable.getValueAt(SlangTable.getSelectedRow(), 1).toString());
            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Slang Word:"));
            panel.add(slang);
            panel.add(new JLabel("Definition:"));
            panel.add(def);
            panel.setPreferredSize(new Dimension(450, 120));
            int result = JOptionPane.showConfirmDialog(null, panel, "Edit Slang Word", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Slang.getInstance().EditWord(slang.getText(), def.getText());
                SlangTable.setModel(DataModel);
                JOptionPane.showMessageDialog(null, "Edit Successfully");
            }
        });

        RESETButton.addActionListener(e -> {
            Slang.getInstance().ResetDict();
            JOptionPane.showMessageDialog(null, "Reset Successfully");
            SlangTable.setModel(DataModel);
        });
    }

    public void createAndShowGUI() {
        setTitle("SLANG Dictionary");
        setContentPane(ContentPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
    public void CreateTableView(){
        //Center align
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        SlangTable.setDefaultRenderer(Object.class, centerRenderer);
        SlangTable.setDefaultEditor(Object.class, null);
        //set Row height
        SlangTable.getTableHeader().setPreferredSize(new Dimension(0, 25));
        SlangTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        SlangTable.setRowHeight(25);
        SlangTable.setFont(new Font("Arial", Font.PLAIN, 16));
    }
    public void createDataModel(){
        DataModel = new DefaultTableModel();
        DataModel.setColumnIdentifiers(new String[]{"Slang Word", "Definition"});
        HashMap<String, ArrayList<String>> data = Slang.getInstance().getDict();
        for (String key : data.keySet()) {
            ArrayList<String> value = data.get(key);
            for (String s : value) {
                DataModel.addRow(new Object[]{key, s});
            }
        }
    }
    public void createSearchModelView(String keyword, String type){
        SearchModelView = new DefaultTableModel();
        SearchModelView.setColumnIdentifiers(new String[]{"Slang Word", "Definition"});
        if(type.equals("Slang Word")){
            ArrayList<String> data = Slang.getInstance().searchByKey(keyword);
            for (String s : data) {
                ArrayList<String> value = Slang.getInstance().getDict().get(s);
                for (String s1 : value) {
                    SearchModelView.addRow(new Object[]{s, s1});
                }
            }
        }
        if (type.equals("Definition")){
           HashMap<String,String> data = Slang.getInstance().searchByDefinition(keyword);
           for (String key : data.keySet()){
               SearchModelView.addRow(new Object[]{key, data.get(key)});
            }
        }

    }
    public void createListHistory(){
        History.setListData(Slang.getInstance().getHistory().toArray());
    }
    public void UpdateListHistory(){
        Slang.getInstance().addHistory(SearchTextField.getText());
        History.setListData(Slang.getInstance().getHistory().toArray());
    }
}
