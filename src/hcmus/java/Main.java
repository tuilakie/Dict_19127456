package hcmus.java;

import hcmus.java.Views.MainView;

public class Main {
    public static void main(String[] args) {
        Slang.getInstance().LoadData("Data.txt");
        MainView.getInstance().createAndShowGUI();
    }
}
