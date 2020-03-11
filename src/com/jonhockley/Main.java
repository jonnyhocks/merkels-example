package com.jonhockley;


public class Main {

    public static void main(String[] args) throws Exception {
	    // Set up Alice
        Alice alice = new Alice();
        // Alice generate puzzles
        alice.generatePuzzles(4096);

        // Set up Bob
        Bob bob = new Bob("puzzles.bin");

        // Bob tells Alice which puzzle number to use.
        alice.setSelectedPuzzleNum(bob.getSelectedPuzzleNum());
        System.out.format("Bob tells Alice to user puzzle number: %d\n", bob.getSelectedPuzzleNum());

        // Alice sends an encrypted message to Bob using his puzzle number's key.
        System.out.format("Alice sends this encrypted message to Bob:\n%s\n", alice.sendEncryptedMessage());

        // Bob receives the message and decrypts it.
        System.out.format("Message decrypted by Bob is:\n%s\n", bob.decryptMessage(alice.sendEncryptedMessage()));

    }
}
