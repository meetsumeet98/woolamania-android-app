package woolamania.in.woolamania;

import java.util.Random;

public class RefferalCode {
    private String characters= "ABCDEFGHIJKLMOPQRSTUVWXYZ0123456789";
    private String randomString= "";
    private int length= 5;


    public String randString(){
        Random rand= new Random();

        char[] text= new char[length];

        for(int i= 0; i<length; i++){
            text[i]= characters.charAt(rand.nextInt(characters.length()));
        }
        for(int i= 0; i<text.length; i++){
            randomString+=text[i];
        }
        return randomString;
    }

}
