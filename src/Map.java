public class Map {
    private Cell[][] cells;

    public Map(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell getCell(int row, int column){
        if(row < cells.length && column < cells[0].length)
            return cells[row][column];
        else
            return null;
    }

    public CellTypes getCellType(int row, int column){
        Cell cell = getCell(row, column);
        if(cell != null)
            return cell.getType();
        else 
            return CellTypes.WALL;
    }
    public void drawFrame(Robot robot){
        for(int row = 0; row < cells.length; row++){
            for (int column = 0; column < cells[row].length; column++){
                Cell cell = getCell(row, column);
                if (cell.hasRobot){
                    System.out.print(robot.form);
                }
                else {
                    switch (cell.getType()) {
                        case SPACE -> System.out.print(".");
                        case WALL -> System.out.print("#");
                        case ENTRY -> System.out.print("E");
                        case EXIT -> System.out.print("X");
                    }
                }
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println("=".repeat(cells[0].length * 2));
    }
    public Cell getEntranceCell(){
        for(int i = 0; i < cells.length; i++){
            for (int j = 0; j < cells[i].length; j++){
                if(cells[i][j].getType() == CellTypes.ENTRY)
                    return cells[i][j];
            }
        }
        return null;
    }
}
