class Condition extends SchemeComponent {
    boolean isFulfilled;
    public int arrowIndex;

    public Condition(int index, int arrowIndex){
        this.index = index;
        this.arrowIndex = arrowIndex;
        type = "condition";
        isFulfilled = false;
    }

    public Condition(boolean isFulfilled, int arrowIndex) {
        this.isFulfilled = isFulfilled;
        this.arrowIndex = arrowIndex;
        type = "condition";
        isFulfilled = false;
    }

    public boolean getCondition(){
        return isFulfilled;
    }

    public void setCondition(){
        int b = OutputHelper.ChooseConditionSafe("Введите условие X" + index + ": ");
        isFulfilled = b == 1;
    }

    public void setCondition(boolean cond){
        isFulfilled = cond;
    }

    void printInfo(){
        System.out.println("Выполняется проверка X" + index);
    }
}
