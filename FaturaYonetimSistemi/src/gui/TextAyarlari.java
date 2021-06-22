/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JTextField;

/**
 *
 * @author PC
 */
public class TextAyarlari {
    private static String orjinalText;
    public static Color orjinalFgColor;
    
     public static void TextFieldFocusGained(JTextField textField,String yazi){
        orjinalText=yazi;
        if(textField.getText().trim().equals(yazi)){
            orjinalFgColor=textField.getForeground();
            textField.setText("");
        }
            textField.setForeground(Color.BLACK);
        
            
        
    }
      public static void TextFieldFocusLost(JTextField textField){
        if(textField.getText().trim().equals("")){
            textField.setText(orjinalText);
            textField.setForeground(orjinalFgColor);
        }
    }
      
    public static void setLimitTextField(JTextField textField,int limit){ //İstenilen limitte karakter girişini sağlar.
        textField.addKeyListener(new KeyAdapter() {
            @Override    
            public void keyTyped(KeyEvent ke) {
                if(textField.getText().length()>=limit){
                    ke.consume();
                }
            }                                                        
        });
    }
        public static void setOnlyNumber(JTextField textField){ //Sadece rakam girişini sağlar.
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent ke) {
                char c=ke.getKeyChar();
                if(!Character.isDigit(c)){
                    ke.consume(); //Girilen karakteri devre dışı bırakır.
                }
            }
            
});      
}
         public static void setOnlyLetter(JTextField textField){ //Sadece harf girişini sağlar.
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent ke) {
                char a=ke.getKeyChar();
                if(!Character.isLetter(a) && !Character.isSpaceChar(a)){
                    ke.consume();
                }
            }
        
        });
    }
}
