package book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

// Giao diện chính của chương trình Quản lý Sách (sử dụng Swing)
public class BookGUI extends JFrame {
    private final BookDAO bookDAO = new BookDAO();

    // Các thành phần giao diện (Swing Components)
    private JTable tblBooks;
    private DefaultTableModel tableModel;
    
    private JTextField txtId;
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtReleaseDate;
    private JTextArea txtContent;
    
    private JTextField txtSearch;
    private JComboBox<String> cbSearchType;

    public BookGUI() {
        super("Hệ Thống Quản Lý Sách Cao Cấp (Book Management)");
        initializeUI();
        loadAllBooks();
    }

    private void initializeUI() {
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Thiết lập bảng màu chủ đạo cho giao diện
        Color primaryColor = new Color(52, 73, 94);
        Color accentColor = new Color(41, 128, 185);
        Color bgLeftPanel = new Color(245, 247, 250);

        // --- 1. Khung phía trên: Tìm kiếm và Tiêu đề ứng dụng ---
        JPanel pnlTop = new JPanel(new BorderLayout(15, 10));
        pnlTop.setBackground(primaryColor);
        pnlTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblAppTitle = new JLabel("HỆ THỐNG QUẢN LÝ SÁCH", JLabel.LEFT);
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAppTitle.setForeground(Color.WHITE);
        pnlTop.add(lblAppTitle, BorderLayout.WEST);

        // Khung tìm kiếm đặt bên trong Top Panel
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSearch.setForeground(Color.WHITE);

        cbSearchType = new JComboBox<>(new String[]{"Theo Tên (Title)", "Theo ID"});
        cbSearchType.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JButton btnSearch = new JButton("Tìm Kiếm");
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setBackground(accentColor);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        JButton btnRefresh = new JButton("Tải Lại");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(new Color(39, 174, 96));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                loadAllBooks();
                clearForm();
            }
        });

        pnlSearch.add(lblSearch);
        pnlSearch.add(cbSearchType);
        pnlSearch.add(txtSearch);
        pnlSearch.add(btnSearch);
        pnlSearch.add(btnRefresh);
        pnlTop.add(pnlSearch, BorderLayout.EAST);
        add(pnlTop, BorderLayout.NORTH);

        // --- 2. Khung bên trái: Form nhập thông tin sách ---
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(bgLeftPanel);
        pnlLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 224, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        pnlLeft.setPreferredSize(new Dimension(360, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.weightx = 1.0;

        // Tiêu đề của form nhập liệu
        JLabel lblFormTitle = new JLabel("Thông Tin Sách");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormTitle.setForeground(primaryColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        pnlLeft.add(lblFormTitle, gbc);

        // Fields
        gbc.gridwidth = 1;

        // Mã sách (ID) - Tự sinh từ DB nên không cho sửa
        gbc.gridx = 0; gbc.gridy = 1;
        pnlLeft.add(new JLabel("Mã Sách (ID):"), gbc);
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(230, 233, 238));
        txtId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtId, gbc);

        // Title
        gbc.gridx = 0; gbc.gridy = 2;
        pnlLeft.add(new JLabel("Tiêu Đề (Title):"), gbc);
        txtTitle = new JTextField();
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtTitle, gbc);

        // Author
        gbc.gridx = 0; gbc.gridy = 3;
        pnlLeft.add(new JLabel("Tác Giả (Author):"), gbc);
        txtAuthor = new JTextField();
        txtAuthor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtAuthor, gbc);

        // Release Date
        gbc.gridx = 0; gbc.gridy = 4;
        pnlLeft.add(new JLabel("Ngày Phát Hành:"), gbc);
        txtReleaseDate = new JTextField();
        txtReleaseDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtReleaseDate, gbc);

        // Content
        gbc.gridx = 0; gbc.gridy = 5;
        pnlLeft.add(new JLabel("Nội Dung (Content):"), gbc);
        txtContent = new JTextArea(8, 15);
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        JScrollPane scrollContent = new JScrollPane(txtContent);
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        pnlLeft.add(scrollContent, gbc);

        add(pnlLeft, BorderLayout.WEST);

        // --- 3. Khung ở giữa: Bảng hiển thị danh sách sách ---
        JPanel pnlCenter = new JPanel(new BorderLayout(5, 5));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 20));

        JLabel lblListTitle = new JLabel("Danh Sách Sách Trong Thư Viện");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblListTitle.setForeground(primaryColor);
        pnlCenter.add(lblListTitle, BorderLayout.NORTH);

        // Cấu hình bảng hiển thị dữ liệu
        String[] columnNames = {"ID", "Tiêu Đề (Title)", "Tác Giả (Author)", "Ngày Phát Hành"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp dữ liệu trên bảng
            }
        };

        tblBooks = new JTable(tableModel);
        tblBooks.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBooks.setRowHeight(24);
        tblBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBooks.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblBooks.getTableHeader().setBackground(new Color(230, 233, 238));

        tblBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromSelectedRow();
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblBooks);
        pnlCenter.add(scrollTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // --- 4. Khung phía dưới: Các nút chức năng (Thêm, Sửa, Xóa, Đọc sách, Xuất file) ---
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        pnlSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));
        pnlSouth.setBackground(bgLeftPanel);

        JButton btnAdd = new JButton("Thêm Sách (Add)");
        setupButton(btnAdd, new Color(39, 174, 96));
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAdd();
            }
        });

        JButton btnUpdate = new JButton("Cập Nhật (Update)");
        setupButton(btnUpdate, new Color(241, 196, 15));
        btnUpdate.setForeground(Color.BLACK); // Chữ màu đen trên nền nút màu vàng cho dễ nhìn
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performUpdate();
            }
        });

        JButton btnRemove = new JButton("Xóa Sách (Remove)");
        setupButton(btnRemove, new Color(192, 57, 43));
        btnRemove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRemove();
            }
        });

        JButton btnClear = new JButton("Làm Sạch Form");
        setupButton(btnClear, new Color(127, 140, 141));
        btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        JButton btnRead = new JButton("Đọc Sách (Auto Scroll)");
        setupButton(btnRead, new Color(155, 89, 182));
        btnRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openBookReader();
            }
        });

        // Các nút xuất file text báo cáo
        JButton btnExportSingle = new JButton("Xuất Cuốn Đang Chọn (Export 1)");
        setupButton(btnExportSingle, accentColor);
        btnExportSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportSelectedBook();
            }
        });

        JButton btnExportAll = new JButton("Xuất Toàn Bộ Sách (Export List)");
        setupButton(btnExportAll, new Color(44, 62, 80));
        btnExportAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportAllBooks();
            }
        });

        pnlSouth.add(btnAdd);
        pnlSouth.add(btnUpdate);
        pnlSouth.add(btnRemove);
        pnlSouth.add(btnClear);
        pnlSouth.add(new JSeparator(JSeparator.VERTICAL));
        pnlSouth.add(btnRead);
        pnlSouth.add(btnExportSingle);
        pnlSouth.add(btnExportAll);

        add(pnlSouth, BorderLayout.SOUTH);
    }

    private void setupButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    // --- Các hàm xử lý chức năng & sự kiện ---

    private void loadAllBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getReleaseDate()});
        }
    }

    private void fillFormFromSelectedRow() {
        int selectedRow = tblBooks.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tblBooks.getValueAt(selectedRow, 0);
            Book book = bookDAO.getBookById(id);
            if (book != null) {
                txtId.setText(String.valueOf(book.getId()));
                txtTitle.setText(book.getTitle());
                txtAuthor.setText(book.getAuthor());
                txtReleaseDate.setText(book.getReleaseDate());
                txtContent.setText(book.getContent());
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtReleaseDate.setText("");
        txtContent.setText("");
        tblBooks.clearSelection();
    }

    private void performAdd() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String releaseDate = txtReleaseDate.getText().trim();
        String content = txtContent.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiêu đề sách không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = new Book(title, author, releaseDate, content);
        if (bookDAO.addBook(book)) {
            JOptionPane.showMessageDialog(this, "Thêm sách mới thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadAllBooks();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm sách thất bại. Vui lòng kiểm tra lại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performUpdate() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ bảng để cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String releaseDate = txtReleaseDate.getText().trim();
        String content = txtContent.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiêu đề sách không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = new Book(id, title, author, releaseDate, content);
        if (bookDAO.updateBook(book)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadAllBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật sách thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRemove() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ bảng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        String title = txtTitle.getText();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa cuốn sách '" + title + "' không?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(id)) {
                JOptionPane.showMessageDialog(this, "Xóa sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadAllBooks();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sách thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllBooks();
            return;
        }

        tableModel.setRowCount(0);
        if (cbSearchType.getSelectedIndex() == 0) {
            // Tìm kiếm theo tiêu đề sách
            List<Book> books = bookDAO.searchBooksByName(keyword);
            for (Book b : books) {
                tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getReleaseDate()});
            }
        } else {
            // Tìm kiếm chính xác theo ID sách
            try {
                int id = Integer.parseInt(keyword);
                Book b = bookDAO.getBookById(id);
                if (b != null) {
                    tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getReleaseDate()});
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã ID để tìm kiếm phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openBookReader() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ bảng để đọc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            AutoScrollReader reader = new AutoScrollReader(this, book);
            reader.setVisible(true);
        }
    }

    // Xuất nội dung cuốn sách đang chọn ra file text riêng
    private void exportSelectedBook() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ danh sách để xuất file!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            // Xóa bỏ các ký tự đặc biệt không được đặt tên file trong Windows
            String fileName = book.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_") + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("==================================================");
                writer.println("MÃ SÁCH (ID): " + book.getId());
                writer.println("TIÊU ĐỀ: " + book.getTitle());
                writer.println("TÁC GIẢ: " + book.getAuthor());
                writer.println("NGÀY PHÁT HÀNH: " + book.getReleaseDate());
                writer.println("==================================================");
                writer.println();
                writer.println("NỘI DUNG SÁCH:");
                writer.println("--------------------------------------------------");
                writer.println(book.getContent());
                writer.println("--------------------------------------------------");
                
                JOptionPane.showMessageDialog(this, "Xuất file sách '" + fileName + "' thành công!", "Xuất file thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi ghi file text: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Xuất toàn bộ danh mục sách ra file báo cáo và tạo các file text riêng cho từng cuốn sách
    private void exportAllBooks() {
        List<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có sách nào trong cơ sở dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 1. Tạo file txt tổng hợp toàn bộ danh mục sách
        String summaryFile = "all_books_summary.txt";
        try (PrintWriter summaryWriter = new PrintWriter(new FileWriter(summaryFile))) {
            summaryWriter.println("==================================================");
            summaryWriter.println("        DANH MỤC TOÀN BỘ SÁCH TRONG THƯ VIỆN      ");
            summaryWriter.println("==================================================");
            summaryWriter.printf("%-5s | %-25s | %-20s | %-12s%n", "ID", "Tiêu Đề", "Tác Giả", "Ngày XB");
            summaryWriter.println("--------------------------------------------------------------------------------");
            for (Book b : books) {
                summaryWriter.printf("%-5d | %-25s | %-20s | %-12s%n",
                        b.getId(),
                        limitString(b.getTitle(), 25),
                        limitString(b.getAuthor(), 20),
                        b.getReleaseDate());
            }
            summaryWriter.println("--------------------------------------------------------------------------------");
            summaryWriter.println("Tổng số sách: " + books.size());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Gặp lỗi khi tạo file tổng hợp: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Xuất nội dung từng cuốn sách thành các file riêng trong thư mục exported_books
        File dir = new File("exported_books");
        if (!dir.exists()) {
            dir.mkdir();
        }

        int successCount = 0;
        for (Book book : books) {
            String sanitizedTitle = book.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_");
            String fileName = "exported_books/" + book.getId() + "_" + sanitizedTitle + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("==================================================");
                writer.println("MÃ SÁCH (ID): " + book.getId());
                writer.println("TIÊU ĐỀ: " + book.getTitle());
                writer.println("TÁC GIẢ: " + book.getAuthor());
                writer.println("NGÀY PHÁT HÀNH: " + book.getReleaseDate());
                writer.println("==================================================");
                writer.println();
                writer.println("NỘI DUNG SÁCH:");
                writer.println("--------------------------------------------------");
                writer.println(book.getContent());
                writer.println("--------------------------------------------------");
                successCount++;
            } catch (IOException e) {
                System.err.println("Lỗi khi xuất cuốn sách ID " + book.getId() + ": " + e.getMessage());
            }
        }

        JOptionPane.showMessageDialog(this, 
                "Xuất file thành công!\n" +
                "- Đã tạo danh mục tổng hợp: '" + summaryFile + "'\n" +
                "- Đã xuất hàng loạt " + successCount + "/" + books.size() + " sách vào thư mục '/exported_books/'",
                "Hoàn thành xuất danh mục", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    private String limitString(String text, int limit) {
        if (text == null) return "";
        if (text.length() <= limit) return text;
        return text.substring(0, limit - 3) + "...";
    }

    // --- Hàm main khởi chạy chương trình ---
    public static void main(String[] args) {
        // Chạy giao diện trên luồng EDT để đảm bảo an toàn trong Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Áp dụng giao diện cửa sổ hệ thống để ứng dụng đẹp mắt hơn
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // Nếu lỗi thì tự động dùng giao diện mặc định của Java
                }
                
                BookGUI gui = new BookGUI();
                gui.setVisible(true);
            }
        });
    }
}
