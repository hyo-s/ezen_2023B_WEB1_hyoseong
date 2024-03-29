package example.day10._1example;

public class User1Thread extends Thread {

    // 필드 User1 객체가 가지고 있는 계산기
    private Calculator calculator;

    public User1Thread(){
        // setName : Thread 클래스로부터 상속받은 함수 ( 작업스레드 이름 변경 )
        setName("User1Thread");
    }

    // SETTER
    public void setCalculator(Calculator calculator){
        this.calculator = calculator;
    }

    // extends Thread : 작업스레드 생성하기 위해
    @Override
    public void run() {
        calculator.setMemory(100);
    }
}
