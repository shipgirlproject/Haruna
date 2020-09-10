package haruna;

public class Sortie {
    public static void main(String[] args) throws Exception {
        System.setProperty("vertx.disableDnsResolver", "true");
        new HarunaServer()
                .buildRoutes()
                .start();
    }
}
