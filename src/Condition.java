public class Condition extends SchemeComponent {
    boolean isFulfilled;
    public int arrowIndex;

    public Condition(int index, int arrowIndex){
        this.index = index;
        this.arrowIndex = arrowIndex;
        isFulfilled = false;
    }

    public Condition(boolean isFulfilled, int arrowIndex) {
        this.isFulfilled = isFulfilled;
        this.arrowIndex = arrowIndex;
        isFulfilled = false;
    }

    public boolean getCondition(){
        return isFulfilled;
    }

    public void setCondition(){
        int b = OutputHelper.ChooseIntValueSafe("Введите условие X" + index + ": ", 1);
        isFulfilled = b == 1;
    }

    public void setCondition(boolean cond){
        isFulfilled = cond;
    }

    void printInfo(){
        System.out.println("Выполняется проверка X" + index);
    }
}
