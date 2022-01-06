package hcmus.java;

import java.io.*;
import java.util.*;

/**
 * hcmus.java
 * Created by Nguyen Thanh Kien 19127456
 * Date 25/12/2021 - 2:27 CH
 * Description: ...
 */
public class Slang {
    private HashMap<String, ArrayList<String>> Dict;
    private ArrayList<String> History = new ArrayList<>();
    public static Slang instance;
    public static Slang getInstance() {
        if (instance == null) {
            instance = new Slang();
        }
        return instance;
    }
    private Slang() {
        Dict = new HashMap<>();
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
                    if (Dict.containsKey(key)) {
                        Dict.get(key).addAll(List.of(definition));
                    } else {
                        Dict.put(key, new ArrayList<>(List.of(definition)));
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
            for (String key : Dict.keySet()) {
                ArrayList<String> definition = Dict.get(key);
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

    public HashMap<String, ArrayList<String>> getDict() {
        return Dict;
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
                bw.newLine();
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

    public HashMap<String,String> searchByDefinition(String definition) {
        HashMap<String,String> result = new HashMap<>();
        if(!definition.isEmpty()) {
            for (Map.Entry<String, ArrayList<String>> entry : Dict.entrySet()) {
                for (String s : entry.getValue()) {
                    if(s.toUpperCase(Locale.ROOT).contains(definition.toUpperCase(Locale.ROOT))) {
                        result.put(entry.getKey(), s);
                    }
                }
            }
        }
        return result;
    }
    public ArrayList<String> searchByKey(String key) {
        ArrayList<String> result = new ArrayList<>();
        if(!key.isEmpty()) {
            for (Map.Entry<String, ArrayList<String>> entry : Dict.entrySet()) {
                //start with
                if (entry.getKey().toUpperCase(Locale.ROOT).startsWith(key.toUpperCase(Locale.ROOT))) {
                    result.add(entry.getKey());
                }
            }
        }
        return result;
    }
    public boolean AddWord(String key, String definition) {
        if(Dict.containsKey(key)) {
            return false;
        }
        Dict.put(key, new ArrayList<>(List.of(definition)));
        SaveData("data.txt");
        return true;
    }
    public void OverwriteWord(String key, String definition) {
        Dict.get(key).clear();
        Dict.get(key).add(definition);
        SaveData("data.txt");

    }
    public void DuplicateWord(String key, String definition) {
        Dict.get(key).add(definition);
        SaveData("data.txt");
    }

    public void EditWord(String oldKey, String oldDefinition, String newKey, String newDefinition) {
        Dict.get(oldKey).remove(oldDefinition);
        if(Dict.containsKey(newKey)) {
            Dict.get(newKey).add(newDefinition);
        }
        else {
            Dict.put(newKey, new ArrayList<>(List.of(newDefinition)));
        }
        //remove old key
        if(Dict.get(oldKey).isEmpty()) {
            Dict.remove(oldKey);
        }
        //save data
        SaveData("data.txt");
    }
    public void DeleteWord(String key, String definition) {
        Dict.get(key).remove(definition);
        if(Dict.get(key).isEmpty()) {
            Dict.remove(key);
        }
        //save data
        SaveData("data.txt");
    }
    public void ResetDict() {
        Dict.clear();
        LoadData("slang.txt");
        SaveData("data.txt");
    }
    public String RandomKey() {
        //random key
        Random r = new Random();
        return Dict.keySet().toArray()[r.nextInt(Dict.keySet().toArray().length)].toString();
    }
    public String RandomDefinition() {
        //random definition
        Random r = new Random();
        String key = RandomKey();
        return Dict.get(key).toArray()[r.nextInt(Dict.get(key).toArray().length)].toString();
    }

}
