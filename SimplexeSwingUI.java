// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.*;
// import java.io.*;

// public class SimplexeSwingUI extends JFrame {
//     private JTextField nbVariablesField, nbContraintesField;
//     private JPanel inputPanel, resultPanel;
//     private JButton nextButton, solveButton, saveButton;
//     private JTextField[][] contraintesFields;
//     private JTextField[] objectifFields, secondMembreFields;
//     private JComboBox<String>[] signesBoxes;
//     private JTextArea resultArea;

//     private int nbVariables, nbContraintes;

//     public SimplexeSwingUI() {
//         setTitle("Simplexe - Interface graphique");
//         setDefaultCloseOperation(EXIT_ON_CLOSE);
//         setSize(700, 600);
//         setLocationRelativeTo(null);
//         setLayout(new BorderLayout());

//         inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
//         inputPanel.setBorder(BorderFactory.createTitledBorder("Definir le probleme"));

//         inputPanel.add(new JLabel("Nombre de variables :"));
//         nbVariablesField = new JTextField("3");
//         inputPanel.add(nbVariablesField);

//         inputPanel.add(new JLabel("Nombre de contraintes :"));
//         nbContraintesField = new JTextField("3");
//         inputPanel.add(nbContraintesField);

//         nextButton = new JButton("Suivant");
//         inputPanel.add(nextButton);

//         add(inputPanel, BorderLayout.NORTH);

//         resultPanel = new JPanel(new BorderLayout());
//         resultArea = new JTextArea(12, 50);
//         resultArea.setEditable(false);
//         resultPanel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
//         resultPanel.setBorder(BorderFactory.createTitledBorder("Resultat"));
//         add(resultPanel, BorderLayout.SOUTH);

//         nextButton.addActionListener(e -> showDataEntry());

//         setVisible(true);
//     }

//     private void showDataEntry() {
//         try {
//             nbVariables = Integer.parseInt(nbVariablesField.getText().trim());
//             nbContraintes = Integer.parseInt(nbContraintesField.getText().trim());
//         } catch (NumberFormatException ex) {
//             JOptionPane.showMessageDialog(this, "Veuillez entrer des entiers valides.");
//             return;
//         }

//         JPanel dataPanel = new JPanel(new BorderLayout(10, 10));
//         JPanel objectifPanel = new JPanel(new GridLayout(1, nbVariables));
//         objectifFields = new JTextField[nbVariables];
//         objectifPanel.setBorder(BorderFactory.createTitledBorder("Fonction objectif:"));

//         for (int i = 0; i < nbVariables; i++) {
//             objectifFields[i] = new JTextField("0");
//             objectifPanel.add(new JLabel("x" + (i + 1)));
//             objectifPanel.add(objectifFields[i]);
//         }
//         dataPanel.add(objectifPanel, BorderLayout.NORTH);

//         JPanel contraintesPanel = new JPanel(new GridLayout(nbContraintes, nbVariables + 2));
//         contraintesPanel.setBorder(BorderFactory.createTitledBorder("Contraintes"));
//         contraintesFields = new JTextField[nbContraintes][nbVariables];
//         secondMembreFields = new JTextField[nbContraintes];
//         signesBoxes = new JComboBox[nbContraintes];

//         for (int i = 0; i < nbContraintes; i++) {
//             for (int j = 0; j < nbVariables; j++) {
//                 contraintesFields[i][j] = new JTextField("0");
//                 contraintesPanel.add(contraintesFields[i][j]);
//             }
//             signesBoxes[i] = new JComboBox<>(new String[]{"<", "=", ">"});
//             contraintesPanel.add(signesBoxes[i]);
//             secondMembreFields[i] = new JTextField("0");
//             contraintesPanel.add(secondMembreFields[i]);
//         }
//         dataPanel.add(contraintesPanel, BorderLayout.CENTER);

//         solveButton = new JButton("Resoudre");
//         dataPanel.add(solveButton, BorderLayout.SOUTH);

//         getContentPane().removeAll();
//         add(dataPanel, BorderLayout.CENTER);
//         add(resultPanel, BorderLayout.SOUTH);
//         revalidate();
//         repaint();

//         solveButton.addActionListener(e -> solveSimplexe());
//     }

//     private void solveSimplexe() {
//         try {
//             double[] objectif = new double[nbVariables];
//             for (int i = 0; i < nbVariables; i++) {
//                 objectif[i] = Double.parseDouble(objectifFields[i].getText().trim());
//             }

//             double[][] contraintes = new double[nbContraintes][nbVariables];
//             double[] secondMembre = new double[nbContraintes];
//             char[] signes = new char[nbContraintes];

//             for (int i = 0; i < nbContraintes; i++) {
//                 for (int j = 0; j < nbVariables; j++) {
//                     contraintes[i][j] = Double.parseDouble(contraintesFields[i][j].getText().trim());
//                 }
//                 String signe = (String) signesBoxes[i].getSelectedItem();
//                 signes[i] = signe.charAt(0);
//                 secondMembre[i] = Double.parseDouble(secondMembreFields[i].getText().trim());
//             }

//             ProgrammeLineaire programme = new ProgrammeLineaire(objectif, contraintes, secondMembre, signes, true);
//             Simplexe simplexe = new Simplexe(programme);

//             simplexe.resoudre();

//             StringBuilder sb = new StringBuilder();
//             java.util.List<double[][]> tableaux = simplexe.getTableauxPhases();
//             java.util.List<int[]> bases = simplexe.getBasesPhases();

//             String[] titres = {
//                 "Tableau initial de la Phase 1",
//                 "Tableau final de la Phase 1",
//                 "Tableau initial de la Phase 2",
//                 "Tableau final de la Phase 2"
//             };

//             for (int k = 0; k < tableaux.size(); k++) {
//                 sb.append(titres[k]).append(" :\n");
//                 double[][] tab = tableaux.get(k);
//                 int[] base = (k < bases.size()) ? bases.get(k) : null;

//                 // Affichage des lignes des contraintes (lignes 1 à n)
//                 for (int i = 1; i < tab.length; i++) {
//                     for (double val : tab[i]) {
//                         sb.append(String.format("%10.4f", val)).append(" ");
//                     }
//                     // Affiche la variable de base de cette ligne si possible
//                     // if (base != null && i - 1 < base.length) {
//                     //     sb.append("   |  Base: x").append(base[i - 1] + 1);
//                     // }
//                     sb.append("\n");
//                 }
//                 // Affichage de la ligne de la fonction objectif (ligne 0)
//                 for (double val : tab[0]) {
//                     sb.append(String.format("%10.4f", val)).append(" ");
//                 }
//                 sb.append("   |  Fonction objectif\n");

//                 // Solution finale uniquement pour le dernier tableau
//                 if (k == tableaux.size() - 1) {
//                     double[] sol = simplexe.getSolution();
//                     sb.append("\nSolution optimale :\n");
//                     for (int i = 0; i < sol.length; i++) {
//                         sb.append("x").append(i + 1).append(" = ").append(String.format("%.4f", sol[i])).append("\n");
//                     }
//                     sb.append("Valeur optimale : ").append(String.format("%.4f", simplexe.getValeurOptimale())).append("\n");
//                 }
//                 sb.append("\n");
//             }

//             resultArea.setText(sb.toString());
            
//             // Ajouter le bouton d'enregistrement après avoir affiché les résultats
//             if (saveButton == null) {
//                 saveButton = new JButton("Enregistrer résultat");
//                 saveButton.addActionListener(e -> saveResultToFile());
                
//                 JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
//                 buttonPanel.add(saveButton);
//                 resultPanel.add(buttonPanel, BorderLayout.SOUTH);
//                 revalidate();
//                 repaint();
//             }
//         } catch (Exception ex) {
//             resultArea.setText("Erreur : " + ex.getMessage());
//         }
//     }

//     // Nouvelle méthode pour enregistrer les résultats dans un fichier texte
//     private void saveResultToFile() {
//         JFileChooser fileChooser = new JFileChooser();
//         fileChooser.setDialogTitle("Enregistrer les résultats");
//         fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Fichiers texte (.txt)", "txt"));
        
//         int userSelection = fileChooser.showSaveDialog(this);
        
//         if (userSelection == JFileChooser.APPROVE_OPTION) {
//             File fileToSave = fileChooser.getSelectedFile();
            
//             // Ajouter l'extension .txt si elle n'est pas présente
//             if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
//                 fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
//             }
            
//             try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
//                 writer.write(resultArea.getText());
//                 JOptionPane.showMessageDialog(this, 
//                     "Résultats enregistrés avec succès dans le fichier:\n" + fileToSave.getAbsolutePath(), 
//                     "Enregistrement réussi", 
//                     JOptionPane.INFORMATION_MESSAGE);
//             } catch (IOException e) {
//                 JOptionPane.showMessageDialog(this, 
//                     "Erreur lors de l'enregistrement: " + e.getMessage(), 
//                     "Erreur", 
//                     JOptionPane.ERROR_MESSAGE);
//             }
//         }
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(SimplexeSwingUI::new);
//     }
// }