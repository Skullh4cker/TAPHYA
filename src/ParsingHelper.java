import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParsingHelper {
    private static final ArrayList<Character> whitelistSymbolChars = new ArrayList<>(Arrays.asList('Y', 'X', 'W', 'U', 'D', 'н', 'к'));
    private static final ArrayList<Character> whitelistMapChars = new ArrayList<>(Arrays.asList('X', 'E', '#', '.'));
    public static ArrayList<String> SplitOperations(String line) {
        ArrayList<String> operations = new ArrayList<>();
        StringBuilder currentOperation = new StringBuilder();
        char prevChar = 0;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (whitelistSymbolChars.contains(c) || Character.isDigit(c)) {
                if (prevChar == 'W') {
                    currentOperation.append(c);
                }
                else if ((c == 'Y' || c == 'X' || c == 'W' || c == 'U' || c == 'D') && !currentOperation.isEmpty() && prevChar != 'W') {
                    operations.add(currentOperation.toString());
                    currentOperation = new StringBuilder();
                    currentOperation.append(c);
                }
                else {
                    currentOperation.append(c);
                }
            }
            prevChar = c;
        }

        if (!currentOperation.isEmpty()) {
            operations.add(currentOperation.toString());
        }

        return operations;
    }

    public static int CheckSymbols(ArrayList<String> symbols, boolean checkEndAndBegginning){
        if(checkEndAndBegginning){
            if(!symbols.get(0).equals("Yн")){
                return 0;
            }
            else if(!symbols.get(symbols.size() - 1).equals("Yк")){
                return symbols.size() - 1;
            }
        }

        ArrayList<Integer> upperArrows = new ArrayList<>();
        ArrayList<Integer> lowerArrows = new ArrayList<>();

        for(int i = 0; i < symbols.size(); i++){
            var currentSymbol = symbols.get(i);
            if(currentSymbol.length() < 2) return i;

            char firstChar = currentSymbol.charAt(0);
            char secondChar = currentSymbol.charAt(1);

            switch (firstChar){
                case 'Y':
                    if((i == 0 || i == symbols.size() - 1) && checkEndAndBegginning){
                        if(secondChar != 'н' && secondChar != 'к')
                            return i;
                    }
                    else {
                        if(!Character.isDigit(secondChar))
                            return i;
                    }
                    break;
                case 'X':
                    if(!Character.isDigit(secondChar))
                        return i;
                    break;
                case 'D', 'U':
                    if(!Character.isDigit(secondChar))
                        return i;
                    else{
                        int index = Character.getNumericValue(secondChar);
                        if(firstChar == 'D' && !lowerArrows.contains(index))
                            lowerArrows.add(index);
                        else if(firstChar == 'U' && !upperArrows.contains(index))
                            upperArrows.add(index);
                    }

                    break;
                case 'W':
                    if(currentSymbol.length() < 3)
                        return i;

                    if(secondChar != 'U' || !Character.isDigit(currentSymbol.charAt(2)))
                        return i;
                    break;
                default:
                    return i;
            }
        }

        if(CheckMatchingArrows(upperArrows, lowerArrows))
            return -1;
        else
            return Integer.MAX_VALUE;
    }

    private static boolean CheckMatchingArrows(ArrayList<Integer> upperArrows, ArrayList<Integer> lowerArrows){
        return lowerArrows.containsAll(upperArrows);
    }

    public static int CheckChars(String line){
        char[] charArray = line.toCharArray();
        for(int i = 0; i < charArray.length; i++){
            if(!whitelistSymbolChars.contains(charArray[i]) && !Character.isDigit(charArray[i]))
                return i;
        }

        return -1;
    }

    public static ArrayList<Integer> GetAllConditionsIndexes(ArrayList<String> symbols){
        ArrayList<Integer> allIndexes = new ArrayList<>();
        var objects = ConvertToSchemeComponents(symbols);
        for(int i = 0; i < objects.size(); i++){
            var currentObj = objects.get(i);
            if(currentObj.type.equals("condition"))
                allIndexes.add(currentObj.index);
        }
        return allIndexes;
    }

    public static ArrayList<SchemeComponent> ConvertToSchemeComponents(ArrayList<String> symbols){
        ArrayList<SchemeComponent> schemeComponents = new ArrayList<SchemeComponent>();
        for (int i = 0; i < symbols.size(); i++) {
            String currentSymbol = symbols.get(i);
            if (currentSymbol.charAt(0) == 'X') {
                schemeComponents.add(new Condition(Integer.parseInt((currentSymbol.substring(1))),
                        Integer.parseInt(symbols.get(i+1).substring(1))));
            }
            else if (currentSymbol.charAt(0) == 'Y') {
                if (currentSymbol.length() == 1 || currentSymbol.equals("Yн")  || currentSymbol.equals("Yn")){
                    continue;
                }
                else if (currentSymbol.charAt(1) == 'k' || currentSymbol.charAt(1) == 'к'){
                    schemeComponents.add(new Command(-1));
                }
                else {
                    schemeComponents.add(new Command(Integer.parseInt(currentSymbol.substring(1))));
                }
            }
            else if (currentSymbol.charAt(0) == 'D') {
                schemeComponents.add(new ArrowDown(Integer.parseInt(currentSymbol.substring(1))));
            }
            else if (currentSymbol.charAt(0) == 'W'){
                schemeComponents.add(new Unconditional(Integer.parseInt(currentSymbol.substring(2))));
            }
        }
        return schemeComponents;
    }

    public static List<List<Boolean>> GetAllCombinations(ArrayList<Integer> allIndexes) {
        List<List<Boolean>> combinations = new ArrayList<>();
        ArrayList<Boolean> currentCombination = new ArrayList<>(allIndexes.size());
        for (int i = 0; i < allIndexes.size(); i++) {
            currentCombination.add(false);
        }
        GenerateCombinations(allIndexes, currentCombination, 0, combinations);
        return combinations;
    }

    private static void GenerateCombinations(ArrayList<Integer> allIndexes, ArrayList<Boolean> currentCombination, int currentIndex, List<List<Boolean>> combinations) {
        if (currentIndex == allIndexes.size()) {
            combinations.add(new ArrayList<>(currentCombination));
            return;
        }

        currentCombination.set(currentIndex, false);
        GenerateCombinations(allIndexes, currentCombination, currentIndex + 1, combinations);

        currentCombination.set(currentIndex, true);
        GenerateCombinations(allIndexes, currentCombination, currentIndex + 1, combinations);
    }

    public static ArrayList<Integer> GetNumbersFromString(String input){
        ArrayList<Integer> numbers = new ArrayList<>();

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            int number = Integer.parseInt(matcher.group());
            numbers.add(number);
        }
        return numbers;
    }

    private static String ReadFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

    public static String CheckSchemeFromFile(String file){
        try {
            String line = ReadFileAsString(file);
            System.out.println("Получена строка:");
            System.out.println(line);
            if(!line.isEmpty()){
                int errorIndex = ParsingHelper.CheckChars(line);

                if (errorIndex == -1) {
                    var symbols = ParsingHelper.SplitOperations(line);
                    //PrintSymbols(symbols);
                    int symbolErrorIndex = ParsingHelper.CheckSymbols(symbols, true);

                    if (symbolErrorIndex == -1)
                        System.out.println("Строка правильная!");
                    else if(symbolErrorIndex == Integer.MAX_VALUE)
                        System.out.println("Несоответствие стрелок!");
                    else
                        OutputHelper.PrintSymbolError(line, symbols, symbolErrorIndex);
                }
                else {
                    OutputHelper.PrintCharError(line, errorIndex, "Введён некорректный символ №" + errorIndex);
                }

                return line;
            }
        }
        catch (IOException e) {
            System.out.println("Ошибка при чтении файла");
        }

        return null;
    }
    /*
    private static boolean Contains(char c) {
        for (char allowedChar : whitelistSymbolChars) {
            if (c == allowedChar) {
                return true;
            }
        }
        return false;
    }
*/
    //Может стоит ввести масcив разрешённых чаров для карты? Да я так и хотел
    //Ну чё, давай пиши :) ща всё буит
    //Гениальный кодинг конечно
    //дыа
    //общение через комментарии - топ　凄いですね
    //Чексимволс надо для мапы писать?
    //По идее нет, там таких сложных проверок не должно быть. Просто чек на аномальные символы можно тут сделать
    //Вообще сейчас можно условно раскидать по классам прямо в этом же цикле for
    //Можно, да, но классов у нас нет. Да и эта функция возвращает же по идее просто список char
    //А, бля, я сейчас понял, что нам же нужен тут двумерный массив, иначе карта потеряется. Бля, тогда список списков делать или как?
    //А, та хочешь на две функции разделить, понял
    //Ну наверное список списков,
    //А может тупо список строк? А перед возвратом, когда размер известен, преобразовать в массив двумерный
    //А у нас не будет проблемы с тем, что карта же не обязательно прямоугольная
    //Короче я придумал, можно просто пустыми клетками заполнять, а размерность массива по самой большой строке/столбцу выбирать
    //А как потом такое кастить? Как он поймёт, где бесмыссленные клетки, а где со смыслом. Можно числами
    //Ну просто, где у нас символов не хватает, туда добавлять

    private static String[] CleanInputMap(String[] lines){
        var cleanLines = new ArrayList<String>();
        int exitCounter=0, enterCounter = 0;
        for(var line : lines){
            StringBuilder newLine = new StringBuilder();
            for(Character ch : line.toCharArray()){
                if(whitelistMapChars.contains(ch)){
                    newLine.append(ch);
                    //if (ch == 'E') enterCounter++;
                    //if (ch == 'X') exitCounter++;
                    //System.out.print("\"" + ch + "\" ");
                }
                else if(ch == ' ')
                    newLine.append('#');
                else if(ch != '\n' && ch != '\r')
                    return null;
            }
            cleanLines.add(newLine.toString());
            //System.out.println(newLine);
        }
        /*
        if (enterCounter > 1 || exitCounter > 1 || enterCounter == 0 || exitCounter == 0 )
        {
            System.out.println("Неправильное количество входов и выходов!");
        }*/
        return cleanLines.toArray(new String[0]);
    }
    public static char[][] ParseMapFromFile(String filepath) throws InvalidMapException, IOException{
        //ПРОИЗОШЁЛ РЕФАКТОРИНГ
        //АВТОР - GPT
        //Мне тож кажется так получше смотрится
        //Я орнул почему-то с этого
        String allLines = ReadFileAsString(filepath);
        if(allLines.isEmpty())
            throw new InvalidMapException("Строка в файле была пустой!");

        String[] cleanLines = CleanInputMap(allLines.split("\n"));
        if(cleanLines == null)
            throw new InvalidMapException("Карта содержит неизвестные символы!");

        char[][] mapArray = ConvertToCharArray(cleanLines);
        if(!CheckEntryExit(mapArray))
            throw new InvalidMapException("Неверное число входов/выходов!");

        if(!CheckWalls(mapArray))
            throw new InvalidMapException("Неверное расположение стен (область не замкнута)!");

        //PrintCharArray(mapArray);
        return mapArray;
    }
    public static Cell[][] ConvertToCells(char[][] charMap){
        Cell[][] cells = new Cell[charMap.length][charMap[0].length];
        for(int row = 0; row < charMap.length; row++){
            for(int column = 0; column < charMap[row].length; column++){
                char currentChar = charMap[row][column];
                CellTypes cellType = CellTypes.SPACE;
                boolean hasRobot = false;
                switch (currentChar){
                    case 'E':
                        hasRobot = true;
                        cellType = CellTypes.ENTRY;
                        break;
                    case 'X':
                        cellType = CellTypes.EXIT;
                        break;
                    case '#':
                        cellType = CellTypes.WALL;
                        break;
                }
                cells[row][column] = new Cell(row, column, cellType, hasRobot);
            }
        }
        return cells;
    }
    private static boolean CheckEntryExit(char[][] mapArray){
        int entryCounter = 0;
        int exitCounter = 0;
        for(int i = 0; i < mapArray.length; i++){
            for(int j = 0; j < mapArray[i].length; j++){
                if(mapArray[i][j] == 'E')
                    entryCounter++;
                else if(mapArray[i][j] == 'X')
                    exitCounter++;
            }
        }
        return entryCounter == 1 && exitCounter == 1;
    }
    /*
    // Function to check if a given cell can be included in DFS
    public static boolean isSafe(char[][] grid, boolean[][] visited, int row, int col) {
        return !(row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] == '#' || visited[row][col]);
    }

    // BFS method to explore any '.' from boundary
    public static void DFS(char[][] grid, boolean[][] visited, int row, int col) {
        visited[row][col] = true;

        int[] dr = {-1, 0, 1, 0};  // Directions for row-wise DFS traversal
        int[] dc = {0, 1, 0, -1};  // Directions for column-wise DFS traversal

        for (int i = 0; i < 4; i++)
            if (isSafe(grid, visited, row + dr[i], col + dc[i]))
                DFS(grid, visited, row + dr[i], col + dc[i]);
    }

    // Function to check if area is closed
    public static boolean isClosed(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;

        boolean[][] visited = new boolean[rows][cols];

        // Exploring boundary cells
        for (int i = 0; i < rows; i++) {
            if (grid[i][0] == '.' && !visited[i][0])
                DFS(grid, visited, i, 0);

            if (grid[i][cols - 1] == '.' && !visited[i][cols - 1])
                DFS(grid, visited, i, cols - 1);
        }

        for (int i = 0; i < cols; i++) {
            if (grid[0][i] == '.' && !visited[0][i])
                DFS(grid, visited, 0, i);

            if (grid[rows - 1][i] == '.' && !visited[rows - 1][i])
                DFS(grid, visited, rows - 1, i);
        }

        // Checking if any left '.' is not visited yet
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (grid[i][j] == '.' && !visited[i][j])
                    return true;

        return false;
    }

    static boolean dfs(char[][] maze, boolean[][] visited, int row, int col) {
        int rows = maze.length;
        int cols = maze[0].length;

        if (row < 0 || row >= rows || col < 0 || col >= cols) {
            return false;
        }

        if (maze[row][col] == '#' || visited[row][col]) {
            return true;
        }

        visited[row][col] = true;


        return dfs(maze, visited, row + 1, col)
                && dfs(maze, visited, row - 1, col)
                && dfs(maze, visited, row, col + 1)
                && dfs(maze, visited, row, col - 1);
    }
     */
    private static boolean CheckWalls(char[][] grid){
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return false;
        }

        int rows = grid.length;
        int cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (i == 0 || i == rows - 1 || j == 0 || j == cols - 1) {
                    if (grid[i][j] != '#' && grid[i][j] != 'E' && grid[i][j] != 'X') {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static char[][] ConvertToCharArray(String[] lines){
        int width = 0;
        for (String str : lines) {
            width = Math.max(width, str.length());
        }
        int height = lines.length;
        char[][] mapArray = new char[height][width];
        for(int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                if(j >= lines[i].length())
                    mapArray[i][j] = '#';
                else
                    mapArray[i][j] = lines[i].charAt(j);
            }
        }
        return mapArray;
    }
    private static void PrintCharArray(char[][] array){
        for(int i = 0; i < array.length; i++){
            for(int j = 0; j < array[i].length; j++){
                System.out.print("\"" + array[i][j] + "\" ");
            }
            System.out.println(" ");
        }
    }
}
