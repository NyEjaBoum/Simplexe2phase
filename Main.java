public class Main {
    public static void main(String[] args) {
        Matrice matrice = new Matrice();

        // Définir la fonction objectif : maximiser 5x1 + 3x2 + 2x3
        FonctionObjectif fo = new FonctionObjectif(new double[]{2,3,1}, true);
        matrice.setFonctionObjectif(fo);

        // Ajouter les contraintes
        matrice.add(new Contrainte(new double[]{1,1,1}, 40, ContrainteType.LESS_EQUAL)); // 3x1 + 2x2 + 4x3 ≤ 120
        matrice.add(new Contrainte(new double[]{2,1,-1}, 10, ContrainteType.GREATER_EQUAL)); // x1 + x2 + x3 = 60
        matrice.add(new Contrainte(new double[]{0, -1, 1}, 10, ContrainteType.GREATER_EQUAL)); // 2x1 + 3x2 + 2x3 ≥ 90

        // Afficher la fonction objectif et les contraintes
        matrice.display();

        // Afficher le nombre de contraintes et de variables
        System.out.println("Nombre de contraintes : " + matrice.getContrainteCount());
        System.out.println("Nombre de variables : " + matrice.getVariableCount());

        // Créer une instance de Simplexe et afficher le tableau
        Simplexe simplexe = new Simplexe(matrice);
        simplexe.resoudreSimplexe();

    }
}