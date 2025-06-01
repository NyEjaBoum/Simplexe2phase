public class FonctionObjectif {
    private double[] coef;
    private boolean isMaximization;

    // Constructeur
    public FonctionObjectif(double[] coef, boolean isMaximization) {
        this.coef = coef.clone(); // Copie pour éviter les modifications externes
        this.isMaximization = isMaximization;
    }

    // Getters
    public double[] getCoef() {
        return coef.clone(); // Retourne une copie pour protéger les données
    }

    public boolean isMaximization() {
        return isMaximization;
    }

    // Afficher la fonction objectif
    public void display() {
        System.out.print(isMaximization ? "Maximiser : " : "Minimiser : ");
        for (int i = 0; i < coef.length; i++) {
            if (i > 0 && coef[i] >= 0) {
                System.out.print("+ ");
            }
            System.out.printf("%.2fx%d ", coef[i], i + 1);
        }
        System.out.println();
    }
}