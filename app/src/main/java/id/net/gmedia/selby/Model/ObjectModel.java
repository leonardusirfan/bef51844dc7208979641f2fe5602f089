package id.net.gmedia.selby.Model;

public class ObjectModel {
    private String id;
    private String value;

    public ObjectModel(String id, String value){
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
