package id.net.gmedia.selby.Model;

public class TransaksiModel {
    private String id;
    private String nomor;
    private String status;
    private String status_string;
    private double total;

    public TransaksiModel(String id, String nomor, String status, String status_string, double total){
        this.id = id;
        this.nomor = nomor;
        this.status = status;
        this.status_string = status_string;
        this.total = total;
    }

    public String getStatus_string() {
        return status_string;
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
