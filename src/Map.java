public class Map {
    public Cell[][] cells;

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
        //TODO: Сюда добавить проверку на выход за пределы карты
        return cells[row][column];
    }
    public CellTypes getCellType(int row, int column){
        return getCell(row, column).getType();
    }
    public void drawFrame(){
        for(int row = 0; row < cells.length; row++){
            for (int column = 0; column < cells[row].length; column++){
                Cell cell = getCell(row, column);
                if (cell.hasRobot){
                    System.out.print("R");
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
