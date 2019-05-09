package id.net.gmedia.selby.Model;

public class TransaksiModel {
    private String id;
    private String nomor;
    private String status;
    private double total;

    public TransaksiModel(String id, String nomor, String status, double total){
        this.id = id;
        this.nomor = nomor;
        this.status = status;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public double getTotal() {
        return total;
    }

    public String getNomor() {
        return nomor;
    }

    public String getStatus() {
        return status;
    }
}
