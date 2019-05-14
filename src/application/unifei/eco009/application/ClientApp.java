package application.unifei.eco009.application;

import application.unifei.eco009.stubs.CesarCipher;
import application.unifei.eco009.stubs.XOREncryption;

import java.io.IOException;
import java.util.Scanner;

public class ClientApp {

    public static void main(String[] args) {

        System.out.println("=================");
        System.out.println("    CLIENT APP   ");
        System.out.println("=================\n");
        Scanner scanner = new Scanner(System.in);
        boolean validOption = false;
        int option = 0;

        do {
            System.out.println("Choose a cryptography algorithm:");
            System.out.println("1 - Cesar Cipher");
            System.out.println("2 - XOR Encryption\n");
            System.out.print(">>  ");

            option = scanner.nextInt();

            if (option > 0 && option < 3) {
                validOption = true;
            } else {
                System.out.println("\n\n\n\n\n\n\nInvalid option!");
            }
        } while (!validOption);

        System.out.print("Enter the plain text: ");
        Scanner scanner2 = new Scanner(System.in);
        String plaintext = scanner2.nextLine();

        System.out.print("Enter key: ");
        Scanner scanner3 = new Scanner(System.in);
        String encrypted = "";

        switch (option) {
            case 1:
                int intKey = scanner3.nextInt();
                CesarCipher cesarCipher = new CesarCipher();
                cesarCipher.setServerAddress("localhost");
                cesarCipher.setServerPort(8000);
                encrypted = cesarCipher.cesarCipher(plaintext, intKey);
                break;
            case 2:
                String strKey = scanner3.nextLine();
                XOREncryption xor = new XOREncryption();
                xor.setServerAddress("localhost");
                xor.setServerPort(8000);
                encrypted = xor.XOREncryption(plaintext, strKey);
                break;
        }

        System.out.println("Encrypted string: " + encrypted);
    }

}
