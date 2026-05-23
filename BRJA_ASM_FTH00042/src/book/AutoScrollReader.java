package book;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Cửa sổ hội thoại đọc sách được thiết kế chế độ tối để chống mỏi mắt.
// Lớp này tích hợp tính năng tự động cuộn nội dung văn bản thông qua việc sử dụng
// luồng lập lịch sự kiện Timer kết hợp với thanh trượt JSlider để điều chỉnh tốc độ cuộn tùy ý.
public class AutoScrollReader extends JDialog {
    private JTextArea txtContent;
    private JScrollPane scrollPane;
    private JButton btnStartStop;
    private JSlider sldSpeed;
    private Timer scrollTimer;
    private boolean isScrolling = false;

    // Khoảng thời gian độ trễ của Timer, Min và Max để tính toán tốc độ cuộn
    private static final int MIN_SPEED_MS = 100;
    private static final int MAX_SPEED_MS = 10;

    // Phương thức khởi tạo thiết lập giao diện chính và nạp thông tin cuốn sách
    public AutoScrollReader(Frame parent, Book book) {
        super(parent, "Đọc sách: " + book.getTitle(), true);
        initializeUI(book);
        setupTimer();
    }

    // Thiết lập toàn bộ các thành phần giao diện người dùng Swing (UI)
    private void initializeUI(Book book) {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Khung tiêu đề sách hiển thị tên tác phẩm, tác giả và ngày xuất bản
        JPanel pnlHeader = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        pnlHeader.setBackground(new Color(40, 44, 52));

        JLabel lblTitle = new JLabel(book.getTitle(), JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(97, 175, 239));

        JLabel lblAuthor = new JLabel("Tác giả: " + book.getAuthor() + " | Ngày xuất bản: " + book.getReleaseDate(), JLabel.CENTER);
        lblAuthor.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblAuthor.setForeground(new Color(171, 178, 191));

        pnlHeader.add(lblTitle);
        pnlHeader.add(lblAuthor);
        add(pnlHeader, BorderLayout.NORTH);

        // Vùng hiển thị nội dung chi tiết của cuốn sách, sử dụng phông chữ Georgia dễ đọc
        txtContent = new JTextArea();
        txtContent.setText(book.getContent());
        txtContent.setEditable(false);
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        txtContent.setFont(new Font("Georgia", Font.PLAIN, 15));
        txtContent.setBackground(new Color(33, 37, 43));
        txtContent.setForeground(new Color(220, 223, 228));
        txtContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Bao quanh JTextArea bằng JScrollPane để kích hoạt tính năng cuộn dọc khi nội dung vượt quá kích thước cửa sổ
        scrollPane = new JScrollPane(txtContent);
        scrollPane.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(27, 29, 35)));
        add(scrollPane, BorderLayout.CENTER);

        // Khung điều khiển phía dưới chứa nút Bắt đầu/Dừng và thanh trượt thay đổi tốc độ cuộn
        JPanel pnlControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        pnlControls.setBackground(new Color(40, 44, 52));

        btnStartStop = new JButton("Tự Động Cuộn (Start)");
        btnStartStop.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnStartStop.setBackground(new Color(97, 175, 239));
        btnStartStop.setForeground(Color.WHITE);
        btnStartStop.setFocusPainted(false);
        btnStartStop.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnStartStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleScroll();
            }
        });

        JLabel lblSpeed = new JLabel("Tốc độ cuộn:");
        lblSpeed.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSpeed.setForeground(Color.WHITE);

        // Thanh trượt cho phép người dùng cấu hình tốc độ cuộn từ mức 1 đến 10
        sldSpeed = new JSlider(1, 10, 2);
        sldSpeed.setBackground(new Color(40, 44, 52));
        sldSpeed.setPreferredSize(new Dimension(150, 20));

        pnlControls.add(btnStartStop);
        pnlControls.add(lblSpeed);
        pnlControls.add(sldSpeed);
        add(pnlControls, BorderLayout.SOUTH);
    }

    // Thiết lập luồng Timer thực hiện cuộn trang theo chu kỳ cố định
    private void setupTimer() {
        // Cấu hình Swing Timer chạy định kỳ mỗi 50 miligiây để cập nhật vị trí thanh cuộn dọc
        scrollTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
                int currentVal = verticalBar.getValue();
                int maxVal = verticalBar.getMaximum() - verticalBar.getModel().getExtent();

                // Nếu vị trí cuộn hiện tại đã chạm tới đáy trang sách, hệ thống dừng cuộn và thông báo
                if (currentVal >= maxVal) {
                    stopScroll();
                    JOptionPane.showMessageDialog(AutoScrollReader.this, "Đã đọc xong cuốn sách này!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Lấy giá trị tốc độ tương ứng từ thanh trượt làm bước cuộn để tịnh tiến thanh cuộn
                    int scrollStep = sldSpeed.getValue();
                    verticalBar.setValue(currentVal + scrollStep);
                }
            }
        });
    }

    // Chuyển đổi qua lại giữa trạng thái cuộn tự động và dừng lại
    private void toggleScroll() {
        if (isScrolling) {
            stopScroll();
        } else {
            startScroll();
        }
    }

    // Bắt đầu kích hoạt Timer chạy và thay đổi giao diện nút bấm tương ứng
    private void startScroll() {
        scrollTimer.start();
        isScrolling = true;
        btnStartStop.setText("Dừng Cuộn (Stop)");
        btnStartStop.setBackground(new Color(224, 108, 117));
    }

    // Dừng luồng Timer chạy và đặt lại giao diện nút bấm về trạng thái ban đầu
    private void stopScroll() {
        scrollTimer.stop();
        isScrolling = false;
        btnStartStop.setText("Tự Động Cuộn (Start)");
        btnStartStop.setBackground(new Color(97, 175, 239));
    }

    // Giải phóng tài nguyên cửa sổ và bảo đảm dừng hẳn Timer để ngăn rò rỉ bộ nhớ luồng chạy ngầm
    @Override
    public void dispose() {
        if (scrollTimer != null) {
            scrollTimer.stop();
        }
        super.dispose();
    }
}
