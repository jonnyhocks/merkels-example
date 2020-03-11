package com.jonhockley;

import javax.crypto.SecretKey;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class Puzzle {

    byte[] puzzlePadding = new byte[16];
    byte[] puzzleIdNum;
    // The key to be stored inside the puzzle.
    byte[] puzzleKey;
    // The encryption key to encrypt the entire puzzle.
    SecretKey encryptionKey;

    /**
     * Puzzle constructor.  Generates puzzle object with puzzle ID number and a randomly generated key.
     * @param puzzleId - The ID of the puzzle to lookup.
     * @throws Exception
     */
    public Puzzle(int puzzleId) throws Exception {

        DES des = new DES();
        puzzleIdNum = CryptoLib.smallIntToByteArray(puzzleId);
        SecretKey puzzleKeyString = des.generateRandomKey();
        puzzleKey = puzzleKeyString.getEncoded();
        encryptionKey = generatePuzzleEncryptionKey();

    }

    /**
     * getPuzzleBytes - returns a byte array of the puzzle data (Not encrypted).
     * @return byte array - First 16 bytes are padding, next two bytes puzzle number and last 8 bytes are the key.
     * @throws IOException
     */
    private byte[] getPuzzleBytes() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(puzzlePadding);
        outputStream.write(puzzleIdNum);
        outputStream.write(puzzleKey);

        return outputStream.toByteArray();
    }

    /**
     * Getter for the puzzle key.
     * @return - byte array representation for the puzzle key.
     */
    public byte[] getKey() {
        return puzzleKey;
    }

    /**
     * Gets the DES encrypted puzzle after encoding the puzzle bytes into a string.
     * @return - A DES encrypted ciphertext.
     * @throws Exception
     */
    public String getEncryptedPuzzle() throws Exception {

        DES des = new DES();
        Base64.Encoder encoder = Base64.getEncoder();
        String puzzleString = encoder.encodeToString(getPuzzleBytes());

        String encryptedPuzzle = "";

        encryptedPuzzle = des.encrypt(puzzleString, encryptionKey);
        return encryptedPuzzle;
    }

    /**
     * Generates an encryption key to encrypt the entire puzzle.  The DES key specification requires the final
     * 48 bits of the key to be zeros.  The first two bytes are generated randomly.
     * @return - Secret key to encrypt the entire puzzle.
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     */
    private SecretKey generatePuzzleEncryptionKey() throws InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException {
        SecureRandom random = new SecureRandom();
        // First get random first two bytes.
        byte[] firstBitsOfKey = new byte[2];
        random.nextBytes(firstBitsOfKey);
        // Last 48 bits needs to be zeros according to spec (6 bytes).
        byte[] endSectionOfKey = new byte[6];
        byte[] completeKey = new byte[firstBitsOfKey.length + endSectionOfKey.length];
        SecretKey key = CryptoLib.createKey(completeKey);
        return key;
    }

}
