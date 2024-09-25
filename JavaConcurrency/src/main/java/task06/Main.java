package task06;

public class Main {
    static public void main(String[] args) {
        Company company = new Company(10);
        Founder founder = new Founder(company);
        founder.start();
    }
}
