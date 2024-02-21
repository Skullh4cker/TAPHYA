class Command extends SchemeComponent {
    public Command(int index){
        this.index = index;
    }

    void printInfo(){
        if(index != -1)
            System.out.println("Выполняется команда Y" + index);
        else
            System.out.println("Выполняется команда конца Yк");
    }
}