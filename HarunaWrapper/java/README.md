### HarunaRequest

Example Usage
```java
public class Test {
    public static void main(String[] args) {
        HarunaRequest haruna = new HarunaRequest("http://localhost:6969", "your_password");
        if(haruna.hasVoted("214829164253937674")) {
            System.out.println("The user has voted");
        } else {
            System.out.println("The user hasn't voted yet");
        }
    }
    
}
```