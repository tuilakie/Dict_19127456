package hcmus.java.Model;

import java.util.HashMap;

/**
 * hcmus.java.Model
 * Created by Nguyen Thanh Kien 19127456
 * Date 25/12/2021 - 4:12 CH
 * Description: ...
 */
public class TrieNode {
    private HashMap<String, TrieNode> Child;
    private Boolean EndOfWord;
    private String Word;

    public TrieNode() {
        Child = new HashMap<>();
        EndOfWord = false;
        Word = "";
    }

    public HashMap<String, TrieNode> getChild() {
        return Child;
    }

    public void setChild(HashMap<String, TrieNode> child) {
        Child = child;
    }

    public boolean isEndOfWord() {
        return EndOfWord;
    }
    public void setEndOfWord(boolean endOfWord) {
        EndOfWord = endOfWord;
    }

    public String getWord() {
        return Word;
    }

    public void setWord(String word) {
        Word = word;
    }

    public boolean containsKey(char c) {
        return Child.containsKey(String.valueOf(c));
    }

    public void put(char c, TrieNode trieNode) {
        Child.put(String.valueOf(c), trieNode);
    }

    public TrieNode get(char c) {
        return Child.get(String.valueOf(c));
    }

    public char[] keys() {
        char[] keys = new char[Child.size()];
        int i = 0;
        for (String key : Child.keySet()) {
            keys[i++] = key.charAt(0);
        }
        return keys;
    }
}
