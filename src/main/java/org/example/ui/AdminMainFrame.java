package org.example.ui;

import org.example.entity.User;
import org.example.entity.Dish;
import org.example.entity.Category;
import org.example.entity.Table;
import org.example.entity.Order;
import org.example.entity.OrderItem;
import org.example.repository.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class AdminMainFrame extends JFrame {
    private final User currentUser;
    private final UserRepository userRepository;
    private final DishRepository dishRepository;
    private final CategoryRepository categoryRepository;
    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;
    
    private JTabbedPane tabbedPane;
    private JTable userTable;
    private JTable dishTable;
    private JTable categoryTable;
    private JTable tableTable;
    private JTable orderTable;
    private DefaultTableModel userTableModel;
    private DefaultTableModel dishTableModel;
    private DefaultTableModel categoryTableModel;
    private DefaultTableModel tableTableModel;
    private DefaultTableModel orderTableModel;

    public AdminMainFrame(User user) {
        this.currentUser = user;
        this.userRepository = new UserRepository();
        this.dishRepository = new DishRepository();
        this.categoryRepository = new CategoryRepository();
        this.tableRepository = new TableRepository();
        this.orderRepository = new OrderRepository();
        
        initUI();
    }

    private void initUI() {
        setTitle("食堂订餐系统 - 管理员：" + currentUser.getUsername());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu adminMenu = new JMenu("管理员");
        JMenuItem changePasswordItem = new JMenuItem("修改密码");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        
        changePasswordItem.addActionListener(e -> showChangePasswordDialog());
        logoutItem.addActionListener(e -> logout());
        
        adminMenu.add(changePasswordItem);
        adminMenu.add(logoutItem);
        menuBar.add(adminMenu);
        setJMenuBar(menuBar);

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        
        // 用户管理选项卡
        JPanel userPanel = createUserPanel();
        tabbedPane.addTab("用户管理", userPanel);
        
        // 菜品管理选项卡
        JPanel dishPanel = createDishPanel();
        tabbedPane.addTab("菜品管理", dishPanel);
        
        // 菜系管理选项卡
        JPanel categoryPanel = createCategoryPanel();
        tabbedPane.addTab("菜系管理", categoryPanel);
        
        // 桌台管理选项卡
        JPanel tablePanel = createTablePanel();
        tabbedPane.addTab("桌台管理", tablePanel);
        
        // 订单管理选项卡
        JPanel orderPanel = createOrderPanel();
        tabbedPane.addTab("订单管理", orderPanel);
        
        // 结账报表选项卡
        JPanel reportPanel = createReportPanel();
        tabbedPane.addTab("结账报表", reportPanel);
        
        add(tabbedPane);
        
        // 初始加载数据
        refreshUserTable();
        refreshDishTable();
        refreshCategoryTable();
        refreshTableTable();
        refreshOrderTable();
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"用户ID", "用户名", "角色", "创建时间", "操作"};
        userTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // 只有操作列可编辑
            }
        };
        userTable = new JTable(userTableModel);
        userTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        userTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("添加用户");
        addButton.addActionListener(e -> showAddUserDialog());
        buttonPanel.add(addButton);
        
        panel.add(new JScrollPane(userTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createDishPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"菜品ID", "菜品名称", "价格", "描述", "菜系", "操作"};
        dishTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // 只有操作列可编辑
            }
        };
        dishTable = new JTable(dishTableModel);
        dishTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        dishTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("添加菜品");
        addButton.addActionListener(e -> showAddDishDialog());
        buttonPanel.add(addButton);
        
        panel.add(new JScrollPane(dishTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createCategoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"菜系ID", "菜系名称", "操作"};
        categoryTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // 只有操作列可编辑
            }
        };
        categoryTable = new JTable(categoryTableModel);
        categoryTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        categoryTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("添加菜系");
        addButton.addActionListener(e -> showAddCategoryDialog());
        buttonPanel.add(addButton);
        
        panel.add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"桌台ID", "桌台号", "状态", "操作"};
        tableTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // 只有操作列可编辑
            }
        };
        tableTable = new JTable(tableTableModel);
        tableTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        tableTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // 创建按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("添加桌台");
        addButton.addActionListener(e -> showAddTableDialog());
        buttonPanel.add(addButton);
        
        panel.add(new JScrollPane(tableTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 添加刷新按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshOrderTable());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        // 创建表格
        String[] columnNames = {"订单ID", "用户", "桌台", "下单时间", "总金额", "状态", "操作"};
        orderTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // 只有操作列可编辑
            }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createReportPanel() {
        JTabbedPane reportTabs = new JTabbedPane();
        reportTabs.addTab("日结账", createDailyReportPanel());
        reportTabs.addTab("月结账", createMonthlyReportPanel());
        reportTabs.addTab("年度结账", createYearlyReportPanel());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(reportTabs, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createDailyReportPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dateLabel = new JLabel("选择日期：");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd"));
        JButton queryButton = new JButton("查询");
        JLabel resultLabel = new JLabel("  订单数：0  总金额：￥0.00");
        panel.add(dateLabel);
        panel.add(dateSpinner);
        panel.add(queryButton);
        panel.add(resultLabel);
        queryButton.addActionListener(e -> {
            java.util.Date date = (java.util.Date) dateSpinner.getValue();
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            ReportRepository.ReportResult result = new ReportRepository().getDailyReport(sqlDate);
            resultLabel.setText(String.format("  订单数：%d  总金额：￥%.2f", result.orderCount, result.totalAmount));
        });
        return panel;
    }

    private JPanel createMonthlyReportPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel yearLabel = new JLabel("年份：");
        JLabel monthLabel = new JLabel("月份：");
        SpinnerNumberModel yearModel = new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1);
        SpinnerNumberModel monthModel = new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);
        JSpinner monthSpinner = new JSpinner(monthModel);
        JButton queryButton = new JButton("查询");
        JLabel resultLabel = new JLabel("  订单数：0  总金额：￥0.00");
        panel.add(yearLabel);
        panel.add(yearSpinner);
        panel.add(monthLabel);
        panel.add(monthSpinner);
        panel.add(queryButton);
        panel.add(resultLabel);
        queryButton.addActionListener(e -> {
            int year = (int) yearSpinner.getValue();
            int month = (int) monthSpinner.getValue();
            ReportRepository.ReportResult result = new ReportRepository().getMonthlyReport(year, month);
            resultLabel.setText(String.format("  订单数：%d  总金额：￥%.2f", result.orderCount, result.totalAmount));
        });
        return panel;
    }

    private JPanel createYearlyReportPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel yearLabel = new JLabel("年份：");
        SpinnerNumberModel yearModel = new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1);
        JSpinner yearSpinner = new JSpinner(yearModel);
        JButton queryButton = new JButton("查询");
        JLabel resultLabel = new JLabel("  订单数：0  总金额：￥0.00");
        panel.add(yearLabel);
        panel.add(yearSpinner);
        panel.add(queryButton);
        panel.add(resultLabel);
        queryButton.addActionListener(e -> {
            int year = (int) yearSpinner.getValue();
            ReportRepository.ReportResult result = new ReportRepository().getYearlyReport(year);
            resultLabel.setText(String.format("  订单数：%d  总金额：￥%.2f", result.orderCount, result.totalAmount));
        });
        return panel;
    }

    private void refreshUserTable() {
        userTableModel.setRowCount(0);
        List<User> users = userRepository.getAllUsers();
        for (User user : users) {
            userTableModel.addRow(new Object[]{
                user.getUserId(),
                user.getUsername(),
                user.getRole(),
                user.getCreatedAt(),
                "编辑"
            });
        }
    }

    private void refreshDishTable() {
        dishTableModel.setRowCount(0);
        List<Dish> dishes = dishRepository.getAllDishes();
        for (Dish dish : dishes) {
            dishTableModel.addRow(new Object[]{
                dish.getDishId(),
                dish.getDishName(),
                dish.getPrice(),
                dish.getDescription(),
                dish.getCategoryName(),
                "编辑"
            });
        }
    }

    private void refreshCategoryTable() {
        categoryTableModel.setRowCount(0);
        List<Category> categories = categoryRepository.getAllCategories();
        for (Category category : categories) {
            categoryTableModel.addRow(new Object[]{
                category.getCategoryId(),
                category.getCategoryName(),
                "编辑"
            });
        }
    }

    private void refreshTableTable() {
        tableTableModel.setRowCount(0);
        List<Table> tables = tableRepository.getAllTables();
        for (Table table : tables) {
            tableTableModel.addRow(new Object[]{
                table.getTableId(),
                table.getTableNumber(),
                table.getStatus(),
                "编辑"
            });
        }
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderRepository.getAllOrders();
        for (Order order : orders) {
            orderTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getUsername(),
                order.getTableNumber(),
                order.getOrderTime(),
                order.getTotalAmount(),
                order.getStatus(),
                "查看详情"
            });
        }
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "修改密码", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JPasswordField oldPasswordField = new JPasswordField(20);
        JPasswordField newPasswordField = new JPasswordField(20);
        JPasswordField confirmPasswordField = new JPasswordField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("原密码："), gbc);
        gbc.gridx = 1;
        panel.add(oldPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("新密码："), gbc);
        gbc.gridx = 1;
        panel.add(newPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("确认密码："), gbc);
        gbc.gridx = 1;
        panel.add(confirmPasswordField, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "所有字段都不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的新密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 验证原密码
            User user = userRepository.authenticate(currentUser.getUsername(), oldPassword);
            if (user == null) {
                JOptionPane.showMessageDialog(dialog, "原密码错误！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 修改密码
            if (userRepository.changePassword(currentUser.getUserId(), newPassword)) {
                JOptionPane.showMessageDialog(dialog, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "密码修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog(this, "添加用户", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"user", "admin"});

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码："), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("角色："), gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名和密码不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);

            if (userRepository.addUser(user)) {
                JOptionPane.showMessageDialog(dialog, "添加用户成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加用户失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddDishDialog() {
        JDialog dialog = new JDialog(this, "添加菜品", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField descriptionField = new JTextField(20);
        JComboBox<Category> categoryComboBox = new JComboBox<>(
            categoryRepository.getAllCategories().toArray(new Category[0]));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("菜品名称："), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("价格："), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("描述："), gbc);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("菜系："), gbc);
        gbc.gridx = 1;
        panel.add(categoryComboBox, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String name = nameField.getText();
            String priceStr = priceField.getText();
            String description = descriptionField.getText();
            Category category = (Category) categoryComboBox.getSelectedItem();

            if (name.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "菜品名称和价格不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal price = new BigDecimal(priceStr);
                Dish dish = new Dish();
                dish.setDishName(name);
                dish.setPrice(price);
                dish.setDescription(description);
                dish.setCategoryId(category.getCategoryId());

                if (dishRepository.addDish(dish)) {
                    JOptionPane.showMessageDialog(dialog, "添加菜品成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshDishTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "添加菜品失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的价格！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddCategoryDialog() {
        JDialog dialog = new JDialog(this, "添加菜系", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("菜系名称："), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String name = nameField.getText();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "菜系名称不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Category category = new Category();
            category.setCategoryName(name);

            if (categoryRepository.addCategory(category)) {
                JOptionPane.showMessageDialog(dialog, "添加菜系成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshCategoryTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加菜系失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showAddTableDialog() {
        JDialog dialog = new JDialog(this, "添加桌台", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField numberField = new JTextField(20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("桌台号："), gbc);
        gbc.gridx = 1;
        panel.add(numberField, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String number = numberField.getText();

            if (number.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "桌台号不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Table table = new Table();
            table.setTableNumber(number);
            table.setStatus("空闲");

            if (tableRepository.addTable(table)) {
                JOptionPane.showMessageDialog(dialog, "添加桌台成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshTableTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "添加桌台失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "确定要退出登录吗？", "确认", 
            JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }

    // 按钮渲染器
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            return this;
        }
    }

    // 按钮编辑器
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // 处理按钮点击事件
                if (label.equals("编辑")) {
                    // 根据当前选项卡处理不同的编辑操作
                    int selectedTab = tabbedPane.getSelectedIndex();
                    switch (selectedTab) {
                        case 0: // 用户管理
                            int userId = (int) userTable.getValueAt(userTable.getSelectedRow(), 0);
                            showEditUserDialog(userId);
                            break;
                        case 1: // 菜品管理
                            int dishId = (int) dishTable.getValueAt(dishTable.getSelectedRow(), 0);
                            showEditDishDialog(dishId);
                            break;
                        case 2: // 菜系管理
                            int categoryId = (int) categoryTable.getValueAt(categoryTable.getSelectedRow(), 0);
                            showEditCategoryDialog(categoryId);
                            break;
                        case 3: // 桌台管理
                            int tableId = (int) tableTable.getValueAt(tableTable.getSelectedRow(), 0);
                            showEditTableDialog(tableId);
                            break;
                    }
                } else if (label.equals("查看详情")) {
                    int orderId = (int) orderTable.getValueAt(orderTable.getSelectedRow(), 0);
                    Order order = orderRepository.getOrderById(orderId);
                    if (order != null) {
                        showOrderDetails(order);
                    }
                }
            }
            isPushed = false;
            return label;
        }
    }

    private void showEditUserDialog(int userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) return;

        JDialog dialog = new JDialog(this, "编辑用户", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField usernameField = new JTextField(user.getUsername(), 20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"user", "admin"});
        roleComboBox.setSelectedItem(user.getRole());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("用户名："), gbc);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("密码："), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("角色："), gbc);
        gbc.gridx = 1;
        panel.add(roleComboBox, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "用户名不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            user.setUsername(username);
            if (!password.isEmpty()) {
                user.setPassword(password);
            }
            user.setRole(role);

            if (userRepository.updateUser(user)) {
                JOptionPane.showMessageDialog(dialog, "更新用户成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "更新用户失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditDishDialog(int dishId) {
        Dish dish = dishRepository.getDishById(dishId);
        if (dish == null) return;

        JDialog dialog = new JDialog(this, "编辑菜品", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(dish.getDishName(), 20);
        JTextField priceField = new JTextField(dish.getPrice().toString(), 20);
        JTextField descriptionField = new JTextField(dish.getDescription(), 20);
        JComboBox<Category> categoryComboBox = new JComboBox<>(
            categoryRepository.getAllCategories().toArray(new Category[0]));
        categoryComboBox.setSelectedItem(new Category(dish.getCategoryId(), dish.getCategoryName()));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("菜品名称："), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("价格："), gbc);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("描述："), gbc);
        gbc.gridx = 1;
        panel.add(descriptionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("菜系："), gbc);
        gbc.gridx = 1;
        panel.add(categoryComboBox, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String name = nameField.getText();
            String priceStr = priceField.getText();
            String description = descriptionField.getText();
            Category category = (Category) categoryComboBox.getSelectedItem();

            if (name.isEmpty() || priceStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "菜品名称和价格不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                BigDecimal price = new BigDecimal(priceStr);
                dish.setDishName(name);
                dish.setPrice(price);
                dish.setDescription(description);
                dish.setCategoryId(category.getCategoryId());

                if (dishRepository.updateDish(dish)) {
                    JOptionPane.showMessageDialog(dialog, "更新菜品成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshDishTable();
                } else {
                    JOptionPane.showMessageDialog(dialog, "更新菜品失败！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "请输入有效的价格！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditCategoryDialog(int categoryId) {
        Category category = categoryRepository.getCategoryById(categoryId);
        if (category == null) return;

        JDialog dialog = new JDialog(this, "编辑菜系", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField nameField = new JTextField(category.getCategoryName(), 20);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("菜系名称："), gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String name = nameField.getText();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "菜系名称不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            category.setCategoryName(name);

            if (categoryRepository.updateCategory(category)) {
                JOptionPane.showMessageDialog(dialog, "更新菜系成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshCategoryTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "更新菜系失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showEditTableDialog(int tableId) {
        Table table = tableRepository.getTableById(tableId);
        if (table == null) return;

        JDialog dialog = new JDialog(this, "编辑桌台", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField numberField = new JTextField(table.getTableNumber(), 20);
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"空闲", "已占用"});
        statusComboBox.setSelectedItem(table.getStatus());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("桌台号："), gbc);
        gbc.gridx = 1;
        panel.add(numberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("状态："), gbc);
        gbc.gridx = 1;
        panel.add(statusComboBox, gbc);

        JButton confirmButton = new JButton("确认");
        confirmButton.addActionListener(e -> {
            String number = numberField.getText();
            String status = (String) statusComboBox.getSelectedItem();

            if (number.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "桌台号不能为空！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            table.setTableNumber(number);
            table.setStatus(status);

            if (tableRepository.updateTable(table)) {
                JOptionPane.showMessageDialog(dialog, "更新桌台成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshTableTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "更新桌台失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(confirmButton, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void showOrderDetails(Order order) {
        JDialog dialog = new JDialog(this, "订单详情", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"菜品", "数量", "单价", "小计"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        
        // 添加订单项
        for (OrderItem item : order.getItems()) {
            model.addRow(new Object[]{
                item.getDishName(),
                item.getQuantity(),
                item.getPrice(),
                item.getSubtotal()
            });
        }
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // 添加订单信息
        JPanel infoPanel = new JPanel(new GridLayout(5, 1));
        infoPanel.add(new JLabel("订单号：" + order.getOrderId()));
        infoPanel.add(new JLabel("用户：" + order.getUsername()));
        infoPanel.add(new JLabel("桌台：" + order.getTableNumber()));
        infoPanel.add(new JLabel("下单时间：" + order.getOrderTime()));
        infoPanel.add(new JLabel("总金额：￥" + order.getTotalAmount()));
        
        // 添加状态更新
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("状态："));
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"待处理", "已支付", "已完成"});
        statusComboBox.setSelectedItem(order.getStatus());
        statusPanel.add(statusComboBox);
        
        JButton updateButton = new JButton("更新状态");
        updateButton.addActionListener(e -> {
            String newStatus = (String) statusComboBox.getSelectedItem();
            order.setStatus(newStatus);
            if (orderRepository.updateOrderStatus(order.getOrderId(), newStatus)) {
                JOptionPane.showMessageDialog(dialog, "状态更新成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshOrderTable();
            } else {
                JOptionPane.showMessageDialog(dialog, "状态更新失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
        statusPanel.add(updateButton);
        
        infoPanel.add(statusPanel);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
} 