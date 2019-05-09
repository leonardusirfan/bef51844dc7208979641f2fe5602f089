package id.net.gmedia.selby.Chat;

import id.net.gmedia.selby.Model.UserModel;

public class ChatModel {
    private String id;
    private String chat;
    private String tanggal;
    private UserModel pengirim;
    private int baru;

    public ChatModel(String id, String chat, String tanggal, UserModel pengirim, int baru){
        this.id = id;
        this.chat = chat;
        this.tanggal = tanggal;
        this.pengirim = pengirim;
        this.baru = baru;
    }

    public UserModel getPengirim() {
        return pengirim;
    }

    public String getId() {
        return id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getChat() {
        return chat;
    }

    public int getBaru() {
        return baru;
    }

    public void setBaru(int baru) {
        this.baru = baru;
    }
}
