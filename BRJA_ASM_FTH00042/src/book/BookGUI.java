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

// Lớp giao diện người dùng chính của ứng dụng Quản lý Sách được xây dựng trên thư viện Java Swing.
// Lớp này triển khai mô hình quản trị dữ liệu trực quan bao gồm bảng hiển thị danh sách sách,
// form nhập liệu chi tiết, các bộ lọc tìm kiếm và các tính năng nâng cao như xuất báo cáo văn bản, đọc sách cuộn tự động.
public class BookGUI extends JFrame {
    // Đối tượng BookDAO để thực hiện giao tiếp và truy vấn cơ sở dữ liệu SQLite
    private final BookDAO bookDAO = new BookDAO();

    // Các thành phần đồ họa của thư viện Swing
    private JTable tblBooks;
    private DefaultTableModel tableModel;
    
    private JTextField txtId;
    private JTextField txtTitle;
    private JTextField txtAuthor;
    private JTextField txtReleaseDate;
    private JTextArea txtContent;
    
    private JTextField txtSearch;
    private JComboBox<String> cbSearchType;

    // Phương thức khởi tạo thiết lập tiêu đề, xây dựng giao diện và nạp dữ liệu sách ban đầu
    public BookGUI() {
        super("Hệ Thống Quản Lý Sách - Bài Tập Lớn Java");
        initializeUI();
        loadAllBooks();
    }

    // Xây dựng cấu trúc bố cục và trang trí giao diện người dùng
    private void initializeUI() {
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Hiển thị cửa sổ ở chính giữa màn hình máy tính
        setLayout(new BorderLayout(10, 10));

        // Định nghĩa bảng màu thiết kế để giao diện trông hài hòa, dễ nhìn và chuyên nghiệp
        Color primaryColor = new Color(52, 73, 94);
        Color accentColor = new Color(41, 128, 185);
        Color bgLeftPanel = new Color(245, 247, 250);

        // Khung phía trên chứa tiêu đề ứng dụng và thanh công cụ tìm kiếm dữ liệu
        JPanel pnlTop = new JPanel(new BorderLayout(15, 10));
        pnlTop.setBackground(primaryColor);
        pnlTop.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblAppTitle = new JLabel("HỆ THỐNG QUẢN LÝ SÁCH", JLabel.LEFT);
        lblAppTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblAppTitle.setForeground(Color.WHITE);
        pnlTop.add(lblAppTitle, BorderLayout.WEST);

        // Khung tìm kiếm nằm bên phải của Top Panel
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

        // Khung bên trái chứa Form nhập liệu thông tin cuốn sách, sử dụng GridBagLayout để căn lề thẳng hàng
        JPanel pnlLeft = new JPanel(new GridBagLayout());
        pnlLeft.setBackground(bgLeftPanel);
        pnlLeft.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 224, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        pnlLeft.setPreferredSize(new Dimension(360, 500));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 4, 6, 4); // Khoảng cách giữa các thành phần nhập liệu
        gbc.weightx = 1.0;

        // Tiêu đề của form nhập liệu sách
        JLabel lblFormTitle = new JLabel("Thông Tin Chi Tiết");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblFormTitle.setForeground(primaryColor);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        pnlLeft.add(lblFormTitle, gbc);

        gbc.gridwidth = 1;

        // Mã sách (ID) tự tăng trong database, người dùng chỉ đọc chứ không được sửa thủ công
        gbc.gridx = 0; gbc.gridy = 1;
        pnlLeft.add(new JLabel("Mã Sách (ID):"), gbc);
        txtId = new JTextField();
        txtId.setEditable(false);
        txtId.setBackground(new Color(230, 233, 238));
        txtId.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtId, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlLeft.add(new JLabel("Tiêu Đề Sách:"), gbc);
        txtTitle = new JTextField();
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlLeft.add(new JLabel("Tác Giả:"), gbc);
        txtAuthor = new JTextField();
        txtAuthor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtAuthor, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        pnlLeft.add(new JLabel("Ngày Phát Hành:"), gbc);
        txtReleaseDate = new JTextField();
        txtReleaseDate.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx = 1;
        pnlLeft.add(txtReleaseDate, gbc);

        // Vùng nhập nội dung văn bản của sách, hỗ trợ tự động xuống dòng khi chạm viền
        gbc.gridx = 0; gbc.gridy = 5;
        pnlLeft.add(new JLabel("Nội Dung:"), gbc);
        txtContent = new JTextArea(8, 15);
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        JScrollPane scrollContent = new JScrollPane(txtContent);
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        pnlLeft.add(scrollContent, gbc);

        add(pnlLeft, BorderLayout.WEST);

        // Khung ở chính giữa chứa bảng danh sách sách trong thư viện
        JPanel pnlCenter = new JPanel(new BorderLayout(5, 5));
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 20));

        JLabel lblListTitle = new JLabel("Danh Sách Sách Trong Thư Viện");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblListTitle.setForeground(primaryColor);
        pnlCenter.add(lblListTitle, BorderLayout.NORTH);

        // Cấu hình bảng hiển thị thông tin sách
        String[] columnNames = {"Mã Sách", "Tiêu Đề Sách", "Tác Giả", "Ngày Phát Hành"};
        // Ghi đè phương thức isCellEditable để ngăn không cho người dùng nháy đúp sửa trực tiếp trên bảng
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblBooks = new JTable(tableModel);
        tblBooks.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblBooks.setRowHeight(24);
        tblBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblBooks.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblBooks.getTableHeader().setBackground(new Color(230, 233, 238));

        // Lắng nghe sự kiện chuột click trên dòng của bảng để tự động điền dữ liệu ngược lên form nhập liệu
        tblBooks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillFormFromSelectedRow();
            }
        });

        JScrollPane scrollTable = new JScrollPane(tblBooks);
        pnlCenter.add(scrollTable, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);

        // Khung phía dưới chứa các nút chức năng chính (Thêm, Sửa, Xóa, Làm sạch, Đọc sách, Xuất file)
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        pnlSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));
        pnlSouth.setBackground(bgLeftPanel);

        JButton btnAdd = new JButton("Thêm Mới");
        setupButton(btnAdd, new Color(39, 174, 96));
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAdd();
            }
        });

        JButton btnUpdate = new JButton("Cập Nhật");
        setupButton(btnUpdate, new Color(241, 196, 15));
        btnUpdate.setForeground(Color.BLACK); // Đặt màu chữ tối trên nền nút màu sáng giúp dễ đọc hơn
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performUpdate();
            }
        });

        JButton btnRemove = new JButton("Xóa Sách");
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

        JButton btnRead = new JButton("Đọc Sách");
        setupButton(btnRead, new Color(155, 89, 182));
        btnRead.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openBookReader();
            }
        });

        JButton btnExportSingle = new JButton("Xuất Cuốn Đang Chọn");
        setupButton(btnExportSingle, accentColor);
        btnExportSingle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportSelectedBook();
            }
        });

        JButton btnExportAll = new JButton("Xuất Toàn Bộ Thư Viện");
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

    // Tiện ích định dạng nhanh giao diện cho nút bấm để đồng nhất thẩm mỹ thiết kế
    private void setupButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
    }

    // Nạp lại toàn bộ danh sách sách từ cơ sở dữ liệu lên bảng giao diện Swing
    private void loadAllBooks() {
        tableModel.setRowCount(0);
        List<Book> books = bookDAO.getAllBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getReleaseDate()});
        }
    }

    // Lấy thông tin sách của dòng được chọn từ bảng để điền chi tiết vào form nhập liệu bên trái
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

    // Làm rỗng toàn bộ form nhập liệu bên trái và xóa lựa chọn trên bảng
    private void clearForm() {
        txtId.setText("");
        txtTitle.setText("");
        txtAuthor.setText("");
        txtReleaseDate.setText("");
        txtContent.setText("");
        tblBooks.clearSelection();
    }

    // Thực hiện chèn thêm một cuốn sách mới vào database sau khi đã kiểm tra tính hợp lệ dữ liệu
    private void performAdd() {
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String releaseDate = txtReleaseDate.getText().trim();
        String content = txtContent.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiêu đề cuốn sách bắt buộc phải nhập và không được để trống!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = new Book(title, author, releaseDate, content);
        if (bookDAO.addBook(book)) {
            JOptionPane.showMessageDialog(this, "Đã thêm mới cuốn sách thành công vào cơ sở dữ liệu!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadAllBooks();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thao tác thêm mới thất bại. Vui lòng kiểm tra lại kết nối cơ sở dữ liệu!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thực hiện cập nhật thông tin cuốn sách đã chọn dựa trên mã ID tương ứng
    private void performUpdate() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng chứa cuốn sách từ bảng dữ liệu để thực hiện cập nhật!", "Lỗi cập nhật", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        String title = txtTitle.getText().trim();
        String author = txtAuthor.getText().trim();
        String releaseDate = txtReleaseDate.getText().trim();
        String content = txtContent.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tiêu đề sách không được để trống khi sửa thông tin!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book book = new Book(id, title, author, releaseDate, content);
        if (bookDAO.updateBook(book)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin chi tiết của cuốn sách thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            loadAllBooks();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thất bại. Vui lòng thử lại sau!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Thực hiện xóa cuốn sách được chọn sau khi người dùng đã xác nhận qua hộp thoại an toàn
    private void performRemove() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách trên danh sách hiển thị trước khi xóa!", "Lỗi thao tác", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        String title = txtTitle.getText();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa vĩnh viễn cuốn sách '" + title + "' khỏi hệ thống không?",
                "Xác nhận yêu cầu xóa",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(id)) {
                JOptionPane.showMessageDialog(this, "Đã xóa thành công cuốn sách khỏi cơ sở dữ liệu SQLite!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadAllBooks();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thực hiện xóa cuốn sách này. Vui lòng kiểm tra lại!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Thực hiện chức năng tìm kiếm sách theo lựa chọn loại tìm kiếm trong ComboBox
    private void performSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadAllBooks();
            return;
        }

        tableModel.setRowCount(0);
        if (cbSearchType.getSelectedIndex() == 0) {
            // Thực hiện tìm kiếm gần đúng theo tiêu đề sách trong CSDL
            List<Book> books = bookDAO.searchBooksByName(keyword);
            for (Book b : books) {
                tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getReleaseDate()});
            }
        } else {
            // Tìm kiếm chính xác tuyệt đối theo mã ID số nguyên
            try {
                int id = Integer.parseInt(keyword);
                Book b = bookDAO.getBookById(id);
                if (b != null) {
                    tableModel.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getReleaseDate()});
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Mã ID dùng để tìm kiếm bắt buộc phải là ký tự số nguyên hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Mở giao diện con hỗ trợ đọc sách và tự động cuộn dọc
    private void openBookReader() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ bảng danh mục để bắt đầu đọc!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            AutoScrollReader reader = new AutoScrollReader(this, book);
            reader.setVisible(true);
        }
    }

    // Xuất nội dung chi tiết của cuốn sách đang được chọn ra một tệp tin văn bản .txt riêng biệt
    private void exportSelectedBook() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một cuốn sách từ danh mục trước khi xuất file văn bản!", "Thao tác lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idText);
        Book book = bookDAO.getBookById(id);
        if (book != null) {
            // Áp dụng biểu thức chính quy (Regex) loại bỏ các ký tự đặc biệt bị cấm đặt tên file trên Windows
            String fileName = book.getTitle().replaceAll("[\\\\/:*?\"<>|]", "_") + ".txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("==================================================");
                writer.println("MÃ SÁCH (ID): " + book.getId());
                writer.println("TIÊU ĐỀ: " + book.getTitle());
                writer.println("TÁC GIẢ: " + book.getAuthor());
                writer.println("NGÀY PHÁT HÀNH: " + book.getReleaseDate());
                writer.println("==================================================");
                writer.println();
                writer.println("NỘI DUNG CHI TIẾT:");
                writer.println("--------------------------------------------------");
                writer.println(book.getContent());
                writer.println("--------------------------------------------------");
                
                JOptionPane.showMessageDialog(this, "Đã xuất file văn bản '" + fileName + "' thành công!", "Xuất file thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Xảy ra lỗi trong quá trình ghi dữ liệu ra tệp tin: " + e.getMessage(), "Lỗi ghi file", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Tạo tệp tin danh mục tổng hợp toàn bộ sách và đồng thời xuất hàng loạt từng cuốn sách ra tệp tin riêng biệt
    private void exportAllBooks() {
        List<Book> books = bookDAO.getAllBooks();
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy dữ liệu sách nào trong hệ thống để thực hiện xuất file!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Tạo tệp tin danh mục toàn bộ sách theo cấu trúc định dạng cột
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
            JOptionPane.showMessageDialog(this, "Gặp lỗi khi tạo file tổng hợp danh mục sách: " + e.getMessage(), "Lỗi ghi file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo thư mục chứa các tệp sách được xuất hàng loạt
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
                System.err.println("Gặp sự cố khi xuất tệp của cuốn sách mã ID " + book.getId() + ": " + e.getMessage());
            }
        }

        JOptionPane.showMessageDialog(this, 
                "Quá trình xuất tệp tin thành công!\n" +
                "- Đã khởi tạo danh mục tổng hợp: '" + summaryFile + "'\n" +
                "- Đã thực hiện xuất hàng loạt " + successCount + "/" + books.size() + " cuốn sách vào thư mục '/exported_books/'",
                "Hoàn thành xuất danh mục", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Hạn chế số lượng ký tự hiển thị để phục vụ in căn lề cột gọn gàng và thẩm mỹ
    private String limitString(String text, int limit) {
        if (text == null) return "";
        if (text.length() <= limit) return text;
        return text.substring(0, limit - 3) + "...";
    }

    // Phương thức main khởi chạy luồng chính của ứng dụng
    public static void main(String[] args) {
        // Đảm bảo việc khởi tạo và cập nhật giao diện Swing chạy an toàn trên luồng phân phối sự kiện EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Cấu hình giao diện Look and Feel đồng bộ theo hệ điều hành Windows để mang lại trải nghiệm tốt nhất
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    // Bỏ qua nếu có lỗi phát sinh và sử dụng giao diện mặc định của nền tảng Java
                }
                
                BookGUI gui = new BookGUI();
                gui.setVisible(true);
            }
        });
    }
}
