package Noyau;

import Jama.Matrix;

public class Matrice extends Matrix {
    public Matrice(int i, int i1) {
        super(i, i1);
    }

    public Matrice(int i, int i1, double v) {
        super(i, i1, v);
    }

    public Matrice(double[][] doubles) {
        super(doubles);
    }

    public Matrice(double[][] doubles, int i, int i1) {
        super(doubles, i, i1);
    }

    public Matrice(double[] doubles, int i) {
        super(doubles, i);
    }

    public Matrice vectToMat(Matrix vect){
        double[][] valVect = new double[112*92][2];
        double[][] valMatrice = new double[112][92];

        valVect = vect.getArray();

        for (int i = 0 ; i<112; i++){
            for (int j= 0; j<92; j++){
                valMatrice[i][j] = valVect[i*j][0];
            }
        }

        Matrice mat = new Matrice(valMatrice);
        return mat;

    }



    public Matrice matToVect(Matrice mat)
    {
        int k = 0;
        int i,j;
        double[][] valVect = new double[mat.getRowDimension()*mat.getColumnDimension()][2];
        double[][] valMatrice = new double[mat.getRowDimension()][mat.getColumnDimension()];
        valMatrice = mat.getArray();

        for (j=0 ; j<mat.getColumnDimension() ; j++){
            for (i=0; i<mat.getRowDimension(); i++){
                valVect[k][0] = valMatrice[i][j];
                k++;
            }
        }



        Matrice vect= new Matrice(valVect);

        return vect;



    }

    public Matrix[] ToMat(Matrix  mat)
    {
        double[][] vallVect = new double[112*92][10];
        Matrix[] tabMatrice= new Matrix [10] ;

      //  valVect = vect.getArray();

        for (int i = 0 ; i<10; i++){
            for (int j= 0; j<112*92; j++){
                vallVect[j][0] = mat.get(j,i);
            }
            Matrix vect = new Matrix (vallVect);
            tabMatrice[i]= vect;
        }


        return tabMatrice;

    }
}
