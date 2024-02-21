import java.util.ArrayList;

public class Robot {
    private Directions direction; //другой вариант названия: napravlenie
    private int column;
    private int row;
    private ArrayList<SchemeComponent> objects;
    private Map map;
    public char form;

    public Robot(Directions direction, int row, int column, ArrayList<SchemeComponent> objects, Map map) {
        this.direction = direction;
        this.row = row;
        this.column = column;
        this.objects = objects;
        this.map = map;
        updateForm();
        map.getCell(row, column).hasRobot = true;
    }

    public Directions getDirection() {
        return direction;
    }

    public void setDirection(Directions direction)
    {
        this.direction = direction;
        updateForm();
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public ArrayList<SchemeComponent> getSchemeComponents() {
        return objects;
    }

    public void setSchemeComponents(ArrayList<SchemeComponent> objects) {
        this.objects = objects;
    }

    public void turnLeft(){
        switch (direction){
            case NORTH -> setDirection(Directions.WEST);
            case EAST -> setDirection(Directions.NORTH);
            case SOUTH -> setDirection(Directions.EAST);
            case WEST -> setDirection(Directions.SOUTH);
        }
    }

    public void turnRight(){
        switch (direction){
            case NORTH -> setDirection(Directions.EAST);
            case EAST -> setDirection(Directions.SOUTH);
            case SOUTH -> setDirection(Directions.WEST);
            case WEST -> setDirection(Directions.NORTH);
        }
    }

    public void move(){
        var oldCell = map.getCell(row, column);
        int rowMemory = row;
        int columnMemory = column;

        switch (direction) {
            case NORTH -> row--;
            case EAST -> column++;
            case SOUTH -> row++;
            case WEST -> column--;
        }
        Cell newCell = map.getCell(row, column);
        if(newCell != null && newCell.getType() != CellTypes.WALL){
            oldCell.hasRobot = false;
            newCell.hasRobot = true;
        }
        else{
            row = rowMemory;
            column = columnMemory;
        }
    }

    public boolean isWallRight(){
        return switch (direction) {
            case NORTH -> map.getCellType(row, column + 1) == CellTypes.WALL;
            case EAST -> map.getCellType(row + 1, column) == CellTypes.WALL;
            case SOUTH -> map.getCellType(row, column - 1) == CellTypes.WALL;
            case WEST -> map.getCellType(row - 1, column) == CellTypes.WALL;
        };
    }

    public boolean isWallFront(){
        return switch (direction) {
            case NORTH -> map.getCellType(row - 1, column) == CellTypes.WALL;
            case EAST -> map.getCellType(row, column + 1) == CellTypes.WALL;
            case SOUTH -> map.getCellType(row + 1, column) == CellTypes.WALL;
            case WEST -> map.getCellType(row, column - 1) == CellTypes.WALL;
        };
    }

    public boolean isExitRight(){
        return switch (direction) {
            case NORTH -> map.getCellType(row, column + 1) == CellTypes.EXIT;
            case EAST -> map.getCellType(row + 1, column) == CellTypes.EXIT;
            case SOUTH -> map.getCellType(row, column - 1) == CellTypes.EXIT;
            case WEST -> map.getCellType(row - 1, column) == CellTypes.EXIT;
        };
    }

    private void updateForm(){
        switch (direction) {
            case NORTH -> form = '^';
            case EAST -> form = '>';
            case SOUTH -> form = 'v';
            case WEST -> form = '<';
        };
    }

    public void convertStringToObjects(String lsa){
        ArrayList<String> parse = ParsingHelper.SplitOperations(lsa);
        objects = ParsingHelper.ConvertToSchemeComponents(parse);
    }

    public void run(){
        map.drawFrame(this);
        int step = 0;
        SchemeComponent currentObj = objects.get(step);
        while (currentObj.index != -1) {
            if (map.getCellType(row, column) == CellTypes.EXIT){
                System.out.println("=".repeat(map.getCells()[0].length * 2));
                System.out.println("Выход найден!");
                break;
            }

            currentObj = objects.get(step);

            if (currentObj instanceof Command command) {
                switch (command.index) {
                    case 1 -> turnRight();
                    case 2 -> turnLeft();
                    case 3 -> move();
                }
                map.drawFrame(this);
                step++;
                continue;
            }
            else if (currentObj instanceof Condition condition) {
                switch (condition.index){
                    case 0 -> condition.setCondition(!isExitRight());
                    case 1 -> condition.setCondition(isWallRight());
                    case 2 -> condition.setCondition(isWallFront());
                }

                if (condition.isFulfilled) {
                    step++;
                    continue;
                }
                else {
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i) instanceof ArrowDown && objects.get(i).index == condition.arrowIndex) {
                            step = i;
                            break;
                        }
                    }
                    continue;
                }
            }
            else if (currentObj instanceof Unconditional) {
                for (int i = 0; i < objects.size(); i++) {
                    if (objects.get(i) instanceof ArrowDown && objects.get(i).index == currentObj.index) {
                        step = i;
                        break;
                    }
                }
            }

            step++;
        }
    }
}
