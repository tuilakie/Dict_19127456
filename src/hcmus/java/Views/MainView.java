package hcmus.java.Views;

import hcmus.java.Slang;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

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
    private JList Suggestion;
    private JScrollPane SuggestPane;
    private JButton SAVEButton;
    private JPanel MeaningPane;
    private JLabel SlangDay;
    private JButton randomSlangButton;
    private JLabel keyLabel;
    private JPanel GuessMeaning;
    public static DefaultTableModel DataModel;
    public static DefaultTableModel SearchModelView;
    public static String CorrectMeaning;
    public static String CorrectSlang;
    public static MainView getInstance() {
        if (instance == null) {
            instance = new MainView();
        }
        return instance;
    }

    private MainView() {
        //SLANG DICTIONARY
        createDataModel();
        CreateTableView();
        SlangTable.setModel(DataModel);
        createListHistory();

        // SLANG ON THIS DAY
        String key = Slang.getInstance().RandomKey();
        createSlangDay(key, Slang.getInstance().getDict().get(key));

        //SLANG GUESS MEANING
        createGuessMeaning();



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
                if (!Slang.getInstance().AddWord(slang.getText(), def.getText())) {
                    String[] options = {"Overwrite", "Duplicate"};
                    int n = JOptionPane.showOptionDialog(null, "Slang Word already exist, do you want to overwrite or duplicate?", "Warning",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (n == 0) {
                        Slang.getInstance().OverwriteWord(slang.getText(), def.getText());
                        JOptionPane.showMessageDialog(null, "Overwrite Successfully");
                    } else if (n == 1) {
                        Slang.getInstance().DuplicateWord(slang.getText(), def.getText());
                        JOptionPane.showMessageDialog(null, "Duplicate Successfully");
                    }
                    SlangTable.setModel(DataModel);
                } else {
                    JOptionPane.showMessageDialog(null, "Add Successfully");
                }
            }
        });

        DELETEButton.addActionListener(e -> {
            if (SlangTable.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(null, "Please select a row to delete");
            } else {
                Slang.getInstance().DeleteWord(SlangTable.getValueAt(SlangTable.getSelectedRow(), 0).toString(), SlangTable.getValueAt(SlangTable.getSelectedRow(), 1).toString());
                JOptionPane.showMessageDialog(null, "Delete Successfully");
                SlangTable.setModel(DataModel);
            }

        });

        EDITButton.addActionListener(e -> {
            if (SlangTable.getSelectedRow() == -1) {
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
        SearchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                if(!SearchOpt.getSelectedItem().toString().equals("Slang Word")){
                    return;
                }
                String search = SearchTextField.getText();
                if (search.length() == 0) {
                    SlangTable.setModel(DataModel);
                    Suggestion.setListData(new String[0]);
                    Suggestion.setVisible(false);
                    SuggestPane.setVisible(false);
                } else {
                    Suggestion.setListData(Slang.getInstance().searchByKey(search).toArray());
                    Suggestion.addListSelectionListener(e1 -> {
                        if (Suggestion.getSelectedValue() != null) {
                            SearchTextField.setText(Suggestion.getSelectedValue().toString());
                            Suggestion.setVisible(false);
                            SuggestPane.setVisible(false);
                            revalidate();
                        }
                    });
                    SuggestPane.setVisible(true);
                    Suggestion.setVisible(true);
                }
                revalidate();
            }
        });
        SearchOpt.addActionListener(e -> {
            if(!SearchOpt.getSelectedItem().toString().equals("Slang Word")){
                Suggestion.setListData(new String[0]);
                Suggestion.setVisible(false);
                SuggestPane.setVisible(false);
                revalidate();
            }
        });
        SAVEButton.addActionListener(e ->  {
            Slang.getInstance().SaveData("data.txt");
            JOptionPane.showMessageDialog(null, "Save Successfully");
        });
        randomSlangButton.addActionListener(e -> {
            String Word = Slang.getInstance().RandomKey();
            createSlangDay(Word,Slang.getInstance().getDict().get(Word));
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
            if(Slang.getInstance().getDict().containsKey(keyword)){
                ArrayList<String> value = Slang.getInstance().getDict().get(keyword);
                for (String s : value) {
                    SearchModelView.addRow(new Object[]{keyword, s});
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
    public void createSlangDay(String key,ArrayList<String> Meaning){
        SlangDay.setText(key);
        SlangDay.setFont(new Font("Arial", Font.BOLD, 45));
        SlangDay.setForeground(Color.RED);
        //clear MeaningPane
        MeaningPane.removeAll();

        MeaningPane.setLayout(new BoxLayout(MeaningPane, BoxLayout.Y_AXIS));
        // margin
        MeaningPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // create label for meaning
        for(int i = 0; i < Meaning.size(); i++){
            JLabel label = new JLabel("☛ "+ Meaning.get(i)+".");
            // dont set font but set font size
            label.setFont(new Font("", Font.PLAIN, 20));
            label.setForeground(Color.BLUE);
            MeaningPane.add(label);
        }
    }
    public void createGuessMeaning(){
        keyLabel.setText(Slang.getInstance().RandomKey());
        keyLabel.setFont(new Font("Arial", Font.BOLD, 55));
        keyLabel.setForeground(Color.blue);
        // create arraylist with 4 random element
        ArrayList<String> Answer = new ArrayList<>();
        Random random = new Random();
        while (Answer.size() < 3){
            String Meaning = Slang.getInstance().RandomDefinition();
            if(!Answer.contains(Meaning)){
                Answer.add(Meaning);
            }
        }
        ArrayList<String> CorrectAnswer = Slang.getInstance().getDict().get(keyLabel.getText());
        CorrectMeaning = CorrectAnswer.get(random.nextInt(CorrectAnswer.size()));
        Answer.add(CorrectMeaning);
        // clear GuessMeaningPane
        GuessMeaning.removeAll();
        // shuffle arraylist
        Collections.shuffle(Answer);
        // random answer and set to button
        for(int i = 0; i < 4; i++){
            // create button
            JButton button = new JButton(Answer.get(i));
            // set font
            button.setFont(new Font("Arial", Font.PLAIN, 25));
            if(Answer.get(i).length() > 40){
                button.setFont(new Font("", Font.PLAIN, 15));
            }
            // set margin
            button.setMargin(new Insets(10, 10, 10, 10));
            // add action listener
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // get text from button
                    String text = button.getText();
                    // check if text is correct
                    if(text.equals(CorrectMeaning)){
                        // set color to green
                        button.setBackground(Color.green);
                        JOptionPane.showMessageDialog(null, "Correct!", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
                        // create new meaning
                        createGuessMeaning();
                    }else{
                        // set color to red
                        button.setBackground(Color.red);
                        JOptionPane.showMessageDialog(null, "Wrong!", "Sorry", JOptionPane.INFORMATION_MESSAGE);
                        createGuessMeaning();
                    }
                }
            });
            GuessMeaning.setLayout(new GridLayout(2, 2));
            GuessMeaning.add(button);
        }

    }

}
