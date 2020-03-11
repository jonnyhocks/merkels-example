package com.jonhockley;

import javax.crypto.SecretKey;
import java.io.FileOutputStream;
import java.util.*;

public class Alice {

    // The puzzle number that Bob sends back to Alice to refer to the puzzle map.
    private int selectedPuzzleNum;
    private Map<Integer, Puzzle> puzzleMap;

    /**
     * Generate puzzles - Takes in a number of puzzles to generate and then initialises that number of puzzles.
     * Stores key index number and key in a local map for lookup and also a bin file which contains the encrypted
     * puzzles to be sent to Bob.
     * @param numOfPuzzles - The number of puzzles to generate.
     * @throws Exception
     */
    public void generatePuzzles(Integer numOfPuzzles) throws Exception {
        System.out.format("Alice: generating %d puzzles...\n", numOfPuzzles);

        Map<Integer,Puzzle> puzzleHashMap = new HashMap<>(numOfPuzzles);
        // Populate the puzzles map.
        for(int i=0;i<numOfPuzzles;i++) {
            puzzleHashMap.put(i,new Puzzle(i));
        }
        puzzleMap = puzzleHashMap;
        List<Integer> puzzleKeys = new ArrayList<>(puzzleMap.keySet());
        Collections.shuffle(puzzleKeys);
        System.out.println("Writing puzzles to file...\n");
        FileOutputStream fos = new FileOutputStream("puzzles.bin");

        // Write out to the file.
        for(int o: puzzleKeys) {
            fos.write(puzzleMap.get(o).getEncryptedPuzzle().getBytes());
            fos.write("\r\n".getBytes());
        }
    }

    /**
     * Setter for the puzzle number selected by Bob after cracking his puzzle.
     * @param puzzleNum - The puzzle number selected by Bob.
     */
    public void setSelectedPuzzleNum(int puzzleNum) {
        selectedPuzzleNum = puzzleNum;
    }

    /**
     * Send encrypted message - Looks up the key which corresponds with the number Bob selected after cracking his
     * puzzle and then encrypts a message with that key to send back to Bob.
     * @return - DES encrypted ciphertext with the agreed key between Alice and Bob.
     * @throws Exception
     */
    public String sendEncryptedMessage() throws Exception {
        DES des = new DES();
        SecretKey secretKey = CryptoLib.createKey(puzzleMap.get(selectedPuzzleNum).getKey());
        return des.encrypt("What's up, Bob? You should now be able to decrypt this!", secretKey);
    }

}
