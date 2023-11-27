package com.example.autocar;

public class Offre {
    private String id;
    private String offreText;
    private String status;
    private String userId;

    public Offre() {
    }

    public Offre(String id, String offreText, String status, String userId) {
        this.id = id;
        this.offreText = offreText;
        this.status = status;
        this.userId = userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setOffreText(String offreText) {
        this.offreText = offreText;
    }

    public String getOffreText() {
        return offreText;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTexte() {
        return "Prix proposé : " + getOffreText() + " " + getStatus() + "\n" +
                "Commentaire : " + getUserId();
    }
}


