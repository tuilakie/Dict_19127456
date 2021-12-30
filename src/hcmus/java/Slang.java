package hcmus.java;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import hcmus.java.Model.*;

/**
 * hcmus.java
 * Created by Nguyen Thanh Kien 19127456
 * Date 25/12/2021 - 2:27 CH
 * Description: ...
 */
public class Slang {
    private Trie trie;
    private HashMap<String, ArrayList<String>> Forward;
    private HashMap<String, ArrayList<String>> Backward;
    private ArrayList<String> History = new ArrayList<>();
    public static Slang instance;
    public static Slang getInstance() {
        if (instance == null) {
            instance = new Slang();
        }
        return instance;
    }
    private Slang() {
        Forward = new HashMap<>();
        Backward = new HashMap<>();
        trie = new Trie();
    }

    public void LoadData(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedReader brData = new BufferedReader(new FileReader(file));
            BufferedReader brHistory = new BufferedReader(new FileReader("history.txt"));
            String line;
            //read file history
            while ((line = brHistory.readLine()) != null) {
                History.add(line);
            }
            while ((line = brData.readLine()) != null) {
                String[] arr = line.split("`");
                if (arr.length == 2) {
                    String key = arr[0];
                    String[] definition = arr[1].split("\\| ");
                    trie.insert(key);
                    if (Forward.containsKey(key)) {
                        Forward.get(key).addAll(List.of(definition));
                    } else {
                        Forward.put(key, new ArrayList<>(List.of(definition)));
                    }
                    for (String s : definition) {
                        String[] words = s.split(" ");
                        for (String word : words) {
                            if (Backward.containsKey(word)) {
                                Backward.get(word).add(key);
                            }
                            else {
                                Backward.put(word, new ArrayList<>(List.of(key)));
                            }
                        }
                    }
                }
            }
            brData.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveData(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (String key : Forward.keySet()) {
                ArrayList<String> definition = Forward.get(key);
                StringBuilder sb = new StringBuilder();
                for (String s : definition) {
                    sb.append(s).append("| ");
                }
                bw.write(key + "`" + sb.toString().substring(0, sb.length() - 2));
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, ArrayList<String>> getForward() {
        return Forward;
    }
    public HashMap<String, ArrayList<String>> getBackward() {
        return Backward;
    }
    public ArrayList<String> getHistory() {
        return History;
    }
    public void addHistory(String word) {
        History.add(word);
        //save history
        try {
            File file = new File("history.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            for (String s : History) {
                bw.write(s);
            }
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void removeHistory(String word) {
        History.remove(word);
    }
    public void clearHistory() {
        History.clear();
    }
    public void addForward(String key, ArrayList<String> definition) {
        Forward.put(key, definition);
    }
    public void addBackward(String key, ArrayList<String> definition) {
        Backward.put(key, definition);
    }
    public void EditForward(String key, ArrayList<String> definition) {
        Forward.replace(key, definition);
    }
    public ArrayList<String> searchByKey(String key) {
        ArrayList<String> result = new ArrayList<>();
        if (Forward.containsKey(key)) {
            result.add(key);
        }
        trie.Search(trie.getRoot(),key, result);
        return result;
    }
    public ArrayList<String> searchByDefinition(String definition) {
        HashSet<String> set = new HashSet<>();
        for (String s : definition.split(" ")) {
        }
        return new ArrayList<>(set);
    }
}
