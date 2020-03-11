package com.jonhockley;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class Bob {

    // The puzzle number Bob got after cracking.
    private int selectedPuzzleNum;
    // The secret key Bob got after cracking.
    private SecretKey selectedPuzzleKey;

    /**
     * Bob constructor - Bob reads in a file and then picks a random puzzle to crack via brute force.
     * @param filename - The file name to read in (Binary).
     * @throws Exception - Thrown if there was a problem reading the file.
     */
    public Bob(String filename) throws Exception {
        Base64.Decoder decoder  = Base64.getDecoder();
        String crackedPuzzle = crackPuzzle(filename);
        byte[] crackedPuzzleBytes = decoder.decode(crackedPuzzle);
        byte[] crackedPuzzleNumberBytes = {crackedPuzzleBytes[16],crackedPuzzleBytes[17]};
        byte[] crackedPuzzleKeyBytes = Arrays.copyOfRange(crackedPuzzleBytes, 18, 26);
        selectedPuzzleNum = CryptoLib.byteArrayToSmallInt(crackedPuzzleNumberBytes);
        selectedPuzzleKey = CryptoLib.createKey(crackedPuzzleKeyBytes);

    }

    /**
     * Getter for Bob's selected puzzle number.
     * @return The puzzle number that Bob selected by cracking a puzzle.
     */
    public int getSelectedPuzzleNum() {
        return selectedPuzzleNum;
    }

    /**
     * Getter for Bob's selected puzzle key.
     * @return The puzzle key that Bob selected by cracking a puzzle.
     */
    public SecretKey getSelectedPuzzleKey() {
        return selectedPuzzleKey;
    }

    /**
     * Crack Puzzle - Accepts the file and reads it line by line and then attempts to crack the puzzle
     * on the line selected at random via brute force.
     * @param filename - The file name to read in (Binary).
     * @return - If successful, a decrypted string.  Otherwise Null.
     * @throws Exception - Thrown if there was a problem reading the file.
     */
    private String crackPuzzle(String filename) throws Exception {
        System.out.println("Bob is reading in the file and will now select a puzzle.\n");

        // Pick one to try and crack.
        SecureRandom random = new SecureRandom();
        int puzzleToCrack = random.nextInt(4096);
        System.out.format("Bob picked puzzle number %d\n", puzzleToCrack);

        // Open the file.
        BufferedReader reader = new BufferedReader(new FileReader(filename));

        // Need to read up until the selected line.
        for(int i=0;i<puzzleToCrack-1;i++) {
            reader.readLine();
        }

        // Now we can try and crack the line selected.
        return desBruteForce(reader.readLine());

    }

    /**
     * Brute force DES - Accepts a line of ciphertext and brute force attempts all 2^16 key possiblities until it
     * hits a plaintext with 128 zero bits at the beginning.
     * @param puzzleLine - DES encrypted ciphertext.
     * @return - If successful, a decrypted string.  Otherwise Null.
     * @throws Exception
     */
    private String desBruteForce(String puzzleLine) throws Exception {
        DES des = new DES();
        System.out.println("Bob is now brute force attacking a puzzle.");
        byte[] keyBytes = new byte[8];
        // Loop through all possible.
        for(int i=-128;i<128;i++){
            for(int j=-128;j<128;j++) {
                keyBytes[0] = (byte)i;
                keyBytes[1] = (byte)j;
                SecretKey key = CryptoLib.createKey(keyBytes);
                if(keyAttempt(puzzleLine, key)){
                    return des.decrypt(puzzleLine, key);
                }
            }
        }
        return null;
    }

    /**
     * Key Attempt - Attempts to decrypt the puzzle with the key and checks for the number of zeros specifed
     * in Alice's letter.
     * @param puzzleLine - DES encrypted ciphertext.
     * @param key - Secret key to attempt decryption.
     * @return - If successful, true.  False if not.
     * @throws Exception - Thrown if bad padding on the key (unsuccessful key attempt).
     */
    private boolean keyAttempt(String puzzleLine, SecretKey key) throws Exception {
        DES des = new DES();
        byte[] decryptedBytes;
        Base64.Decoder decoder = Base64.getDecoder();
        try {
            String decryptedPuzzle = des.decrypt(puzzleLine, key);
            decryptedBytes = decoder.decode(decryptedPuzzle);

            for (int i = 0; i < 15; i++) {
                if (decryptedBytes[i] != 0) {
                    return false;
                } else {
                    continue;
                }
            }
            System.out.println("Bob cracked it!!!");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Decrypt message - Receives an encrypted ciphertext from Alice and decrypts with the puzzle key saved
     * after cracking via brute force.
     * @param encryptedMessage - DES encrypted ciphertext - The encryption key has been agreed with Alice.
     * @return - The decrypted message.
     * @throws Exception - Thrown if there was an error with decryption.
     */
    public String decryptMessage(String encryptedMessage) throws Exception {
        DES des = new DES();
        return des.decrypt(encryptedMessage, selectedPuzzleKey);
    }
}
