import java.util.ArrayList;

public class Robot {
    public Directions direction; //другой вариант названия: napravlenie
                                    // North, East, South, West
    public int column;
    public int row;
    public ArrayList<SchemeComponent> objects;
    public Map map;

    public Robot(Directions direction, int row, int column, ArrayList<SchemeComponent> objects, Map map) {
        this.direction = direction;
        this.row = row;
        this.column = column;
        this.objects = objects;
        this.map = map;
        map.getCell(row, column).hasRobot = true;
    }

    public Directions getDirection() {
        return direction;
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
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
            case NORTH -> direction = Directions.WEST;
            case EAST -> direction = Directions.NORTH;
            case SOUTH -> direction =Directions.EAST;
            case WEST -> direction = Directions.SOUTH;
        }
    }

    public void turnRight(){
        switch (direction){
            case NORTH -> direction = Directions.EAST;
            case EAST -> direction = Directions.SOUTH;
            case SOUTH -> direction =Directions.WEST;
            case WEST -> direction = Directions.NORTH;
        }
    }

    public void move(){
        map.getCell(row, column).setHasRobot(false);
        switch (direction) {
            case NORTH -> row--;
            case EAST -> column++;
            case SOUTH -> row++;
            case WEST -> column--;
        }
        map.getCell(row, column).setHasRobot(true);
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

    public void convertStringToObjects(String lsa){
        ArrayList<String> parse = ParsingHelper.SplitOperations(lsa);
        objects = ParsingHelper.ConvertToSchemeComponents(parse);
    }

    public void run(){
        int step = 0;
        SchemeComponent currentObj = objects.get(step);
        while (currentObj.index != -1) {

            currentObj = objects.get(step);

            if (currentObj.type.equals("command")) {
                Command command = (Command) currentObj;
                switch (command.index) {
                    case 1 -> turnRight();
                    case 2 -> turnLeft();
                    case 3 -> {
                        move();
                        map.drawFrame();
                    }
                }
                step++;
                continue;
            }
            else if (currentObj.type.equals("condition")) {
                Condition condition = (Condition) currentObj;
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
                        if ((objects.get(i).type.equals("arrow")) && (objects.get(i).index == condition.arrowIndex)) {
                            step = i;
                            break;
                        }
                    }
                    continue;
                }
            }
            else if (currentObj.type.equals("unconditional")) {
                for (int i = 0; i < objects.size(); i++) {
                    if (objects.get(i).type.equals("arrow") && objects.get(i).index == currentObj.index) {
                        step = i;
                        break;
                    }
                }
            }
            if (map.getCellType(row, column) == CellTypes.EXIT){
                System.out.println("Выход найден!");
                break;
            }
            step++;
        }
    }
}
