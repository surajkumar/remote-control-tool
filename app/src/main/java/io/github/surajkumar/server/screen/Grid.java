package io.github.surajkumar.server.screen;

public class Grid {
    private static final int DEFAULT_ROWS = 4;
    private static final int DEFAULT_COLUMNS = 2;
    private int rows;
    private int columns;

    public Grid() {
        this(DEFAULT_ROWS, DEFAULT_COLUMNS);
    }

    public Grid(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }
}
