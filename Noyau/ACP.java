package Noyau;

import Jama.Matrix;

import java.io.IOException;
import java.util.Date;

public class ACP {

    private Matrix[] matPoid;
    private LaboACP laboACP;
    private Matrix matEig;
    private Matrix visageMoyen;
    private Matrice[] apprentissage;
    public static double seuil = 0.03;



    public ACP() throws IOException {
        LaboACP labo = new LaboACP();
        //creation matrice d'apprentissage
        System.out.print("Matrice D'apprentissage ... ");
        Matrice[] app = labo.creerApp();
        System.out.println("OK ");
        //calcule du visage moyen
        System.out.print("Visage moyen ... ");
        Matrix vectMoyen = labo.calculVisageMoy(app);
        LaboACP.convertToImage(vectMoyen, 2002 );
        System.out.println("OK (La photo est dans le dossier du projet sous le nom de 000 ......)");
        //calcule de la matrice A
        Matrix A = labo.creerMatriceA(app);
        //calcule de la matrice tA*A 200*200
        Matrix Cov = labo.calculeCov(A);
        System.out.println("Matrice Covariance ... ");
        Matrix mat = labo.getKEigenfaces(Cov, 10);
        Matrix[] poid = labo.matPoid(app, mat);

        this.matPoid = poid;
        this.matEig = mat;
        this.visageMoyen = vectMoyen;
        this.laboACP = labo;
        this.apprentissage = app;

    }

    public Matrix[] getMatPoid() {
        return matPoid;
    }


    public double[] reconnaissance(String img) throws IOException {
        double [] tab = new double[3];
        Matrix[] poid = this.matPoid;
        Matrix imageIn = laboACP.imgTn(img, matEig);
        double distance = laboACP.getDistance(imageIn, poid[0]);
        int indice = 0;


        for (int i = 1 ; i < apprentissage.length; i++){
            if (distance > laboACP.getDistance(imageIn, poid[i])){
                distance = laboACP.getDistance(imageIn, poid[i]);
                indice = i;
            }
        }

        if (distance < seuil){
            System.out.println("\nDistance :"+ distance);
            //System.out.println("Nom : " + BDD.labels.get(indice));
            Matrix resultat = apprentissage[indice].matToVect(apprentissage[indice]);
            LaboACP.convertToImage(resultat, 2001 );
        }
        else{
            System.out.println("\nDistance :"+ distance);
            System.out.println("\nVisage non reconnu !");
            indice = -1;

        }
        tab[0] = indice;
        tab[1] = distance;
        return tab;
    }


}
