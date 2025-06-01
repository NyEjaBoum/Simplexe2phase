public class Contrainte {
    public double[] coef;
    public double droite;
    public ContrainteType type;

    // Constructeur
    public Contrainte(double[] coef, double droite, ContrainteType type) {
        this.coef = coef.clone(); // Copie pour éviter les modifications externes
        this.droite = droite;
        this.type = type;
    }

    // Getters
    public double[] getCoef() {
        return coef.clone(); // Retourne une copie pour protéger les données
    }

    public double getDroite() {
        return droite;
    }

    public ContrainteType getType() {
        return type;
    }

    // Afficher la contrainte
    public void display() {
        for (int i = 0; i < coef.length; i++) {
            if (i > 0 && coef[i] >= 0) {
                System.out.print("+ ");
            }
            System.out.printf("%.2fx%d ", coef[i], i + 1);
        }
        System.out.printf("%s %.2f\n", type.getSymbol(), droite);
    }
}