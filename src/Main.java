import java.io.IOException;
import java.util.*;

public class Main
{
    private static boolean detailedOutput = false;

    public static void main(String[] args) {
        String[] studentsFilePaths = {"Efremov.txt", "Kuznetsov.txt", "Vavilov.txt", "Agafonov.txt", "Rogov.txt"};
        String algorithmPath = "Robot.txt";
        //String algorithmPath = studentsFilePaths[0];
        String mapPath = "Maps/spiral.txt";

        String line = ParsingHelper.CheckSchemeFromFile(algorithmPath);
        if(line != null){
            int mode = OutputHelper.ChooseModeSafe();
            var symbols = ParsingHelper.SplitOperations(line);

            switch (mode){
                case 1:
                    RunFirstMode(symbols);
                    break;
                case 2:
                    RunSecondMode(symbols, OutputHelper.ChooseTrueConditionSafe(ParsingHelper.GetAllConditionsIndexes(symbols)));
                    break;
                case 3:
                    RunThirdMode(symbols);
                    break;
                case 4:
                    try{
                        var charMap = ParsingHelper.ParseMapFromFile(mapPath);
                        RunRobotMode(symbols, charMap);
                    }
                    catch (IOException ex){
                        System.err.println("Ошибка при чтении файла!");
                    }
                    catch (InvalidMapException ex){
                        System.err.println(ex.getMessage());
                    }
                    break;
            }
        }
    }

    private static void RunFirstMode(ArrayList<String> symbols){
        ArrayList<SchemeComponent> objects = ParsingHelper.ConvertToSchemeComponents(symbols);
        /*for(int i = 0; i < obj.size(); i++){
            System.out.println(obj.get(i) + "\t" + obj.get(i).index);
        }*/

        System.out.println("Выполняется команда начала Yн");

        int step = 0;
        var currentObj = objects.get(step);
        while (currentObj.index != -1) {
            currentObj = objects.get(step);

            if (currentObj instanceof Command command) {
                command.printInfo();
                step++;
                continue;
            }
            else if (currentObj instanceof Condition condition ) {
                if(detailedOutput)
                    condition.printInfo();

                condition.setCondition();
                if (condition.isFulfilled) {
                    step++;
                    continue;
                }
                else {
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i) instanceof ArrowDown && (objects.get(i).index == condition.arrowIndex)) {
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

    private static void RunSecondMode(ArrayList<String> symbols, ArrayList<Integer> trueIndexes) {
        var objects = ParsingHelper.ConvertToSchemeComponents(symbols);
        ArrayList<Integer> tags = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++){
            var currentObj = objects.get(i);
            if(currentObj instanceof Condition condition){
                for (int j = 0; j < trueIndexes.size(); j++){
                    if(currentObj.index == trueIndexes.get(j)){
                        condition.setCondition(true);
                        break;
                    }
                }
            }
        }

        int step = 0;
        var currentObj = objects.get(step);

        System.out.println("Выполняется команда начала Yн");

        while (currentObj.index != -1) {
            currentObj = objects.get(step);

            if (currentObj instanceof Command command) {
                command.printInfo();
                step++;
                continue;
            }
            else if (currentObj instanceof Condition condition) {
                if (condition.isFulfilled) {
                    step++;
                    continue;
                }
                else {
                    for (int i = 0; i < objects.size(); i++) {
                        if (objects.get(i) instanceof ArrowDown && (objects.get(i).index == condition.arrowIndex)) {
                            for (int k = 0; k < tags.size(); k++){
                                if(tags.get(k) == condition.arrowIndex){
                                    System.out.println("Далее алгоритм зацикливается!");
                                    return;
                                }
                            }
                            tags.add(condition.arrowIndex);
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
                        for (int k = 0; k < tags.size(); k++){
                            if(tags.get(k) == currentObj.index){
                                System.out.println("Далее алгоритм зацикливается!");
                                return;
                            }
                        }
                        tags.add(currentObj.index);
                        step = i;
                        break;
                    }
                }
            }
            step++;
        }
    }

    private static void RunThirdMode(ArrayList<String> symbols){
        ArrayList<Integer> allIndexes = ParsingHelper.GetAllConditionsIndexes(symbols);

        List<List<Boolean>> allCombinations = ParsingHelper.GetAllCombinations(allIndexes);
        //System.out.println(allCombinations);

        for(int i = 0; i < allCombinations.size(); i++){
            ArrayList<Integer> trueIndexes = new ArrayList<>();
            for(int j = 0; j < allCombinations.get(i).size(); j++){
                if(allCombinations.get(i).get(j))
                    trueIndexes.add(allIndexes.get(j));
            }
            StringBuilder str = new StringBuilder();
            if(!trueIndexes.isEmpty()){
                for(int j = 0; j < trueIndexes.size(); j++){
                    str.append("X");
                    str.append(trueIndexes.get(j));
                }
            }
            else str.append("нет условий");

            System.out.println("Запуск алгоритма с истинными условиями: " + str);
            RunSecondMode(symbols, trueIndexes);
            System.out.println("Переход к следующей вариации...");
            System.out.println("==============================================================");
        }
    }

    private static void RunRobotMode(ArrayList<String> symbols, char[][] charMap){
        var cells = ParsingHelper.ConvertToCells(charMap);

        if(cells.length > 0){
            Map map = new Map(cells);
            var schemeComponents = ParsingHelper.ConvertToSchemeComponents(symbols);
            var entranceCell = map.getEntranceCell();
            Robot robot = new Robot(Directions.NORTH, entranceCell.row, entranceCell.column, schemeComponents, map);
            map.drawFrame(robot);
            robot.run();
        }
    }
}
