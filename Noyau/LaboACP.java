package Noyau;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

//import java.awt.desktop.SystemSleepEvent;

public class LaboACP {
    protected Matrix A ;
    private Matrix visageMoy;
    private int size = BDD.bd.size();
    double[] d = new double[size];
    int [] indice = new int [10] ;



    public Matrice convertPGMtoMatrix(String address) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(address);
        Scanner scan = new Scanner(fileInputStream);

        // Discard the magic number
        scan.nextLine();
        // Read pic width, height and max value
        int picWidth = 92;
        int picHeight = 112;

        fileInputStream.close();

        // Now parse the file as binary data
        fileInputStream = new FileInputStream(address);
        DataInputStream dis = new DataInputStream(fileInputStream);

        // look for 4 lines (i.e.: the header) and discard them
        int numnewlines = 3;
        while (numnewlines > 0) {
            char c;
            do {
                c = (char) (dis.readUnsignedByte());
            } while (c != '\n');
            numnewlines--;
        }

        // read the image data
        double[][] data2D = new double[picHeight][picWidth];
        for (int row = 0; row < picHeight; row++) {
            for (int col = 0; col < picWidth; col++) {
                data2D[row][col] = dis.readUnsignedByte();
            }
        }

        return new Matrice(data2D);
    }

    public static void convertToImage(Matrix input, int name) throws IOException {
        File file = new File("./src/Image/"+name + " dimensions.jpg");
        if (!file.exists())
            file.createNewFile();

        BufferedImage img = new BufferedImage(92, 112, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img.getRaster();

        for (int m = 0; m < 112; m++) {
            for (int n = 0; n < 92; n++) {
                int value = (int) input.get(n * 112 + m, 0);
                raster.setSample(n, m, 0, value);
            }
        }

        ImageIO.write(img, "bmp", file);

    }

    public static void convertToJpg(Matrix input) throws IOException {
        File file = new File("./src/Image/choix.jpg");
        if (!file.exists())
            file.createNewFile();

        BufferedImage img = new BufferedImage(92, 112, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = img.getRaster();

        for (int m = 0; m < 112; m++) {
            for (int n = 0; n < 92; n++) {
                int value = (int) input.get(n * 112 + m, 0);
                raster.setSample(n, m, 0, value);
            }
        }

        ImageIO.write(img, "bmp", file);

    }

    public Matrice[] creerApp() throws IOException {
        Matrice[] tabMat = new Matrice[size];
        Matrice mat;
        int k = 0;

        for (int i = 0 ; i< BDD.bd.size() ; i++){
            mat = convertPGMtoMatrix(BDD.bd.get(i));
            mat = mat.matToVect(mat);
            tabMat[k] = mat;
            k++;
        }
        return tabMat;

    }

    public Matrix calculVisageMoy(Matrice[] app) throws IOException {
        Matrix somme = app[0];
        for (int i= 1; i < app.length; i++){
            somme = app[i].plus(somme);
        }
        somme = somme.times(0.005);
        visageMoy = somme;
        return somme;
    }
    public Matrix calculVecteurCaracteristique(Matrix vecteur,Matrix VisageMoy)
    {
        return vecteur.minus(VisageMoy);
    }

    public Matrix creerMatriceA(Matrix[] app){
        Matrix vect;

        double[][] tabA = new double[112*92][size];
        double[][] tabVect;
        for (int i =0 ; i< app.length;i++){
            vect = calculVecteurCaracteristique(app[i], visageMoy);
            tabVect = vect.getArray();

            for ( int j = 0 ; j < 112*92; j++){
                tabA[j][i] = tabVect[j][0];
            }
        }
        Matrix A = new Matrice(tabA);
        this.A = (Matrix)A.clone();
        return A;
    }


    public Matrix calculeCov(Matrix A){
        return (A.transpose()).times(A);
    }

    public Matrix getKEigenfaces (Matrix cov , int k ){

        int ligne , colonne ;
        //obtenir les valeurs propres et les vecteurs propres de la matrice covariance
        EigenvalueDecomposition caract = cov.eig();
        Matrix matD = caract.getD();
        double[] d = new double[size];
        for(int i = 0 ; i<size; i++){
            d[i] = matD.get(i,i);
            //System.out.println(d[i]);
        }
        this.d = d ;
        assert d.length >= k : "le nombre de valeurs propres est inférieur à k";
        int[] indices = getIndexesOfKEigenvalues(d, k);
        //System.out.println(A.getColumnDimension());
        this.indice = indices ;
        double som_keig = 0  ;
        double som_eig = 0;
        for (int i=0;i<A.getColumnDimension();i++){
            som_eig+=d[i];
        }

        for (int i=0 ;i<k;i++){
            som_keig+=d[indices[i]];
        }

        //System.out.println("somme des k eigenvalues"+som_keig);
        //System.out.println("somme des eigenvalues"+som_eig);
        System.out.println(som_keig/som_eig);

        Matrix VectProp = this.A.times(caract.getV()) ;
        Matrix EigenVectorsChoisis = VectProp.getMatrix(0,VectProp.getRowDimension()-1,indices);

        //Normalisation des vecteurs propres

        ligne =EigenVectorsChoisis.getRowDimension();
        colonne = EigenVectorsChoisis.getColumnDimension();
        for (int i=0 ;i<colonne;i++) {
            double temp = 0;
            for (int j=0 ; j<ligne;j++) {
                temp += Math.pow(EigenVectorsChoisis.get(j, i), 2);
            }

            temp=Math.sqrt(temp);

            for ( int j = 0 ; j<ligne;j++){
                EigenVectorsChoisis.set(j,i,EigenVectorsChoisis.get(j,i)/temp);
            }

        }

        return EigenVectorsChoisis ;
    }

    // obtenir les indices des k premiers grandes valeurs propres

    protected class mix implements Comparable {
        int index;
        double value;

        mix(int i, double v) {
            index = i;
            value = v;
        }
        public int compareTo(Object o) {
            double target = ((mix) o).value;
            if (value > target)
                return -1;
            else if (value < target)
                return 1;
            return 0;
        }
    }
    // la méthode
    public int[] getIndexesOfKEigenvalues(double[] d, int k) {
        mix[] mixes = new mix[d.length];
        int i;
        for (i = 0; i < d.length; i++)
            mixes[i] = new mix(i, d[i]);
        Arrays.sort(mixes);

        int[] result = new int[k];
        for (i = 0; i < k; i++) {
            result[i] = mixes[i].index;
            //System.out.println(mixes[i].value + "   ");
            //System.out.println(result[i] + "   ");
        }
        return result;
    }




    public Matrix[] matPoid (Matrice[] app,  Matrix Eig){
        Matrix[] matP = new Matrix[size];

        for (int i = 0; i < app.length; i++) {
            Matrix vectP = Eig.transpose().times(app[i].minus(visageMoy));
            matP[i] = vectP;
        }

        return matP;
    }

    public Matrix imgTn(String img, Matrix Eig) throws IOException {
        Matrice temp = convertPGMtoMatrix(img);
        temp = temp.matToVect(temp);
        Matrix tempPoid = Eig.transpose().times(temp.minus(visageMoy));
        return tempPoid;
    }

    /*
    // la distance euclidienne
      public double getDistance(Matrix a, Matrix b) {
          int size = a.getRowDimension();
          double sum = 0;

          for (int i = 0; i < size; i++) {
              sum += Math.pow(a.get(i, 0) - b.get(i, 0), 2);
          }

          return Math.sqrt(sum);
      }*/
//La distance de mahalanobis
    public double getDistance(Matrix a, Matrix b) {
        int nbLigne = a.getRowDimension();
        double dist = 0 ;
        for (int i=0 ; i<nbLigne;i++){
            dist+= Math.pow(a.get(i,0)-b.get(i,0),2)/this.d[indice[i]];
        }

        //System.out.println(dist);

        return dist ;
    }


}
