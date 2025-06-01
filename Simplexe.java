import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simplexe {
    public Matrice matrice;
    private double[][] tableau;
    private int[] variablesDeBase;
    private List<Integer> indicesArtificiels;
    public double[] coefObjectifOriginal;
    public double[]solution;

    public Simplexe(Matrice matrice) {
        this.matrice = matrice;
        this.indicesArtificiels = new ArrayList<>();
    }

    private StringBuilder affichageEtapes = new StringBuilder();
    


public void resetAffichageEtapes() {
    affichageEtapes = new StringBuilder();
}

public String getAffichageEtapes() {
    return affichageEtapes.toString();
}

// Modifie displayTableauSimplexe pour accumuler l'affichage

    public double[] getSolution(){
        int nbVariables = matrice.getVariableCount();
        int nbColonnes = tableau[0].length;
        double[]sol = new double[nbVariables+1];
        for(int i=0; i<nbVariables+1;i++){
            sol[i] = 0;
        } 
        //valiny dia lay colonne droite farany
        for(int i=0; i<nbVariables;i++){
            for(int j=0; j<tableau.length-1;j++){
                if(variablesDeBase[j] == i){
                    sol[i] = tableau[j][nbColonnes-1];
                }
            }
        }
        sol[nbVariables] = Math.abs(tableau[tableau.length-1][nbColonnes-1]); //atao valeur absolue eto XD
        solution = sol;
        return solution;
    }

    public void afficherSolution(){
        double[] sol = getSolution();
        for (int i = 0; i < sol.length-1; i++) {
            System.out.println("x" + (i+1) + " = " + sol[i]);
        }
        System.out.println("Valeur optimale : " + sol[sol.length-1]);
    }

    public void resoudreSimplexe() {
        // PHASE 1
        System.out.println("=== PHASE 1 ===");
        resoudrePhase1();
        //enregistrerResolutionDansFichier("resultat_simplexe.txt");

        // Si pas de variable artificielle, on passe directement à la phase 2
        if (indicesArtificiels.isEmpty()) {
            System.out.println("Pas de variable artificielle : passage direct à la phase 2.");
        } else {
            // PHASE 2
            System.out.println("\n=== PHASE 2 ===");
            construireTableauPhase2();
            resoudrePhase1();
            //enregistrerResolutionDansFichier("resultat_simplexe.txt");

            // Ici tu peux ajouter la boucle d'itération du simplexe pour la phase 2 si tu veux aller jusqu'à l'optimal
        }

        // Affichage de la solution finale
        System.out.println("\n=== SOLUTION FINALE ===");
        displayTableauSimplexe();
        afficherSolution();
    }

    public void convertirFormeStandard() { //affichage forme standard
        List<Contrainte> contraintes = matrice.getContraintes();
        int nbVariables = matrice.getVariableCount();
        int nbEcart = 0;
        int nbArtificiel = 0;

        for (Contrainte c : contraintes) {
            if (c.getType() == ContrainteType.LESS_EQUAL) {
                nbEcart++;
            } else if (c.getType() == ContrainteType.GREATER_EQUAL) {
                nbEcart++;
                nbArtificiel++;
            } else if (c.getType() == ContrainteType.EQUAL) {
                nbArtificiel++;
            }
        }

        System.out.println("Forme standard :");
        int indiceEcart = 1;
        int indiceArtificiel = 1;
        for (int i = 0; i < contraintes.size(); i++) {
            Contrainte c = contraintes.get(i);
            double[] coef = c.getCoef();
            System.out.printf("Contrainte %d: ", i + 1);
            for (int j = 0; j < nbVariables; j++) {
                if (j > 0 && coef[j] >= 0) {
                    System.out.print("+ ");
                }
                System.out.printf("%.2fx%d ", coef[j], j + 1);
            }
            if (c.getType() == ContrainteType.LESS_EQUAL) {
                System.out.printf("+ s%d = %.2f\n", indiceEcart++, c.getDroite());
            } else if (c.getType() == ContrainteType.GREATER_EQUAL) {
                System.out.printf("- s%d + a%d = %.2f\n", indiceEcart++, indiceArtificiel++, c.getDroite());
            } else if (c.getType() == ContrainteType.EQUAL) {
                System.out.printf("+ a%d = %.2f\n", indiceArtificiel++, c.getDroite());
            }
        }
    }

    private void insererVariablesEcartEtArtificielles(int nbVariables, int nbContraintes, List<Contrainte> contraintes) { //inserer variable artificielle anaty tableau simplexe
        int nbVariablesEcart = 0;
        int nbVariablesArtificielles = 0;
        for (Contrainte c : contraintes) {
            if (c.getType() == ContrainteType.LESS_EQUAL) {
                nbVariablesEcart++;
            } else if (c.getType() == ContrainteType.GREATER_EQUAL) {
                nbVariablesEcart++;
                nbVariablesArtificielles++;
            } else if (c.getType() == ContrainteType.EQUAL) {
                nbVariablesArtificielles++;
            }
        }

        int cols = nbVariables + nbVariablesEcart + nbVariablesArtificielles + 1;
        int indiceEcart = nbVariables;
        int indiceArtificiel = nbVariables + nbVariablesEcart;

        for (int i = 0; i < nbContraintes; i++) {
            Contrainte c = contraintes.get(i);
            double[] coef = c.getCoef();

            for (int j = 0; j < nbVariables; j++) {
                tableau[i][j] = coef[j];
            }

            if (c.getType() == ContrainteType.LESS_EQUAL) { //eto tsisy variable artificielle, tonga d manao +s1 
                tableau[i][indiceEcart] = 1.0;
                variablesDeBase[i] = indiceEcart++;
            } else if (c.getType() == ContrainteType.GREATER_EQUAL) { //eto misy anzareo roa, -s1 + a1
                tableau[i][indiceEcart] = -1.0;
                tableau[i][indiceArtificiel] = 1.0;
                variablesDeBase[i] = indiceArtificiel;
                indicesArtificiels.add(indiceArtificiel++);
                indiceEcart++;
            } else if (c.getType() == ContrainteType.EQUAL) { //eto misy + a1
                tableau[i][indiceArtificiel] = 1.0;
                variablesDeBase[i] = indiceArtificiel;
                indicesArtificiels.add(indiceArtificiel++);
            }

            tableau[i][cols - 1] = c.getDroite();
        }
    }




    public double[] construireLigneObjectifPhase1(int nbColonnes, int nbContraintes) {
        double[] ligneObjectif = new double[nbColonnes];
        FonctionObjectif fonctionObjectif = matrice.getFonctionObjectif();
        double[] fonctionObjectInit = fonctionObjectif.getCoef();

        if (indicesArtificiels.isEmpty()) { //rehefa tsisy variable artificiel lay izy 
            // Pas de variable artificielle : on met la fonction objectif initiale
            for (int j = 0; j < fonctionObjectInit.length; j++) {
                ligneObjectif[j] = fonctionObjectInit[j];
            }
            // Les autres colonnes (slack, etc.) restent à zéro
        } else {
            // Cas classique : phase 1 avec variables artificielles
            for (int j : indicesArtificiels) {
                ligneObjectif[j] = -1.0; // Coefficient -1 pour a1, a2, ...
            }
            for (int i = 0; i < nbContraintes; i++) {
                if (indicesArtificiels.contains(variablesDeBase[i])) {
                    for (int j = 0; j < nbColonnes; j++) {
                        ligneObjectif[j] += tableau[i][j];
                    }
                }
            }
        }
        return ligneObjectif;
    }

    public double[] construireLigneObjectifPhase2(int nbColonnes, int nbContraintes) {
        // Liste des colonnes à garder (hors variables artificielles)
        List<Integer> colonnesAGarder = new ArrayList<>();
        for (int j = 0; j < nbColonnes; j++) {
            if (!indicesArtificiels.contains(j)) {
                colonnesAGarder.add(j);
            }
        }

        int newCols = colonnesAGarder.size();
        double[] ligneObjectif = new double[newCols];

        // Pour chaque variable de base, soustraire sa contribution à la fonction objectif
        for (int i = 0; i < nbContraintes; i++) {
            int varBase = variablesDeBase[i];
            double coefBase = (varBase < coefObjectifOriginal.length) ? coefObjectifOriginal[varBase] : 0.0; 
            if (Math.abs(coefBase) > 1e-10) {
                for (int idx = 0; idx < newCols; idx++) {
                    int j = colonnesAGarder.get(idx);
                    ligneObjectif[idx] -= coefBase * tableau[i][j];
                }
            }
        }

        // Ajouter les coefficients des variables hors base (fonction objectif initiale)
        for (int idx = 0; idx < newCols; idx++) {
            int j = colonnesAGarder.get(idx);
            ligneObjectif[idx] += (j < coefObjectifOriginal.length) ? coefObjectifOriginal[j] : 0.0;
        }

        return ligneObjectif;
    }


    public double[][] construireTableauSimplexe() {
        if (matrice.getContrainteCount() == 0 || matrice.getFonctionObjectif() == null) {
            throw new IllegalStateException("Contraintes ou fonction objectif manquante");
        }

        int nbVariables = matrice.getVariableCount();
        int nbContraintes = matrice.getContrainteCount();
        List<Contrainte> contraintes = matrice.getContraintes();
        FonctionObjectif fonctionObjectif = matrice.getFonctionObjectif();

        convertirFormeStandard();

        int nbVariablesEcart = 0;
        int nbVariablesArtificielles = 0;
        for (Contrainte c : contraintes) {
            if (c.getType() == ContrainteType.LESS_EQUAL) {
                nbVariablesEcart++;
            } else if (c.getType() == ContrainteType.GREATER_EQUAL) {
                nbVariablesEcart++;
                nbVariablesArtificielles++;
            } else if (c.getType() == ContrainteType.EQUAL) {
                nbVariablesArtificielles++;
            }
        }

        int rows = nbContraintes + 1;
        int cols = nbVariables + nbVariablesEcart + nbVariablesArtificielles + 1;

        tableau = new double[rows][cols];
        variablesDeBase = new int[nbContraintes];
        indicesArtificiels.clear();
        coefObjectifOriginal = fonctionObjectif.getCoef();
        
        //ligne farany
        insererVariablesEcartEtArtificielles(nbVariables, nbContraintes, contraintes);
        System.out.println("nb variable = " + nbVariables);
        System.out.println("nb contrainte = " + nbContraintes);
        
        double[]ligneObject = construireLigneObjectifPhase1(cols, nbContraintes);
        for(int j = 0; j < cols; j++) {
            tableau[rows - 1][j] = ligneObject[j];
        }

        return tableau;
    } 

    public void construireTableauPhase2() {
        int nbContraintes = matrice.getContrainteCount();
        int nbColonnes = tableau[0].length;

        // Colonnes à garder (hors variables artificielles)
        List<Integer> colonnesAGarder = new ArrayList<>();
        for (int j = 0; j < nbColonnes; j++) {
            if (!indicesArtificiels.contains(j)) {
                colonnesAGarder.add(j);
            }
        }

        int newCols = colonnesAGarder.size();
        int newRows = nbContraintes + 1;
        double[][] nouveauTableau = new double[newRows][newCols];

        for (int i = 0; i < newRows; i++) {
            int colNouveau = 0;
            for (int j : colonnesAGarder) {
                nouveauTableau[i][colNouveau++] = tableau[i][j];
            }
        }

        double[] ligneObjectifPhase2 = construireLigneObjectifPhase2(nbColonnes, nbContraintes);
        for (int idx = 0; idx < newCols; idx++) {
            nouveauTableau[newRows - 1][idx] = ligneObjectifPhase2[idx];
        }

        // Remplacer le tableau par le nouveau
        tableau = nouveauTableau;
    }

    public void displayTableauSimplexe() {
    if (tableau == null) {
        tableau = construireTableauSimplexe();
    }
    StringBuilder sb = new StringBuilder();
    int nbVariables = matrice.getVariableCount();
    int nbColonnes = tableau[0].length;
    int indiceEcart = nbVariables;
    int indiceArtificiel = nbVariables;
    for (Contrainte c : matrice.getContraintes()) {
        if (c.getType() == ContrainteType.LESS_EQUAL || c.getType() == ContrainteType.GREATER_EQUAL) {
            indiceArtificiel++;
        }
    }
    sb.append("Tableau du simplexe :\n");
    sb.append(String.format("%8s ", "Base"));
    for (int j = 0; j < nbColonnes - 1; j++) {
        if (j < nbVariables) {
            sb.append(String.format("%8s ", "x" + (j + 1)));
        } else if (j < indiceArtificiel) {
            sb.append(String.format("%8s ", "s" + (j - nbVariables + 1)));
        } else {
            sb.append(String.format("%8s ", "a" + (j - indiceArtificiel + 1)));
        }
    }
    sb.append(String.format("%8s\n", "b"));
    for (int i = 0; i < tableau.length; i++) {
        String varName;
        if (i < variablesDeBase.length) {
            int col = variablesDeBase[i];
            varName = col < nbVariables ? "x" + (col + 1) :
                    col < indiceArtificiel ? "s" + (col - nbVariables + 1) :
                    "a" + (col - indiceArtificiel + 1);
        } else {
            varName = "z";
        }
        sb.append(String.format("%8s ", varName));
        for (int j = 0; j < tableau[0].length; j++) {
            sb.append(String.format("%8.2f ", tableau[i][j]));
        }
        sb.append("\n");
    }
    sb.append("\n");
    // Affichage console
    System.out.print(sb.toString());
    // Ajout à l'affichage pour Swing
    affichageEtapes.append(sb);
}

    // public void displayTableauSimplexe() {
    //     if (tableau == null) {
    //         tableau = construireTableauSimplexe();
    //     }
    //     System.out.println("Tableau du simplexe :");
    //     int nbVariables = matrice.getVariableCount();
    //     int nbColonnes = tableau[0].length;
    //     int indiceEcart = nbVariables;
    //     int indiceArtificiel = nbVariables;
    //     for (Contrainte c : matrice.getContraintes()) {
    //         if (c.getType() == ContrainteType.LESS_EQUAL || c.getType() == ContrainteType.GREATER_EQUAL) {
    //             indiceArtificiel++;
    //         }
    //     }
    //     for (int j = 0; j < nbColonnes - 1; j++) {
    //         if (j < nbVariables) {
    //             System.out.printf("%8s ", "x" + (j + 1));
    //         } else if (j < indiceArtificiel) {
    //             System.out.printf("%8s ", "s" + (j - nbVariables + 1));
    //         } else {
    //             System.out.printf("%8s ", "a" + (j - indiceArtificiel + 1));
    //         }
    //     }
    //     System.out.printf("%8s\n", "b");
    //     for (int i = 0; i < tableau.length; i++) {
    //         //System.out.printf("Ligne %d: ", i);
    //         for (int j = 0; j < tableau[0].length; j++) {
    //             System.out.printf("%8.2f ", tableau[i][j]);
    //         }
    //         System.out.println();
    //     }
    //     // Afficher les variables de base
    //     // System.out.println("Variables de base :");
    //     // for (int i = 0; i < variablesDeBase.length; i++) {
    //     //     int col = variablesDeBase[i];
    //     //     String varName = col < nbVariables ? "x" + (col + 1) :
    //     //                     col < indiceArtificiel ? "s" + (col - nbVariables + 1) :
    //     //                     "a" + (col - indiceArtificiel + 1);
    //     //     System.out.printf("Contrainte %d: %s = %.2f\n", i + 1, varName, tableau[i][nbColonnes - 1]);
    //     // }
    // }

    public int choisirColonnePivotPhase1() {
        int nbColonnes = tableau[0].length;
        int nbContraintes = matrice.getContrainteCount();
        int colPivot = -1;
        double maxCoef = 0.0;
        for (int j = 0; j < nbColonnes - 1; j++) {
            if (tableau[nbContraintes][j] > maxCoef) {
                maxCoef = tableau[nbContraintes][j];
                colPivot = j;
            }
        }
        // System.out.println("la colonne pivot est" + colPivot);
        return colPivot;
    }

    public int choisirLignePivot(int colPivot){
        int nbContraintes = matrice.getContrainteCount();
        int nbVariables = tableau[0].length;
        int lignePivot = -1;
        double minRatio = Double.POSITIVE_INFINITY;

        for(int i=0; i<nbContraintes;i++){
            double coef = tableau[i][colPivot];
            double droite = tableau[i][nbVariables-1];
            if(coef > 0){
                double ratio = droite/coef;
                if(ratio >=0 && ratio < minRatio){
                    minRatio = ratio;
                    lignePivot = i;
                }
            }
        }
        return lignePivot;
    }


    public String getVariableName(int col) {
        int nbVariables = matrice.getVariableCount();
        int indiceArtificiel = nbVariables;
        for (Contrainte c : matrice.getContraintes()) {
            if (c.getType() == ContrainteType.LESS_EQUAL || c.getType() == ContrainteType.GREATER_EQUAL) {
                indiceArtificiel++;
            }
        }
        if (col < nbVariables) {
            return "x" + (col + 1);
        } else if (col < indiceArtificiel) {
            return "s" + (col - nbVariables + 1);
        } else {
            return "a" + (col - indiceArtificiel + 1);
        }
    }

    public double[] getCoefObjectifOriginal() { //phase 2
        return coefObjectifOriginal;
    }

    public void resoudrePhase1() {
    if (tableau == null) {
        tableau = construireTableauSimplexe();
    }

    int nbContraintes = matrice.getContrainteCount();
    int nbColonnes = tableau[0].length;

    // if (indicesArtificiels.isEmpty()) {
    //     System.out.println("Phase 1 : Aucune variable artificielle, solution de base réalisable trouvée.");
    //     return;
    // }

    System.out.println("=== Phase 1 : Maximisation de -(somme des variables artificielles) ===");
    System.out.println("Tableau initial pour la phase 1 :");
    displayTableauSimplexe();

    int iteration = 0;
    while (!estOptimalPhase1()) {
        iteration++;
        System.out.printf("--- Itération %d ---\n", iteration);

        int colPivot = choisirColonnePivotPhase1();
        if (colPivot == -1) {
            System.out.println("Aucun coefficient positif dans la fonction objectif.");
            break;
        }

        int lignePivot = choisirLignePivot(colPivot); // Utilise ta version avec epsilon
        if (lignePivot == -1) {
            System.out.println("Aucun rapport positif valide trouvé.");
            throw new IllegalStateException("Phase 1 : Problème infaisable, aucun rapport positif valide.");
        }

        System.out.printf("Colonne pivot : %d (variable %s)\n", colPivot + 1, getVariableName(colPivot));
        System.out.printf("Ligne pivot : %d\n", lignePivot + 1);

        pivoter(lignePivot, colPivot);

        System.out.printf("Pivot effectué : Ligne %d, Colonne %d\n", lignePivot + 1, colPivot + 1);
        System.out.println("Tableau après pivot :");
        displayTableauSimplexe();
    }

    // Vérifier si la phase 1 a réussi
    /*if (Math.abs(tableau[nbContraintes][nbColonnes - 1]) > 1e-10) {
        throw new IllegalStateException("Phase 1 : Problème infaisable, variables artificielles non éliminées.");
    }*/

    // Vérifier que les variables artificielles ne sont plus dans la base
    for (int varBase : variablesDeBase) {
        if (indicesArtificiels.contains(varBase)) {
            throw new IllegalStateException("Phase 1 : Variables artificielles encore dans la base. tsy mety tapitra ny phase 1");
        }
    }

    System.out.println("Phase 1 terminée : Solution de base réalisable trouvée.");
}

// Méthode de pivotage (transforme la colonne pivot en 1 sur la ligne pivot et 0 ailleurs)
public void pivoter(int lignePivot, int colPivot) {
    int nbColonnes = tableau[0].length;

    double pivotValue = tableau[lignePivot][colPivot]; // coefficient de la ligne pivot (intersection)
    // Normaliser la ligne pivot
    for (int j = 0; j < nbColonnes; j++) {
        tableau[lignePivot][j] /= pivotValue;
    }

    // Annuler la colonne pivot dans les autres lignes
    for (int i = 0; i < tableau.length; i++) {
        if (i != lignePivot) {
            double factor = tableau[i][colPivot];
            for (int j = 0; j < nbColonnes; j++) { //par exemple L2 -> L2- 2L1 par exemple
                tableau[i][j] -= factor * tableau[lignePivot][j];
            }
        }
    }

    // Mettre à jour la variable de base
    variablesDeBase[lignePivot] = colPivot;
}

// Critère d’optimalité pour la phase 1
public boolean estOptimalPhase1() { // optimal rehefa tsisy superieur a zero ny ligne 4
    int nbColonnes = tableau[0].length;
    int nbContraintes = matrice.getContrainteCount();
    for (int j = 0; j < nbColonnes - 1; j++) {
        if (tableau[nbContraintes][j] > 1e-10) {
            return false;
        }
    }
    return true;
}
}