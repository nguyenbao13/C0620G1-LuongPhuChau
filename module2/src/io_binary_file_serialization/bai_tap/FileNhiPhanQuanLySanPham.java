package io_binary_file_serialization.bai_tap;

import java.io.*;
import java.util.*;

public class FileNhiPhanQuanLySanPham {
    public static Scanner scanner = new Scanner(System.in);
    public static HashMap<String, NhiPhanSanPham> danhSach = new HashMap<>();
    public static final String PATH = "E:\\C0620G1\\module2\\src\\io_binary_file_serialization\\bai_tap\\demo.bin";

    public static void them() throws Exception {
        docFileNhiPhan();

        System.out.print("Nhập mã sản phẩm : ");
        String maSanPham = scanner.nextLine();

        System.out.print("Nhập tên sản phẩm : ");
        String ten = scanner.nextLine();

        System.out.print("Nhập hãng sản xuất : ");
        String hangSanXuat = scanner.nextLine();

        System.out.print("Nhập giá sản phẩm : ");
        String gia = scanner.nextLine();

        System.out.print("Nhập mô tả khác cho sản phẩm : ");
        String moTa = scanner.nextLine();

        System.out.println("Thêm sản phẩm thành công !");

        danhSach.put(maSanPham, new NhiPhanSanPham(maSanPham, ten, hangSanXuat, gia, moTa));
        ghiVaoFileNhiPhan();
        danhSach.clear();
    }

    public static void hienThi() throws Exception {
        docFileNhiPhan();
        for (Map.Entry<String, NhiPhanSanPham> entry : danhSach.entrySet()) {
            System.out.println(entry.getValue().toString());
        }
    }

    public static void timKiem() throws Exception {
        System.out.print("Nhập tên sản phẩm bạn muốn tìm kiếm : ");
        String ten = scanner.nextLine();
        docFileNhiPhan();
        boolean check = true;
        for (Map.Entry<String, NhiPhanSanPham> entry : danhSach.entrySet()) {
            if (entry.getValue().getTenSanPham().equals(ten)) {
                check = false;
                System.out.println(entry.getValue().toString());
            }
        }
        if (check) System.out.println("Sản phẩm này không tồn tại !");
    }

    public static void ghiVaoFileNhiPhan() throws Exception {
        FileOutputStream fileOutput = new FileOutputStream(PATH);
        try {
            ObjectOutputStream output = new ObjectOutputStream(fileOutput);
            for (Map.Entry<String, NhiPhanSanPham> entry : danhSach.entrySet()) {
                output.writeObject(entry.getValue());
            }
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void docFileNhiPhan() throws Exception {
        FileInputStream fileInput = new FileInputStream(PATH);
        try {
            ObjectInputStream input = new ObjectInputStream(fileInput);
            NhiPhanSanPham sanPham;
            while ((sanPham = (NhiPhanSanPham) input.readObject()) != null) {
                danhSach.put(sanPham.getMaSanPham(), sanPham);
                //System.out.println(input.read());
            }
            input.close();
        } catch (Exception e) {
        }
    }
}