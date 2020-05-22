package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerMDP implements Initializable {

    public PasswordField passwordField_motDePasseA;
    public PasswordField passwordField_motDePasseN;

    @FXML
    private void validerMDP() throws IOException {
        if(passwordField_motDePasseA.getText().equals(Controller.motDePasse)){
            if(!passwordField_motDePasseN.getText().isEmpty()){
                if(passwordField_motDePasseN.getText().length()>=8){
                    Controller.motDePasse = passwordField_motDePasseN.getText();
                    SauvgarderData();
                    Stage stage = (Stage) passwordField_motDePasseA.getScene().getWindow();
                    stage.close();
                }
                else
                {
                    passwordField_motDePasseN.clear();
                    passwordField_motDePasseN.setPromptText("Minimum 8 caractères !");
                }

            }
            else{
                passwordField_motDePasseN.setPromptText("Veuillez remplir ce champ !");
            }
        }
        else
        {
            passwordField_motDePasseA.clear();
            passwordField_motDePasseA.setPromptText("Incorrect !");
        }
    }
    private void SauvgarderData() throws IOException {
        try
        {
            FileOutputStream fos = new FileOutputStream("MDP");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(Controller.motDePasse);
            oos.close();
            fos.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
