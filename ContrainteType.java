public enum ContrainteType {
    LESS_EQUAL, GREATER_EQUAL, EQUAL;

    // Retourne le symbole correspondant pour l'affichage
    public String getSymbol() {
        switch (this) {
            case LESS_EQUAL:
                return "<=";
            case GREATER_EQUAL:
                return ">=";
            case EQUAL:
                return "=";
            default:
                return "";
        }
    }
}