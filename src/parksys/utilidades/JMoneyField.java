package parksys.utilidades;

import java.awt.event.FocusAdapter;   
import java.awt.event.FocusEvent;   
import javax.swing.JFormattedTextField;   
import javax.swing.JTextField;   
import javax.swing.event.CaretEvent;   
import javax.swing.event.CaretListener;   
import javax.swing.text.AttributeSet;   
import javax.swing.text.BadLocationException;   
import javax.swing.text.PlainDocument;   
import javax.swing.text.SimpleAttributeSet;   
   
/**  
* Component JMoneyField  
* @author Dyorgio da Silva Nascimento
* @updater Bruno Aparecido Silvestre  
*/   
public class JMoneyField extends JFormattedTextField {   
        
    private static final long serialVersionUID = -5712106034509737967L;   
    private static final SimpleAttributeSet nullAttribute = new SimpleAttributeSet();   
        
    /**  
     * Creates a new instance of JMoneyField  
     */   
    public JMoneyField() {   
        this.setHorizontalAlignment( JTextField.LEFT);
        this.setColumns(7);
        this.setDocument(new MoneyFieldDocument());   
        this.addFocusListener(new MoneyFieldFocusListener());   
        this.setText("R$ 0,00");   
        this.addCaretListener(new CaretListener(){   
            public void caretUpdate(CaretEvent e) {   
                if (e.getDot() != getText().length() ) {   
                    getCaret().setDot(getText().length());   
                }   
            }   
        });   
    }   
        
    private final class MoneyFieldFocusListener extends FocusAdapter{   
        public void focusGained(FocusEvent e) {   
            selectAll();   
        }   
    }   
        
    private final class MoneyFieldDocument extends PlainDocument {   
            
        /**  
         *  
         */   
        private static final long serialVersionUID = -3802846632709128803L;   
   
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {   
            String original = getText(0,getLength());   
                
            // Permite apenas digitar ate 11 caracteres (R$ 9.999,99)
            if (original.length()<11) {   
                StringBuffer mascarado = new StringBuffer();
                if (a != nullAttribute) {   
                    //limpa o campo   
                    remove(-1,getLength());   
                        
                    mascarado.append((original+str).replaceAll("[^0-9]",""));   
                    for (int i = 0; i < mascarado.length(); i++){   
                        if (!Character.isDigit(mascarado.charAt(i))){   
                            mascarado.deleteCharAt(i);   
                        }   
                    }   
                    
                    if (mascarado.toString().equals(""))
                    	return;
                    
                    Long number = Long.parseLong(mascarado.toString());
                        
                    mascarado.replace(0, mascarado.length(), number.toString());   
                       
                    mascarado.insert(0,"R$ ");   
                    
                    mascarado.insert(mascarado.length()-2,",");      
                        
                    if ( mascarado.length() > 9 ) {   
                        mascarado.insert(mascarado.length()-6, '.');      
                    }   
                    super.insertString(0, mascarado.toString(), a);   
                }else{   
                    if (original.length() == 0){   
                        super.insertString(0, "R$ 0,00", a);   
                    }   
                }   
            }   
        }   
            
        @Override   
        public void remove(int offs, int len) throws BadLocationException {   
            if ( len == getLength() ) {   
                super.remove(0, len);   
                if (offs != -1){   
                    insertString(0, "",nullAttribute);   
                }   
            }else{   
                String original = getText(0, getLength()).substring(0, offs) + getText(0, getLength()).substring(offs+len);   
                super.remove(0, getLength());   
                insertString(0,original,null);   
            }   
        }   
    }   
}