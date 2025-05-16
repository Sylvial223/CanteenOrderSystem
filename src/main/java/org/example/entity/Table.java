package org.example.entity;

public class Table {
    private int tableId;
    private String tableNumber;
    private String status;

    public Table() {}

    public Table(int tableId, String tableNumber, String status) {
        this.tableId = tableId;
        this.tableNumber = tableNumber;
        this.status = status;
    }

    // Getters and Setters
    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return tableNumber + " (" + status + ")";
    }
} 