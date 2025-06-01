import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.io.*;

public class SimplexeSwing extends JFrame {
    private JTextField nbVarField, nbConstrField;
    private JPanel inputPanel, resultPanel, contraintesPanel;
    private JButton nextButton, solveButton, saveButton, ajouterContrainteButton;
    private ArrayList<JTextField[]> contraintesFields = new ArrayList<>();
    private JTextField[] objFields;
    private ArrayList<JComboBox<String>> typeBoxes = new ArrayList<>();
    private ArrayList<JTextField> droiteFields = new ArrayList<>();
    private JComboBox<String> maxMinBox;
    private JTextArea resArea;
    private int nbVar = 0;
    private int nbConstrActuel = 0;

    public SimplexeSwing() {
        setTitle("Résolution Simplexe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Nombre de variables:"));
        nbVarField = new JTextField(3);
        topPanel.add(nbVarField);
        topPanel.add(new JLabel("Nombre de contraintes:"));
        nbConstrField = new JTextField(3);
        topPanel.add(nbConstrField);
        nextButton = new JButton("Suivant");
        topPanel.add(nextButton);
        add(topPanel, BorderLayout.NORTH);

        inputPanel = new JPanel();
        add(inputPanel, BorderLayout.CENTER);

        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        add(resultPanel, BorderLayout.SOUTH);

        saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> saveResultToFile());
        nextButton.addActionListener(e -> setupInputFields());
    }

    private void saveResultToFile() {
        String chemin = "resultat.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(chemin))) {
            writer.write(resArea.getText());
            JOptionPane.showMessageDialog(this,
                "Résultats enregistrés avec succès dans le fichier:\n",
                "Enregistrement réussi",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'enregistrement: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void setupInputFields() {
        inputPanel.removeAll();
        resultPanel.removeAll();
        contraintesFields.clear();
        typeBoxes.clear();
        droiteFields.clear();

        int nbConstr;
        try {
            nbVar = Integer.parseInt(nbVarField.getText());
            nbConstr = Integer.parseInt(nbConstrField.getText());
            if (nbVar <= 0 || nbConstr <= 0) throw new Exception();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Entrer des nombres valides.");
            return;
        }

        nbConstrActuel = nbConstr;
        inputPanel.setLayout(new BorderLayout());

        // Panel pour la fonction objectif
        JPanel objPanel = new JPanel();
        objPanel.add(new JLabel("Fonction objectif:"));
        objFields = new JTextField[nbVar];
        for (int i = 0; i < nbVar; i++) {
            objFields[i] = new JTextField(4);
            objPanel.add(objFields[i]);
            objPanel.add(new JLabel("x" + (i + 1) + (i < nbVar - 1 ? " + " : "")));
        }
        maxMinBox = new JComboBox<>(new String[]{"Maximiser"});
        inputPanel.add(objPanel, BorderLayout.NORTH);

        // Panel principal pour les contraintes avec scroll
        contraintesPanel = new JPanel();
        contraintesPanel.setLayout(new BoxLayout(contraintesPanel, BoxLayout.Y_AXIS));
        
        // Ajouter les contraintes initiales
        for (int i = 0; i < nbConstr; i++) {
            ajouterUneContrainte();
        }

        JScrollPane scrollPane = new JScrollPane(contraintesPanel);
        scrollPane.setPreferredSize(new Dimension(650, 300));
        inputPanel.add(scrollPane, BorderLayout.CENTER);

        // Panel pour les boutons
        JPanel buttonsPanel = new JPanel();
        ajouterContrainteButton = new JButton("Ajouter Contrainte");
        ajouterContrainteButton.addActionListener(e -> {
            ajouterUneContrainte();
            nbConstrActuel++;
            contraintesPanel.revalidate();
            contraintesPanel.repaint();
        });
        
        solveButton = new JButton("Résoudre");
        solveButton.addActionListener(e -> solveSimplexe());
        
        buttonsPanel.add(ajouterContrainteButton);
        buttonsPanel.add(solveButton);
        inputPanel.add(buttonsPanel, BorderLayout.SOUTH);

        inputPanel.revalidate();
        inputPanel.repaint();
    }

    private void ajouterUneContrainte() {
        JPanel cPanel = new JPanel();
        cPanel.setBorder(BorderFactory.createTitledBorder("Contrainte " + (contraintesFields.size() + 1)));
        
        JTextField[] fields = new JTextField[nbVar];
        for (int j = 0; j < nbVar; j++) {
            fields[j] = new JTextField(4);
            cPanel.add(fields[j]);
            cPanel.add(new JLabel("x" + (j + 1) + (j < nbVar - 1 ? " + " : "")));
        }
        
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"<=", ">=", "="});
        cPanel.add(typeBox);
        
        JTextField droiteField = new JTextField(5);
        cPanel.add(droiteField);
        
        // Bouton pour supprimer cette contrainte
        JButton supprimerButton = new JButton("Supprimer");
        supprimerButton.addActionListener(e -> {
            int index = contraintesFields.indexOf(fields);
            if (index != -1) {
                contraintesFields.remove(index);
                typeBoxes.remove(index);
                droiteFields.remove(index);
                contraintesPanel.remove(cPanel);
                nbConstrActuel--;
                // Renuméroter les contraintes
                for (int i = 0; i < contraintesPanel.getComponentCount(); i++) {
                    JPanel panel = (JPanel) contraintesPanel.getComponent(i);
                    panel.setBorder(BorderFactory.createTitledBorder("Contrainte " + (i + 1)));
                }
                contraintesPanel.revalidate();
                contraintesPanel.repaint();
            }
        });
        cPanel.add(supprimerButton);
        
        contraintesFields.add(fields);
        typeBoxes.add(typeBox);
        droiteFields.add(droiteField);
        contraintesPanel.add(cPanel);
    }

    private void solveSimplexe() {
        try {
            double[] objCoefs = new double[nbVar];
            for (int i = 0; i < nbVar; i++) {
                objCoefs[i] = Double.parseDouble(objFields[i].getText());
            }
            boolean isMax = maxMinBox.getSelectedIndex() == 0;
            FonctionObjectif fo = new FonctionObjectif(objCoefs, isMax);

            Matrice matrice = new Matrice();
            matrice.setFonctionObjectif(fo);

            for (int i = 0; i < contraintesFields.size(); i++) {
                double[] coef = new double[nbVar];
                for (int j = 0; j < nbVar; j++) {
                    coef[j] = Double.parseDouble(contraintesFields.get(i)[j].getText());
                }
                double droite = Double.parseDouble(droiteFields.get(i).getText());
                String typeStr = (String) typeBoxes.get(i).getSelectedItem();
                ContrainteType type = ContrainteType.LESS_EQUAL;
                if (typeStr.equals(">=")) type = ContrainteType.GREATER_EQUAL;
                else if (typeStr.equals("=")) type = ContrainteType.EQUAL;
                matrice.add(new Contrainte(coef, droite, type));
            }

            Simplexe simplexe = new Simplexe(matrice);
            simplexe.resetAffichageEtapes();
            simplexe.resoudreSimplexe();

            // Affichage résultat
            resultPanel.removeAll();
            resArea = new JTextArea(25, 70);
            resArea.setEditable(false);
            StringBuilder sb = new StringBuilder();
            sb.append(simplexe.getAffichageEtapes());
            double[] sol = simplexe.getSolution();
            sb.append("Résultat du simplexe:\n");
            for (int i = 0; i < nbVar; i++) {
                sb.append("x").append(i + 1).append(" = ").append(sol[i]).append("\n");
            }
            sb.append("Valeur optimale : ").append(sol[nbVar]).append("\n");
            resArea.setText(sb.toString());
            resultPanel.add(new JScrollPane(resArea));
            resultPanel.add(saveButton);
            resultPanel.revalidate();
            resultPanel.repaint();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur dans la saisie : " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SimplexeSwing().setVisible(true));
    }
}