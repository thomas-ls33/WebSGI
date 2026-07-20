package fr.esgi.websgi.entity;

public enum RequestStatus {
    EN_ATTENTE("En attente"),
    EN_COURS("En cours de traitement"),
    VALIDEE("Validée"),
    REFUSEE("Refusée");

    private final String label;

    RequestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static RequestStatus fromLabel(String label) {
        for (RequestStatus status : values()) {
            if (status.label.equalsIgnoreCase(label) || status.name().equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Statut inconnu : " + label);
    }
}
