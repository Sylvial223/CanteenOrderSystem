package org.example.ui;

import org.example.entity.User;
import org.example.entity.Dish;
import org.example.entity.Order;
import org.example.entity.OrderItem;
import org.example.entity.Table;
import org.example.repository.DishRepository;
import org.example.repository.OrderRepository;
import org.example.repository.UserRepository;
import org.example.repository.TableRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserMainFrame extends JFrame {
    private final User currentUser;
    private final DishRepository dishRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    
    private JTabbedPane tabbedPane;
    private JTable dishTable;
    private JTable orderTable;
    private DefaultTableModel dishTableModel;
    private DefaultTableModel orderTableModel;
    private List<OrderItem> currentOrderItems;
    private JLabel totalLabel;

    public UserMainFrame(User user) {
        this.currentUser = user;
        this.dishRepository = new DishRepository();
        this.orderRepository = new OrderRepository();
        this.userRepository = new UserRepository();
        this.currentOrderItems = new ArrayList<>();
        
        initUI();
    }

    private void initUI() {
        setTitle("食堂订餐系统 - 用户：" + currentUser.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        JMenu userMenu = new JMenu("用户");
        JMenuItem changePasswordItem = new JMenuItem("修改密码");
        JMenuItem logoutItem = new JMenuItem("退出登录");
        
        changePasswordItem.addActionListener(e -> showChangePasswordDialog());
        logoutItem.addActionListener(e -> logout());
        
        userMenu.add(changePasswordItem);
        userMenu.add(logoutItem);
        menuBar.add(userMenu);
        setJMenuBar(menuBar);

        // 创建选项卡面板
        tabbedPane = new JTabbedPane();
        
        // 菜品浏览选项卡
        JPanel dishPanel = createDishPanel();
        tabbedPane.addTab("浏览菜品", dishPanel);
        
        // 我的订单选项卡
        JPanel orderPanel = createOrderPanel();
        tabbedPane.addTab("我的订单", orderPanel);
        
        add(tabbedPane);
        
        // 初始加载数据
        refreshDishTable();
        refreshOrderTable();
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
        
        // 创建订单面板
        JPanel orderPanel = new JPanel(new BorderLayout());
        totalLabel = new JLabel("总金额：￥0.00");
        JButton placeOrderButton = new JButton("提交订单");
        placeOrderButton.addActionListener(e -> placeOrder());
        
        // 新增返回登录按钮
        JButton backToLoginButton = new JButton("返回登录");
        backToLoginButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            UserMainFrame.this.dispose();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(backToLoginButton);
        buttonPanel.add(placeOrderButton);
        
        orderPanel.add(totalLabel, BorderLayout.CENTER);
        orderPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(new JScrollPane(dishTable), BorderLayout.CENTER);
        panel.add(orderPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // 创建表格
        String[] columnNames = {"订单ID", "下单时间", "总金额", "状态", "操作"};
        orderTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // 只有操作列可编辑
            }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // 添加刷新按钮
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshOrderTable());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(refreshButton);
        panel.add(topPanel, BorderLayout.NORTH);
        
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        
        return panel;
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
                "加入订单"
            });
        }
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderRepository.getUserOrders(currentUser.getUserId());
        for (Order order : orders) {
            // 操作列内容根据状态变化
            String action = "查看详情";
            if ("待处理".equals(order.getStatus())) {
                action = "结账";
            }
            orderTableModel.addRow(new Object[]{
                order.getOrderId(),
                order.getOrderTime(),
                order.getTotalAmount(),
                order.getStatus(),
                action
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

    private void placeOrder() {
        if (currentOrderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择菜品！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 获取所有空闲桌台
        List<Table> tables = new TableRepository().getAllTables();
        List<Table> freeTables = tables.stream()
            .filter(t -> "空闲".equals(t.getStatus()))
            .collect(Collectors.toList());
        if (freeTables.isEmpty()) {
            JOptionPane.showMessageDialog(this, "没有空闲桌台，无法下单！", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // 弹出选择框
        Table selectedTable = (Table) JOptionPane.showInputDialog(
            this, "请选择桌台：", "选择桌台",
            JOptionPane.PLAIN_MESSAGE, null,
            freeTables.toArray(), freeTables.get(0)
        );
        if (selectedTable == null) {
            return; // 用户取消选择
        }

        // 计算总金额
        BigDecimal totalAmount = currentOrderItems.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 创建订单
        Order order = new Order();
        order.setUserId(currentUser.getUserId());
        order.setTableId(selectedTable.getTableId());
        order.setTotalAmount(totalAmount);
        order.setStatus("待处理");
        order.setItems(currentOrderItems);

        // 保存订单
        if (orderRepository.createOrder(order)) {
            JOptionPane.showMessageDialog(this, "下单成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            currentOrderItems.clear();
            refreshOrderTable();
            totalLabel.setText("总金额：￥0.00");
        } else {
            JOptionPane.showMessageDialog(this, "下单失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
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
                if (label.equals("加入订单")) {
                    int dishId = (int) dishTable.getValueAt(dishTable.getSelectedRow(), 0);
                    String dishName = (String) dishTable.getValueAt(dishTable.getSelectedRow(), 1);
                    BigDecimal price = (BigDecimal) dishTable.getValueAt(dishTable.getSelectedRow(), 2);
                    
                    // 弹出数量输入对话框
                    String quantityStr = JOptionPane.showInputDialog(UserMainFrame.this, "请输入数量：", "1");
                    if (quantityStr != null && !quantityStr.isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr);
                            if (quantity > 0) {
                                OrderItem item = new OrderItem();
                                item.setDishId(dishId);
                                item.setDishName(dishName);
                                item.setPrice(price);
                                item.setQuantity(quantity);
                                currentOrderItems.add(item);
                                
                                // 更新总金额
                                BigDecimal total = currentOrderItems.stream()
                                    .map(orderItem -> orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                                totalLabel.setText(String.format("总金额：￥%.2f", total));
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(UserMainFrame.this, "请输入有效的数量！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (label.equals("结账")) {
                    int orderId = (int) orderTable.getValueAt(orderTable.getSelectedRow(), 0);
                    int result = JOptionPane.showConfirmDialog(UserMainFrame.this, "确定要结账吗？", "确认", JOptionPane.YES_NO_OPTION);
                    if (result == JOptionPane.YES_OPTION) {
                        if (orderRepository.updateOrderStatus(orderId, "已支付")) {
                            JOptionPane.showMessageDialog(UserMainFrame.this, "结账成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                            refreshOrderTable();
                        } else {
                            JOptionPane.showMessageDialog(UserMainFrame.this, "结账失败！", "错误", JOptionPane.ERROR_MESSAGE);
                        }
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
        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.add(new JLabel("订单号：" + order.getOrderId()));
        infoPanel.add(new JLabel("下单时间：" + order.getOrderTime()));
        infoPanel.add(new JLabel("总金额：￥" + order.getTotalAmount()));
        infoPanel.add(new JLabel("状态：" + order.getStatus()));
        
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
} 