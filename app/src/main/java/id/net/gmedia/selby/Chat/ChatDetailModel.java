package id.net.gmedia.selby.Chat;

public class ChatDetailModel {
    private String id;
    private String chat;
    private boolean pengirim;

    public ChatDetailModel(String id, String chat, boolean pengirim){
        this.id = id;
        this.chat = chat;
        this.pengirim = pengirim;
    }

    public String getId() {
        return id;
    }

    public String getChat() {
        return chat;
    }

    public boolean isPengirim() {
        return pengirim;
    }
}
