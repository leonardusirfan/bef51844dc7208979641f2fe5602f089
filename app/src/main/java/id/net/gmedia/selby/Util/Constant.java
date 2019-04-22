package id.net.gmedia.selby.Util;

import android.util.Base64;
import android.util.Log;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Constant {
    /*
        Kelas penampung variabel2 konstan
     */

    //Header Request
    public final static Map<String, String> HEADER_AUTH = new HashMap<String, String>(){{put("Auth-Key", "gmedia"); put("Client-Service", "selby-ecommerce");}};

    //Extra
    public static final String EXTRA_BITMAP = "bitmap";

    public static final String TAG = "selbi_log";

    public static final String EXTRA_JENIS_BARANG = "jenis_barang";
    public static final String EXTRA_USER = "user";
    public static final String EXTRA_CHAT_FROM = "chat_from";
    public static final String EXTRA_ARTIS = "nama_artis";
    public static final String EXTRA_BERITA = "berita";

    public static final int BARANG_PRELOVED = 31;
    public static final int BARANG_MERCHANDISE = 32;
    public static final int BARANG_LELANG = 33;
    public static final int BARANG_DONASI = 34;

    //URL Request
    private static final String BASE_URL = "http://gmedia.bz/selbi/api/";
    public static final String URL_HOME_SLIDE = BASE_URL + "Slider/index";
    public static final String URL_ARTIS = BASE_URL + "Penjual/index";
    public static final String URL_BARANG_MASTER = BASE_URL + "Produk/index";
    public static final String URL_DETAIL_PRODUK = BASE_URL + "Produk/details";
    public static final String URL_DETAIL_PRODUK_REVIEW = BASE_URL + "Produk/review";
    public static final String URL_LELANG = BASE_URL + "Lelang/index";
    public static final String URL_DETAIL_LELANG = BASE_URL + "Lelang/details";
    public static final String URL_AUTENTIFIKASI = BASE_URL + "Authentication/register";
    public static final String URL_FAVORIT = BASE_URL + "Favorit/index";
    public static final String URL_TAMBAH_FAVORIT = BASE_URL + "Favorit/add_to_favorit";
    public static final String URL_HAPUS_FAVORIT = BASE_URL + "Favorit/hapus_favorit";
    public static final String URL_KERANJANG = BASE_URL + "Keranjang/index";
    public static final String URL_TRANSAKSI = BASE_URL + "Transaksi/index";
    public static final String URL_DETAIL_TRANSAKSI = BASE_URL + "Transaksi/details";
    public static final String URL_TAMBAH_KERANJANG = BASE_URL + "Keranjang/add_to_cart";
    public static final String URL_HAPUS_KERANJANG = BASE_URL + "Keranjang/hapus_keranjang";
    public static final String URL_FEED = BASE_URL + "Feed/index";
    public static final String URL_GALLERY = BASE_URL + "Feed/gallery";
    public static final String URL_BID = BASE_URL + "Lelang/bid";
    public static final String URL_PROFIL = BASE_URL + "Profile/index";
    public static final String URL_UPLOAD_FOTO_PROFIL = BASE_URL + "Profile/edit_image";
    public static final String URL_EDIT_PROFIL = BASE_URL + "Profile/edit_profile";
    public static final String URL_TAMBAH_ULASAN = BASE_URL + "Produk/add_review";
    public static final String URL_DISKUSI_BARANG = BASE_URL + "Produk/diskusi";
    public static final String URL_TAMBAH_DISKUSI_BARANG = BASE_URL + "Produk/add_diskusi";
    public static final String URL_LIST_BID = BASE_URL + "Lelang/list_bid";
    public static final String URL_FOLLOW_PENJUAL = BASE_URL + "Profile/follow";
    public static final String URL_KEGIATAN = BASE_URL + "Feed/kegiatan";
    public static final String URL_ARTIS_DIFOLLOW = BASE_URL + "Penjual/following";
    public static final String URL_KATEGORI_BARANG = BASE_URL + "Category/index";
    public static final String URL_KATEGORI_ARTIS = BASE_URL + "Pekerjaan/index";
    public static final String URL_BARANG_RATING = BASE_URL + "Produk/jumlah_rating";
    public static final String URL_HOT_ITEM = BASE_URL + "Produk/hot_item";
    public static final String URL_CHAT = BASE_URL + "Chat/list_chat";
    public static final String URL_CHAT_DETAIL = BASE_URL + "Chat/detail_chat";
    public static final String URL_CHAT_TAMBAH = BASE_URL + "Chat/insert";
    public static final String URL_FCM_UPDATE = BASE_URL + "Authentication/update_fcm_id";
    public static final String URL_GREETING_ADD = BASE_URL + "Greeting/add_greeting";
    public static final String URL_HOT_NEWS = BASE_URL + "Hot_News/view";
    public static final String URL_KURIR_PROVINSI = BASE_URL + "ongkir/provinsi";
    public static final String URL_KURIR_KOTA = BASE_URL + "ongkir/kota";
    public static final String URL_KURIR_ONGKIR = BASE_URL + "ongkir/hitung";

    //Token heaader dengan enkripsi
    public static Map<String, String> getTokenHeader(String uuid){
        Map<String, String> header = new HashMap<>();
        header.put("Auth-key", "gmedia");
        header.put("Client-Service", "selby-ecommerce");
        header.put("User-id", uuid);

        String timestamp =  new SimpleDateFormat("SSSHHyyyyssMMddmm", Locale.getDefault()).format(new Date());
        String signature = sha256(uuid+"&"+timestamp,uuid+"die");

        /*System.out.println("UUID : " + uuid);
        System.out.println("Timestamp : " + timestamp);
        System.out.println("Signature : " + signature);*/

        header.put("Timestamp", timestamp);
        header.put("Signature", signature);
        return header;
    }

    private static String sha256(String message, String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            return Base64.encodeToString(sha256_HMAC.doFinal(message.getBytes()), Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        Log.w("SHA256", "Return string kosong");
        return "";
    }

    public static String getSatuanWaktu(int i){
        switch (i){
            case 0:return "hari";
            case 1:return "bulan";
            case 2:return "tahun";
            default:return "";
        }
    }
}

