package id.net.gmedia.selby.Model;

public class BarangJualModel extends BarangModel{

    private int jumlah;

    public BarangJualModel(String id, String nama, String url, double harga, int jumlah){
        super(id, nama, url, harga);
        this.jumlah = jumlah;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void increase(){
        jumlah++;
    }

    public void decrease(){
        jumlah--;
    }
}
