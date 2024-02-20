public class Cell {
    public int column;
    public int row;
    public CellTypes type;
    public boolean hasRobot;

    public Cell(int row, int column, CellTypes type, boolean hasRobot) {
        this.row = row;
        this.column = column;
        this.type = type;
        this.hasRobot = hasRobot;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public CellTypes getType() {
        return type;
    }

    public void setType(CellTypes type) {
        this.type = type;
    }

    public boolean isHasRobot() {
        return hasRobot;
    }

    public void setHasRobot(boolean hasRobot) {
        this.hasRobot = hasRobot;
    }
}
