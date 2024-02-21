import java.io.File;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class OutputHelper {
    public static void PrintCharError(String line, int index, String message){
        int i = 0;
        while(i < index){
            System.out.print(' ');
            i++;
        }
        System.out.println('^');
        System.out.println(message);
    }

    public static void PrintSymbolError(String line, ArrayList<String> symbols, int index){
        int lineIndex = 0;
        for(int i = 0; i < index; i++){
            lineIndex += symbols.get(i).length();
        }

        PrintCharError(line, lineIndex, "Неверный синтаксис символа №" + index  + ": " + symbols.get(index));
    }

    public static ArrayList<Integer> ChooseTrueConditionSafe(ArrayList<Integer> validIndexes){
        ArrayList<Integer> trueConditionsIndexes = new ArrayList<>();
        System.out.println("Введите истинные условия: ");
        Scanner scanner = new Scanner(System.in);

        while (true){
            String trueConditionsInput = scanner.next();

            int errorIndex = ParsingHelper.CheckChars(trueConditionsInput);

            if (errorIndex == -1) {
                var symbols = ParsingHelper.SplitOperations(trueConditionsInput);
                int symbolErrorIndex = ParsingHelper.CheckSymbols(symbols, false);

                if (symbolErrorIndex != -1)
                    PrintSymbolError(trueConditionsInput, symbols, symbolErrorIndex);
                else if(trueConditionsInput.matches("^[X0-9]+$")){
                    var indexes = ParsingHelper.GetNumbersFromString(trueConditionsInput);
                    if(validIndexes.containsAll(indexes)){
                        trueConditionsIndexes.addAll(indexes);
                        break;
                    }
                }
            }
            System.out.println("Похоже, строка была введена некорректно!");
            scanner.nextLine();
        }

        return trueConditionsIndexes;
    }

    public static int ChooseModeSafe(){
        Scanner scanner = new Scanner(System.in);
        int mode = 0;
        while(true){
            try {
                System.out.print("Выберите режим работы (1 - пошаговый, 2 - с вводом всех условий, 3 - перебор вариаций, 4 - режим робота): ");
                mode = scanner.nextInt();
                if(mode >= 1 && mode <= 4){
                    System.out.println("Вы успешно выбрали режим " + mode + "!");
                    break;
                }
                else
                    System.out.println("Режим " + mode + " неизвестен...");
            }
            catch (InputMismatchException e) {
                System.out.println("Ошибка! Пожалуйста, введите целое число.");
                scanner.nextLine();
            }
        }
        return mode;
    }

    public static int ChooseIntValueSafe(String defaultMessage, int upperBound){
        Scanner scanner = new Scanner(System.in);
        int value = 0;
        while(true){
            try {
                System.out.print(defaultMessage);
                value = scanner.nextInt();
                if(value >= 0 && value <= upperBound)
                    break;

                else
                    System.out.println("Значение " + value + " неверно...");
            }
            catch (InputMismatchException e) {
                System.out.println("Ошибка! Пожалуйста, введите целое число.");
                scanner.nextLine();
            }
        }
        return value;
    }

    public static String ChooseFilePathSafe(String directory, String errorMessage) throws InvalidMapException {
        String chosenPath = "";
        var textFiles = GetAllFiles(directory);

        if (textFiles == null)
            throw new InvalidMapException("Ошибка получения файлов! Вероятно, директория \"" + directory + "\" не найдена");
        if (textFiles.length == 0)
            throw new InvalidMapException(errorMessage);

        for (int i = 0; i < textFiles.length; i++) {
            System.out.println(i + ") " + textFiles[i].getName());
        }

        int chosenIndex = ChooseIntValueSafe("Пожалуйста, выберите файл по индексу: ", textFiles.length - 1);
        chosenPath = textFiles[chosenIndex].getAbsolutePath();

        return chosenPath;
    }

    private static File[] GetAllFiles(String mapsDirectory){
        File folder = new File(mapsDirectory);

        if (folder.exists() && folder.isDirectory())
            return folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        return null;
    }
}
