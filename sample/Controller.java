package sample;

import java.awt.*;
import java.net.*;

import Noyau.*;
import animatefx.animation.Bounce;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXToggleButton;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Controller implements Initializable {


    public PasswordField passwordField_motDePasse;
    public Label label_Err;
    public JFXButton Btn_Valider_mdp;
    public JFXButton Btn_Annuler_mdp;
    public Pane PaneMotDePasse;
    public Pane paneDeconnexion;
    public JFXButton Btn_deconnexion;
    static public String motDePasse = "admin";
    static public String email = null;
    public Label LabelModifier;
    public Pane paneSecurite;
    public TextField textEmail;
    public Pane paneWebcam;
    public JFXButton Btn_ScanWebcam;
    public Label label_ScanEnCours;
    private AbstractButton label;

    @FXML
    private void webcam() throws IOException, InterruptedException {

        Webcam webcam = Webcam.getDefault();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        FadeTransition Loadingtxt = new FadeTransition();
                        Loadingtxt.setDuration(Duration.seconds(2));
                        Loadingtxt.setNode(label_ScanEnCours);
                        Loadingtxt.setFromValue(0.0);
                        Loadingtxt.setToValue(1.0);
                        Loadingtxt.play();
                        label_ScanEnCours.toFront();
                    }
                });

                try{
                    webcam.open();
                }
                catch (Exception e){
                    System.out.println("Erreur");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            label_ScanEnCours.toBack();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Erreur");
                            alert.setContentText("Acces webcam impossible !");
                            alert.showAndWait();
                        }
                    });

                }


                try {
                    ImageIO.write(webcam.getImage(), "PNG", new File("security.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                webcam.close();
                    LaboACP labo = new LaboACP();
                    Matrice choix = null;

                    adresse = "security.png";


                    java.util.Date uDate = new java.util.Date(System.currentTimeMillis()); //Relever l'heure avant le debut du progamme (en milliseconde)

                    ACP acp = null;
                    try {
                        acp = new ACP();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    double[] result = new double[0]; //test image aleatoire
                    try {
                        result = acp.reconnaissance(Photo.conversion(adresse, 666));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Date dateFin = new Date(System.currentTimeMillis()); //Relever l'heure a la fin du progamme (en milliseconde)
                    Date duree = new Date(System.currentTimeMillis()); //Pour calculer la différence
                    duree.setTime(dateFin.getTime() - uDate.getTime());  //Calcul de la différence
                    long secondes = duree.getTime() / 1000;

                    double[] finalResult = result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (finalResult[0] != -1 && BDD.labels.get((int) finalResult[0]).equals("Admin")) {
                               System.out.println("Nom : " + BDD.labels.get((int) finalResult[0]) + "\nTemps du scan : " + secondes + "s\nRessemblance : " + (int) (100*(ACP.seuil-finalResult[1])/ACP.seuil) + "%");
                                Btn_personnes.setDisable(false);
                                Btn_ajout_personne.setDisable(false);
                                paneDeconnexion.toFront();
                                PaneMotDePasse.toBack();
                                paneSecurite.toFront();
                                if(email==null){
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Rappel !");
                                    // alert.setHeaderText("Results:");
                                    alert.setContentText("Vous pouvez ajouter un email de recupération pour votre mot de passe dans paramètres !");
                                    alert.showAndWait();
                                }
                            } else {
                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle("Erreur");
                                // alert.setHeaderText("Results:");
                                alert.setContentText("Acces refusé ! Réessayer avec le mot de passe.");
                                alert.showAndWait();
                            }
                            label_ScanEnCours.toBack();
                        }
                    });

            }
        }).start();

    }
    @FXML
    private void validerMDP() {
        if(passwordField_motDePasse.getText().equals(motDePasse)){
            Btn_personnes.setDisable(false);
            Btn_ajout_personne.setDisable(false);
            paneDeconnexion.toFront();
            PaneMotDePasse.toBack();
            paneSecurite.toFront();
            if(email.equals("abc123@exemple.xyz")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Rappel !");
                // alert.setHeaderText("Results:");
                alert.setContentText("Vous pouvez ajouter un email de recupération pour votre mot de passe dans paramètres !");
                alert.showAndWait();
            }
        }
        else
        {
            passwordField_motDePasse.clear();
            passwordField_motDePasse.setPromptText("Incorrect");
        }
    }

    public boolean isValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private static boolean sendFromGMail() {
        // Sender's email ID needs to be mentioned
        String from = "acpagnoface@gmail.com";
        String pass ="azerty2020";
        // Recipient's email ID needs to be mentioned.
        String to = email;

        boolean send = false;

        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject("Mot de passe");

            // Now set the actual message
            message.setText("Votre mot de passe est : "+motDePasse);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Sent message successfully....");
            send = true;
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
        return send;
    }
    @FXML
    private void modifierEmail() {
        if(textEmail.getText().isEmpty()){
            textEmail.setPromptText("Veuillez remplir ce champ !");
        }
        else
        {
            if (isValid(textEmail.getText())){
                email = textEmail.getText();
                textEmail.clear();
                textEmail.setText("Adresse enregistrée !");
                try
                {
                    FileOutputStream fos = new FileOutputStream("Email");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(email);
                    oos.close();
                    fos.close();
                }
                catch (IOException ioe)
                {
                    ioe.printStackTrace();
                }

            }
            else{
                textEmail.clear();
                textEmail.setPromptText("Email non valide !");
            }
        }
    }

    @FXML
    private void motDePasseOublie(){
        if (email.equals("abc123@exemple.xyz")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Erreur");
            // alert.setHeaderText("Results:");
            alert.setContentText("Aucun email de récupération trouvé !");
            alert.showAndWait();
        }
        else
        {
            if (sendFromGMail()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Email envoyé !");
                // alert.setHeaderText("Results:");
                alert.setContentText("Le mot de passe vous a été envoyé sur : " + email);
                alert.showAndWait();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Erreur");
                // alert.setHeaderText("Results:");
                alert.setContentText("Email non envoyé ! Vérifiez votre connexion !");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void annulerMdp(){
        PaneMotDePasse.toBack();
    }


    @FXML
    private void deconnexion(){
        paneDeconnexion.toBack();
        Btn_personnes.setDisable(true);
        Btn_ajout_personne.setDisable(true);
        passwordField_motDePasse.clear();
        passwordField_motDePasse.setPromptText("Mot de passe");
        paneSecurite.toBack();
    }


    public JFXButton Btn_ajout_personne;
    public JFXButton Btn_personnes;
    public JFXButton Btn_detecter_personne;
    public JFXButton Btn_a_propos;
    public JFXButton Btn_debloquer;

    @FXML public Label label_mode_nuit1,comment1,label_mode_nuit11,label_mode_nuit111,comment11,comment2,comment22,comment222;
    @FXML public ImageView bg_parametres;
    @FXML public ImageView bg_resultat_p;
    @FXML public  ImageView bg_resultat_n;
    @FXML public ImageView bg_menu;
    @FXML public ImageView bg_ajout;
    @FXML public  ImageView bg_parcourir;
    @FXML public  ImageView detection_logo1;
    @FXML public  ImageView bg_about;
    @FXML public  ImageView bg_bdd;
    @FXML public Label label_mode_nuit;









    @FXML public  TextField text_nom;
    @FXML public  Pane PANE_A_PROPOS;
    @FXML public  Pane PANE_PARAMETRES;
    @FXML public  Pane PANE_AFFICHAGE_BDD;
    @FXML public  JFXListView<String> listeBDD;
    @FXML public  ImageView im;
    @FXML public  ImageView im1;
    @FXML public  ImageView im2;
    @FXML public  ImageView im3;
    @FXML public  ImageView im4;
    @FXML public  Pane PANE_AFFICHAGE_DETAILS;
    @FXML public  Label labelAfficherDet;
    @FXML public  Label labelAfficherDet1;
    @FXML public  ImageView im2002;
    @FXML public  Pane PANE_AFFICHAGE_VM;
    @FXML public  Button closeButton;
    @FXML public  JFXButton Btn_parametres;

    public  int cpt = 0;
    @FXML public  Label progress;
    @FXML public  ImageView im_v;
    @FXML public  ImageView avatar;
    @FXML public  ImageView im_test;
    @FXML public ImageView im_non_reconnu;
    @FXML public  Label Label_nbr_photos;
    @FXML public  ImageView drag_drop;
    @FXML public  Pane PANE_AJOUT;
    @FXML public  Pane PANE_RESULTAT_N;
    @FXML public  ImageView loadingtxt;
    @FXML public  Button Btn_Rs;
    @FXML public  Pane PANE_RESULTAT_P;
    @FXML public  Button Btn_Rs1;
    @FXML public  Label LABEL_INFO;
    @FXML public  String adresse;
    @FXML
    public  ImageView IMAGE;
    @FXML
    public  Pane PANE_PARCOURIR;
    @FXML
    public  JFXToggleButton night_mode;
    @FXML
    public  Pane PANE_DEMARRER;
    @FXML
    public  Pane MENU;
    @FXML
    public  JFXButton Btn_debut;

    @FXML public  HBox loading;

    @FXML
    Circle circle1;
    @FXML
    Circle circle2;
    @FXML
    Circle circle3;
    @FXML
    Circle circle4;


    @FXML public  ImageView intro_title,intro_logo;



    @FXML
    private void menu(){
        FadeTransition Menu = new FadeTransition();
        Menu.setDuration(Duration.seconds(0.5));
        Menu.setNode(MENU);
        Menu.setFromValue(0.0);
        Menu.setToValue(1.0);
        Menu.play();
        MENU.toFront();
    }

    @FXML
    private void debloquer() throws IOException {
        PaneMotDePasse.toFront();
    }


    private void populateData(){
        int i = 0;
        while(i != BDD.labels.size()){
            listeBDD.getItems().add(BDD.labels.get(i));
            i = i+5;
        }
    }

    @FXML
    private void renitialiser() throws IOException {
        listeBDD.getItems().clear();
        int i = 0;
        while(i != BDD.labelsCopie.size()){
            listeBDD.getItems().add(BDD.labelsCopie.get(i));
            i = i+5;
        }
        BDD.bd.clear();
        BDD.bd.addAll(BDD.bdCopie);
        BDD.labels.clear();
        BDD.labels.addAll(BDD.labelsCopie);
        SauvgarderDataLabels();
        SauvgarderDataPhotos();
    }

    private void SauvgarderDataPhotos() throws IOException {
        try
        {
            FileOutputStream fos = new FileOutputStream("Photos");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(BDD.bd);
            oos.close();
            fos.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    private void SauvgarderDataLabels() throws IOException {
        try
        {
            FileOutputStream fos = new FileOutputStream("Labels");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(BDD.labels);
            oos.close();
            fos.close();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    private void RestaurerDataPhotos() {
        try {
            FileInputStream fis = new FileInputStream("Photos");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BDD.bd = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
    }
    private void RestaurerDataLabels() {
        try {
            FileInputStream fis = new FileInputStream("Labels");
            ObjectInputStream ois = new ObjectInputStream(fis);
            BDD.labels = (ArrayList<String>) ois.readObject();
            ois.close();
            fis.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();

        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
    }


    @FXML
    private void afficherVm() throws IOException {
        FadeTransition Menu = new FadeTransition();
        Menu.setDuration(Duration.seconds(0.5));
        Menu.setNode(PANE_AFFICHAGE_VM);
        Menu.setFromValue(0.0);
        Menu.setToValue(1.0);
        Menu.play();
        PANE_AFFICHAGE_VM.toFront();
        Image image = new Image("file:./src/Image/2002 dimensions.jpg");
        im2002.setImage(image);

    }


    @FXML
    private void afficherDetails() throws IOException {
        PANE_AFFICHAGE_VM.toBack();
        FadeTransition Menu = new FadeTransition();
        Menu.setDuration(Duration.seconds(0.5));
        Menu.setNode(PANE_AFFICHAGE_DETAILS);
        Menu.setFromValue(0.0);
        Menu.setToValue(1.0);
        Menu.play();
        PANE_AFFICHAGE_DETAILS.toFront();
        Object nom = listeBDD.getSelectionModel().getSelectedItem();
        String str = (String) nom;
        labelAfficherDet.setText(str);
        int ind = BDD.labels.indexOf(str);
        LaboACP labo = new LaboACP();

        for (int i = 0 ; i< 5; i++) {
            Matrice choix = null;
            choix = labo.convertPGMtoMatrix(BDD.bd.get(ind));
            choix = choix.matToVect(choix);
            LaboACP.convertToImage(choix, i);
            ind++;
        }
        Image image = new Image("file:./src/Image/0 dimensions.jpg");
        im.setImage(image);
        Image image1 = new Image("file:./src/Image/1 dimensions.jpg");
        im1.setImage(image1);
        Image image2 = new Image("file:./src/Image/2 dimensions.jpg");
        im2.setImage(image2);
        Image image3 = new Image("file:./src/Image/3 dimensions.jpg");
        im3.setImage(image3);
        Image image4 = new Image("file:./src/Image/4 dimensions.jpg");
        im4.setImage(image4);

    }

    @FXML
    private void afficherBDD(){
        FadeTransition Menu = new FadeTransition();
        Menu.setDuration(Duration.seconds(0.5));
        Menu.setNode(PANE_AFFICHAGE_BDD);
        Menu.setFromValue(0.0);
        Menu.setToValue(1.0);
        Menu.play();
        PANE_AFFICHAGE_BDD.toFront();
        listeBDD.getItems().clear();
        populateData();
    }


    @FXML
    private void demarrer(){
        FadeTransition Parc = new FadeTransition();
        Parc.setDuration(Duration.seconds(0.5));
        Parc.setNode(PANE_PARCOURIR);
        Parc.setFromValue(0.0);
        Parc.setToValue(1.0);
        Parc.play();
        PANE_PARCOURIR.toFront();
    }



    @FXML
    private void valider() throws IOException {
        if(text_nom.getText().isEmpty()){
            text_nom.clear();
            text_nom.setPromptText("Entrez un nom !");
        }
        else {
            FadeTransition Parc2 = new FadeTransition();
            Parc2.setDuration(Duration.seconds(0.5));
            Parc2.setNode(PANE_PARCOURIR);
            Parc2.setFromValue(0.0);
            Parc2.setToValue(1.0);
            Parc2.play();
            PANE_PARCOURIR.toFront();
            String str = text_nom.getText();
            for (int i = 0; i < cpt; i++) BDD.labels.add(str);
            cpt = 0;
            text_nom.clear();
            Label_nbr_photos.setText("0");
            SauvgarderDataPhotos();
            SauvgarderDataLabels();

        }

    }

    @FXML
    private void ajouterPersonne(){
        FadeTransition Aj = new FadeTransition();
        Aj.setDuration(Duration.seconds(0.5));
        Aj.setNode(PANE_AJOUT);
        Aj.setFromValue(0.0);
        Aj.setToValue(1.0);
        Aj.play();
        PANE_AJOUT.toFront();
        PaneMotDePasse.toBack();
        text_nom.clear();
        text_nom.setDisable(false);
    }
    @FXML
    private void ajouterAdmin(){
        FadeTransition Aj = new FadeTransition();
        Aj.setDuration(Duration.seconds(0.5));
        Aj.setNode(PANE_AJOUT);
        Aj.setFromValue(0.0);
        Aj.setToValue(1.0);
        Aj.play();
        PANE_AJOUT.toFront();
        PaneMotDePasse.toBack();
        text_nom.setText("Admin");
        text_nom.setDisable(true);
    }

    @FXML
    private void parametres(){
        FadeTransition Aj = new FadeTransition();
        Aj.setDuration(Duration.seconds(0.5));
        Aj.setNode(PANE_PARAMETRES);
        Aj.setFromValue(0.0);
        Aj.setToValue(1.0);
        Aj.play();
        PANE_PARAMETRES.toFront();
    }


    @FXML
    private void reessayer(){
        FadeTransition Parc3 = new FadeTransition();
        Parc3.setDuration(Duration.seconds(0.5));
        Parc3.setNode(PANE_PARCOURIR);
        Parc3.setFromValue(0.0);
        Parc3.setToValue(1.0);
        Parc3.play();
        PANE_PARCOURIR.toFront();
    }
    private ArrayList<String>  listAjout = new ArrayList<>();
    @FXML
    private void ajoutPhoto(){
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image PGM", "*.pgm");
        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        String currentpath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentpath));
        fileChooser.getExtensionFilters().add(imageFilter);
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null) {
            for (File file : list) {
                BDD.bd.add(file.getPath());
            }
        }

        cpt = cpt + list.size();
        Label_nbr_photos.setText(Integer.toString(cpt));
    }
    @FXML
    private void ajoutPhotoConvert(){
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("PGM ou PNG", "*.png","*.pgm");
        Stage stage = new Stage();
        final FileChooser fileChooser = new FileChooser();
        String currentpath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentpath));
        fileChooser.getExtensionFilters().add(imageFilter);
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null) {
            int i = BDD.bd.size();
            for (File file : list) {
                BDD.bd.add(Photo.conversion(file.getPath(), i));
                i++;
            }
        }

        cpt = cpt + list.size();
        Label_nbr_photos.setText(Integer.toString(cpt));
    }

    @FXML
    private void retour(){
        FadeTransition Menu2 = new FadeTransition();
        Menu2.setDuration(Duration.seconds(0.5));
        Menu2.setNode(MENU);
        Menu2.setFromValue(0.0);
        Menu2.setToValue(1.0);
        Menu2.play();
        MENU.toFront();

    }
    @FXML
    private void retourAjout(){
        showConfirmation();
    }

    private void showConfirmation() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Annuler");
        alert.setHeaderText("Êtes-vous sur de vouloir quitter cette page ?");
        alert.setContentText("Les photos ajoutées seront perdues...");

        // option != null.
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == ButtonType.OK) {
            // System.out.println(BDD.bd.size());
            for(int i = 0 ; i< cpt; i++){
                BDD.bd.remove(BDD.bd.get(BDD.bd.size()-1));
            }
            cpt = 0;
            // System.out.println(BDD.bd.size());
            FadeTransition Menu2 = new FadeTransition();
            Menu2.setDuration(Duration.seconds(0.5));
            Menu2.setNode(MENU);
            Menu2.setFromValue(0.0);
            Menu2.setToValue(1.0);
            Menu2.play();
            MENU.toFront();
        } else if(option.get() == ButtonType.CANCEL) {
            System.out.println("Cancel");
        }
    }

    @FXML
    private void aPropos(){
        FadeTransition Aprp = new FadeTransition();
        Aprp.setDuration(Duration.seconds(0.5));
        Aprp.setNode(PANE_A_PROPOS);
        Aprp.setFromValue(0.0);
        Aprp.setToValue(1.0);
        Aprp.play();
        PANE_A_PROPOS.toFront();
    }

    @FXML
    private void modifierMDP() throws IOException {
        Stage stage = new Stage();
        Parent root1 = FXMLLoader.load(getClass().getResource("motDePasse.fxml"));
        stage.setScene(new Scene(root1));
        stage.show();
    }



    @FXML
    private void parcourirConvert() throws IOException {


        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("PGM et PNG", "*.png","*.pgm");
        Stage stage = new Stage();

        final FileChooser fileChooser = new FileChooser();
        String curentpath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(curentpath));

        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showOpenDialog(stage);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        FadeTransition Loadingtxt = new FadeTransition();
                        Loadingtxt.setDuration(Duration.seconds(2));
                        Loadingtxt.setNode(loadingtxt);
                        Loadingtxt.setFromValue(0.0);
                        Loadingtxt.setToValue(1.0);
                        Loadingtxt.play();
                        loadingtxt.toFront();
                        loading.toFront();
                    }
                });
                if (file != null) {

                    LaboACP labo = new LaboACP();
                    Matrice choix = null;
                    try {
                        choix = labo.convertPGMtoMatrix(file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    choix = choix.matToVect(choix);
                    try {
                        LaboACP.convertToJpg(choix);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Image image2 = new Image("file:./src/Image/nGris.png");

                    adresse = file.getPath();


                    java.util.Date uDate = new java.util.Date(System.currentTimeMillis()); //Relever l'heure avant le debut du progamme (en milliseconde)

                    ACP acp = null;
                    try {
                        acp = new ACP();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    double[] result = new double[0]; //test image aleatoire
                    try {
                        result = acp.reconnaissance(Photo.conversion(adresse, 666));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Date dateFin = new Date(System.currentTimeMillis()); //Relever l'heure a la fin du progamme (en milliseconde)
                    Date duree = new Date(System.currentTimeMillis()); //Pour calculer la différence
                    duree.setTime(dateFin.getTime() - uDate.getTime());  //Calcul de la différence
                    long secondes = duree.getTime() / 1000;

                    double[] finalResult = result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (finalResult[0] != -1) {
                                LABEL_INFO.setText("Nom : " + BDD.labels.get((int) finalResult[0]) + "\nTemps du scan : " + secondes + "s\nRessemblance : " + (int) (100*(ACP.seuil-finalResult[1])/ACP.seuil) + "%");
                                PANE_RESULTAT_P.toFront();
                                Image image = new Image("file:./src/Image/2001%20dimensions.jpg");
                                im_v.setImage(image);
                                Image image2 = new Image("file:./src/Image/nGris.png");
                                im_test.setImage(image2);
                            } else {
                                PANE_RESULTAT_N.toFront();
                                Image image2 = new Image("file:./src/Image/nGris.png");
                                im_non_reconnu.setImage(image2);
                            }
                            loadingtxt.toBack();
                            loading.toBack();
                        }
                    });


                }

            }
        }).start();
    }
    @FXML
    private void parcourir() throws IOException {


        FileChooser.ExtensionFilter imageFilter
                = new FileChooser.ExtensionFilter("Image PGM", "*.pgm");
        Stage stage = new Stage();

        final FileChooser fileChooser = new FileChooser();
        String curentpath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(curentpath));

        fileChooser.getExtensionFilters().add(imageFilter);
        File file = fileChooser.showOpenDialog(stage);


        new Thread(new Runnable() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        FadeTransition Loadingtxt = new FadeTransition();
                        Loadingtxt.setDuration(Duration.seconds(2));
                        Loadingtxt.setNode(loadingtxt);
                        Loadingtxt.setFromValue(0.0);
                        Loadingtxt.setToValue(1.0);
                        Loadingtxt.play();
                        loadingtxt.toFront();
                        loading.toFront();
                    }
                });
                if (file != null) {

                    LaboACP labo = new LaboACP();
                    Matrice choix = null;
                    try {
                        choix = labo.convertPGMtoMatrix(file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    choix = choix.matToVect(choix);
                    try {
                        LaboACP.convertToJpg(choix);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Image image2 = new Image("file:./src/Image/choix.jpg");
                    im_test.setImage(image2);
                    im_non_reconnu.setImage(image2);
                    adresse = file.getPath();


                    java.util.Date uDate = new java.util.Date(System.currentTimeMillis()); //Relever l'heure avant le debut du progamme (en milliseconde)

                    ACP acp = null;
                    try {
                        acp = new ACP();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    double[] result = new double[0]; //test image aleatoire
                    try {
                        result = acp.reconnaissance(adresse);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Date dateFin = new Date(System.currentTimeMillis()); //Relever l'heure a la fin du progamme (en milliseconde)
                    Date duree = new Date(System.currentTimeMillis()); //Pour calculer la différence
                    duree.setTime(dateFin.getTime() - uDate.getTime());  //Calcul de la différence
                    long secondes = duree.getTime() / 1000;

                    double[] finalResult = result;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (finalResult[0] != -1) {
                                LABEL_INFO.setText("Nom : " + BDD.labels.get((int) finalResult[0]) + "\nTemps du scan : " + secondes + "s\nRessemblance : " + (int) (100*(ACP.seuil-finalResult[1])/ACP.seuil) + "%");
                                PANE_RESULTAT_P.toFront();
                                Image image = new Image("file:./src/Image/2001%20dimensions.jpg");
                                im_v.setImage(image);
                            } else {
                                PANE_RESULTAT_N.toFront();
                            }
                            loadingtxt.toBack();
                            loading.toBack();
                        }
                    });


                }

            }
        }).start();
    }





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

         try {
            FileInputStream fileInputStream = new FileInputStream("MDP");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            motDePasse = (String) objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        try {
            FileInputStream fileInputStream = new FileInputStream("Email");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            email = (String) objectInputStream.readObject();
            textEmail.setText(email);
            objectInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        {
            night_mode.setSelected(false);
            bg_parametres.setImage(new Image("file:./src/Interface/PARAMETRES.png"));
            bg_resultat_n.setImage(new Image("file:./src/Interface/RESULTAT_FAUX.png"));
            bg_resultat_p.setImage(new Image("file:./src/Interface/RESULTAT_VRAI.png"));
            bg_menu.setImage(new Image("file:./src/Interface/pane_MENU.png"));
            LABEL_INFO.getStylesheets().remove("file:./src/Css/nightmode.css");
            LABEL_INFO.getStylesheets().add("file:./src/Css/Bt.css");

            label_mode_nuit.getStylesheets().remove("file:./src/Css/nightmode.css");
            label_mode_nuit.getStylesheets().add("file:./src/Css/Bt.css");

            //pano ajout
            bg_ajout.setImage(new Image("file:./src/Interface/pane_ajouter.png"));
            Label_nbr_photos.setTextFill(Color.web("#4f4f4f"));

            //pano parcourir (detection)
            bg_parcourir.setImage(new Image("file:./src/Interface/pane_detection.png"));
            detection_logo1.setImage(new Image("file:./src/Interface/Detectionlogo.png"));

            //pano BDD
            bg_bdd.setImage(new Image("file:./src/Interface/background.jpg"));
            PANE_AFFICHAGE_DETAILS.setStyle("-fx-background-color: #acacac; -fx-background-radius: 10; -fx-background-insets: 0; -fx-border-width: 0;");
            PANE_AFFICHAGE_VM.setStyle("-fx-background-color: #acacac; -fx-background-radius: 10; -fx-background-insets: 0; -fx-border-width: 0;");

            labelAfficherDet.setTextFill(Color.web("#282828"));
            labelAfficherDet1.setTextFill(Color.web("#282828"));
            listeBDD.getStylesheets().remove("file:./src/Css/nightmode.css");
            listeBDD.getStylesheets().add("file:./src/Css/Listview.css");
        } //initaliser en mode light

        this.night_mode.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                if (night_mode.isSelected()==true) {
                    //pano resultat - pano menu - pano parametres
                    bg_parametres.setImage(new Image("file:./src/Interface/night-mode/PARAMETRES.png"));
                    bg_resultat_n.setImage(new Image("file:./src/Interface/night-mode/RESULTAT_FAUX.png"));
                    bg_resultat_p.setImage(new Image("file:./src/Interface/night-mode/RESULTAT_VRAI.png"));
                    bg_about.setImage(new Image("file:./src/Interface/night-mode/About_page_dark.png"));
                    bg_menu.setImage(new Image("file:./src/Interface/night-mode/pane_MENU.png"));
                    LABEL_INFO.getStylesheets().remove("file:./src/Css/Bt.css");
                    LABEL_INFO.getStylesheets().add("file:./src/Css/nightmode.css");
                    label_mode_nuit.getStylesheets().remove("file:./src/Css/Bt.css");
                    label_mode_nuit.getStylesheets().add("file:./src/Css/nightmode.css");


                    //pano ajout
                    bg_ajout.setImage(new Image("file:./src/Interface/night-mode/pane_ajouter.png"));
                    Label_nbr_photos.setTextFill(Color.web("#b8b8b8"));

                    //pano parcourir (detection)
                    bg_parcourir.setImage(new Image("file:./src/Interface/night-mode/pane_detection.png"));
                    detection_logo1.setImage(new Image("file:./src/Interface/night-mode/Detection_logo.png"));

                    //pano BDD
                    bg_bdd.setImage(new Image("file:./src/Interface/night-mode/background.png"));
                    PANE_AFFICHAGE_DETAILS.setStyle("-fx-background-color: #282828; -fx-background-radius: 10; -fx-background-insets: 0; -fx-border-width: 0;");
                    PANE_AFFICHAGE_VM.setStyle("-fx-background-color: #282828; -fx-background-radius: 10; -fx-background-insets: 0; -fx-border-width: 0;");

                    labelAfficherDet.setTextFill(Color.web("#FFFFFF"));
                    labelAfficherDet1.setTextFill(Color.web("#FFFFFF"));
                    listeBDD.getStylesheets().remove("file:./src/Css/Bt.css");
                    listeBDD.getStylesheets().add("file:./src/Css/nightmode.css");


                    //pano parametres
                    label_mode_nuit1.setTextFill(Color.web("#FFFFFF"));
                    comment1.setTextFill(Color.web("#FFFFFF"));
                    label_mode_nuit11.setTextFill(Color.web("#FFFFFF"));
                    label_mode_nuit111.setTextFill(Color.web("#FFFFFF"));
                    comment11.setTextFill(Color.web("#FFFFFF"));

                    comment2.setTextFill(Color.web("#FFFFFF"));
                    comment22.setTextFill(Color.web("#FFFFFF"));
                    comment222.setTextFill(Color.web("#FFFFFF"));



                }

                if (night_mode.isSelected()==false) {
                    //pano resultat - pano menu - pano parametres
                    bg_parametres.setImage(new Image("file:./src/Interface/PARAMETRES.png"));
                    bg_resultat_n.setImage(new Image("file:./src/Interface/RESULTAT_FAUX.png"));
                    bg_resultat_p.setImage(new Image("file:./src/Interface/RESULTAT_VRAI.png"));
                    bg_menu.setImage(new Image("file:./src/Interface/pane_MENU.png"));
                    LABEL_INFO.getStylesheets().remove("file:./src/Css/nightmode.css");
                    LABEL_INFO.getStylesheets().add("file:./src/Css/Bt.css");

                    bg_about.setImage(new Image("file:./src/Interface/About_page_light.png"));

                    label_mode_nuit.getStylesheets().remove("file:./src/Css/nightmode.css");
                    label_mode_nuit.getStylesheets().add("file:./src/Css/Bt.css");

                    //pano ajout
                    bg_ajout.setImage(new Image("file:./src/Interface/pane_ajouter.png"));
                    Label_nbr_photos.setTextFill(Color.web("#4f4f4f"));

                    //pano parcourir (detection)
                    bg_parcourir.setImage(new Image("file:./src/Interface/pane_detection.png"));
                    detection_logo1.setImage(new Image("file:./src/Interface/Detectionlogo.png"));

                    //pano BDD
                    bg_bdd.setImage(new Image("file:./src/Interface/background.jpg"));
                    PANE_AFFICHAGE_DETAILS.setStyle("-fx-background-color: #acacac; -fx-background-radius: 10; -fx-background-insets: 0; -fx-border-width: 0;");
                    PANE_AFFICHAGE_VM.setStyle("-fx-background-color: #acacac; -fx-background-radius: 10; -fx-background-insets: 0; -fx-border-width: 0;");

                    labelAfficherDet.setTextFill(Color.web("#282828"));
                    labelAfficherDet1.setTextFill(Color.web("#282828"));
                    listeBDD.getStylesheets().remove("file:./src/Css/nightmode.css");
                    listeBDD.getStylesheets().add("file:./src/Css/Listview.css");

                    //pano parametres
                    label_mode_nuit1.setTextFill(Color.web("#282828"));
                    comment1.setTextFill(Color.web("#282828"));
                    label_mode_nuit11.setTextFill(Color.web("#282828"));
                    label_mode_nuit111.setTextFill(Color.web("#282828"));
                    comment11.setTextFill(Color.web("#282828"));

                    comment2.setTextFill(Color.web("#282828"));
                    comment22.setTextFill(Color.web("#282828"));
                    comment222.setTextFill(Color.web("#282828"));

                }
            }
        });

        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setDuration(Duration.seconds(2));
        translateTransition.setNode(intro_logo);
        translateTransition.setToY(-724);
        translateTransition.play();

        FadeTransition introTitle = new FadeTransition();
        introTitle.setDuration(Duration.seconds(2));
        introTitle.setNode(intro_title);
        introTitle.setFromValue(0.0);
        introTitle.setToValue(1.0);
        introTitle.play();

        FadeTransition introTitle2 = new FadeTransition();
        introTitle2.setDuration(Duration.seconds(2));
        introTitle2.setNode(Btn_debut);
        introTitle2.setFromValue(0.0);
        introTitle2.setToValue(1.0);
        introTitle2.play();

        new Bounce(circle1).setCycleDuration(1000).setCycleCount(10000).setDelay(Duration.valueOf("500ms")).play();
        new Bounce(circle2).setCycleDuration(1000).setCycleCount(10000).setDelay(Duration.valueOf("1000ms")).play();
        new Bounce(circle3).setCycleDuration(1000).setCycleCount(10000).setDelay(Duration.valueOf("1100ms")).play();
        new Bounce(circle4).setCycleDuration(1000).setCycleCount(10000).setDelay(Duration.valueOf("1150ms")).play();

        Btn_debloquer.setDisable(false);
        Btn_personnes.setDisable(true);
        Btn_ajout_personne.setDisable(true);

        RestaurerDataPhotos();
        RestaurerDataLabels();

    }

    public void website() throws URISyntaxException, IOException {
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI("https://acpnetwork.netlify.app/");
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void websiteConvert() throws URISyntaxException, IOException {
        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI("https://onlineconvertfree.com/fr/convert-format/jpg-to-pgm/");
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void Ajouter_img(DragEvent dragEvent) {//Ajouter une image par la nouvelle methode
        if(dragEvent.getDragboard().hasFiles()){//Verifier si il ya une image
            dragEvent.acceptTransferModes(TransferMode.ANY);//!!!  A MODIFIER : pour que le porgramme accepte uniquement le images
        }
    }
    List<File> files = new ArrayList<>();//Liste contenant les nouvelles images ajoutees par la methode de drag and drop

    public void enreg_img(DragEvent event){//Enregistrer les imgages
        Dragboard container = event.getDragboard();//L'intreface des images dans scene builder
        boolean success = false;//Faux tant que l'operation n'est pas finie
        if (container.hasFiles()) {
            success = true;
            for (File file : container.getFiles()) {//Parcourir les fichier ajoutes
                files.add(file);//Les ajouter a une liste dans le programme global pour eviter les conflits avec les autre proccessus
                cpt = files.size();//Suivre la taille de la listes
                Label_nbr_photos.setText(Integer.toString(cpt));//Mise a jour du nombre d'images inserees
            }
        }
        if (files.size() == 5) {//Nombre images requis pour terminer L'operation
            for (File file : files) {//Parcourir La liste intermediaire
                BDD.bd.add(file.getPath());
            }
        }
        event.setDropCompleted(success);//Finir le traitement avec le drag en drop
        event.consume();//Terminer
    }
}
