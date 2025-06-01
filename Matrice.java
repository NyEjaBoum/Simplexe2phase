import java.util.ArrayList;
import java.util.List;

public class Matrice {
    private List<Contrainte> contraintes;
    private FonctionObjectif fonctionObjectif;

    // Constructeur
    public Matrice() {
        contraintes = new ArrayList<>();
        fonctionObjectif = null;
    }

    // Ajouter une contrainte
    public void add(Contrainte contrainte) {
        contraintes.add(contrainte);
    }

    // Définir la fonction objectif
    public void setFonctionObjectif(FonctionObjectif fonctionObjectif) {
        this.fonctionObjectif = fonctionObjectif;
    }

    // Obtenir le nombre de contraintes
    public int getContrainteCount() {
        return contraintes.size();
    }

    // Obtenir le nombre de variables (basé sur la première contrainte)
    public int getVariableCount() {
        return contraintes.isEmpty() ? 0 : contraintes.get(0).getCoef().length;
    }

    // Obtenir les contraintes
    public List<Contrainte> getContraintes() {
        return new ArrayList<>(contraintes); // Retourne une copie pour protéger les données
    }

    // Obtenir la fonction objectif
    public FonctionObjectif getFonctionObjectif() {
        return fonctionObjectif;
    }

    // Afficher toutes les contraintes et la fonction objectif
    public void display() {
        if (fonctionObjectif != null) {
            System.out.println("Fonction objectif :");
            fonctionObjectif.display();
        }
        System.out.println("Contraintes :");
        for (int i = 0; i < contraintes.size(); i++) {
            System.out.printf("Contrainte %d: ", i + 1);
            contraintes.get(i).display();
        }
    }
}