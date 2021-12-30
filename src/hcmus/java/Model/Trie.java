package hcmus.java.Model;

import java.util.ArrayList;

/**
 * hcmus.java.Model
 * Created by Nguyen Thanh Kien 19127456
 * Date 25/12/2021 - 4:13 CH
 * Description: ...
 */
public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public void insert(String word) {
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!current.containsKey(c)) {
                current.put(c, new TrieNode());
            }
            current = current.get(c);
        }
        current.setEndOfWord(true);
        current.setWord(word);
    }


    public TrieNode startsWith(String prefix) {
        TrieNode current = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (!current.containsKey(c)) {
                return null;
            }
            current = current.get(c);
        }
        return current;
    }

    public ArrayList<String> Search(TrieNode node,String prefix,ArrayList<String> result){
        TrieNode current = startsWith(prefix);
        if(current == null){
            return result;
        }
        for(char c:current.keys()){
            if(current.get(c).isEndOfWord()){
                result.add(prefix+c);
            }
        }
        for(char c:current.keys()){
            Search(current.get(c),prefix+c,result);
        }
        return result;
    }
    public void Remove(String word){
        TrieNode current = root;
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!current.containsKey(c)) {
                return;
            }
            current = current.get(c);
        }
        current.setEndOfWord(false);
    }
    public void Remove(TrieNode node,String prefix){
        TrieNode current = startsWith(prefix);
        if(current == null){
            return;
        }
        for(char c:current.keys()){
            if(current.get(c).isEndOfWord()){
                Remove(prefix+c);
            }
        }
        for(char c:current.keys()){
            Remove(current.get(c),prefix+c);
        }
    }
    public TrieNode getRoot() {
        return root;
    }

}
