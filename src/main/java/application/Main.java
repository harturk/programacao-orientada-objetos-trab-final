package application;

import application.Interface.TelaPrincipal;

public class Main {
    public static void main(String[] args) {
        TelaPrincipal principal = new TelaPrincipal();
        App app = new App();
        app.executar();
    }
}
