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
                SearchTextField.setText("");
            }

        }
        );
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
        HashMap<String, ArrayList<String>> data = Slang.getInstance().getForward();
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
                SearchModelView.addRow(new Object[]{s, Slang.getInstance().getForward().get(s).get(0)});
            }
        }
        if (type.equals("Definition")){
            ArrayList<String> data = Slang.getInstance().searchByDefinition(keyword);
            for (String s : data) {
                System.out.println(s);
            }
        }

    }
    public void createListHistory(){
       History.setListData(Slang.getInstance().getHistory().toArray());
    }
    public void UpdateListHistory(){
        Slang.getInstance().addHistory(SearchTextField.getText()+"\n");
        History.setListData(Slang.getInstance().getHistory().toArray());
    }
}
