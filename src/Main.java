import hcmus.java.Slang;
import hcmus.java.Views.MainView;

public class Main {
    public static void main(String[] args) {
        Slang.getInstance().LoadData("data.txt");
        MainView.getInstance().createAndShowGUI();
    }
}
