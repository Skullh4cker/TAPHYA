import java.util.*;

public class Main
{
    //TODO: Хорошенько отдохнуть :)
    private static boolean detailedOutput = false;

    public static void main(String[] args) {
        String[] studentsFilePaths = {"Efremov.txt", "Kuznetsov.txt", "Vavilov.txt", "Agafonov.txt", "Rogov.txt"};
        String filePath = studentsFilePaths[2];
        String line = ParsingHelper.CheckSchemeFromFile(filePath);

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

            if (currentObj.type.equals("command")) {
                Command command = (Command) currentObj;
                command.printInfo();
                step++;
                continue;
            }
            else if (currentObj.type.equals("condition")) {
                Condition condition = (Condition) currentObj;
                if(detailedOutput)
                    condition.printInfo();

                condition.setCondition();
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
            step++;
        }
    }

    private static void RunSecondMode(ArrayList<String> symbols, ArrayList<Integer> trueIndexes) {
        var objects = ParsingHelper.ConvertToSchemeComponents(symbols);
        ArrayList<Integer> tags = new ArrayList<>();

        for (int i = 0; i < objects.size(); i++){
            var currentObj = objects.get(i);
            if(currentObj.type.equals("condition")){
                for (int j = 0; j < trueIndexes.size(); j++){
                    if(currentObj.index == trueIndexes.get(j)){
                        ((Condition) currentObj).setCondition(true);
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

            if (currentObj.type.equals("command")) {
                ((Command) currentObj).printInfo();
                step++;
                continue;
            }
            else if (currentObj.type.equals("condition")) {
                Condition condition = (Condition) currentObj;
                if (condition.isFulfilled) {
                    step++;
                    continue;
                }
                else {
                    for (int i = 0; i < objects.size(); i++) {
                        if ((objects.get(i).type.equals("arrow")) && (objects.get(i).index == condition.arrowIndex)) {
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
            else if (currentObj.type.equals("unconditional")) {
                for (int i = 0; i < objects.size(); i++) {
                    if (objects.get(i).type.equals("arrow") && objects.get(i).index == currentObj.index) {
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
            System.out.println("Запуск алгоритма с истинными условиями: " + "X" + String.join("X", trueIndexes.stream().map(Object::toString).toArray(String[]::new)));
            RunSecondMode(symbols, trueIndexes);
            System.out.println("Переход к следующей вариации...");
            System.out.println("==============================================================");
        }
    }
}
