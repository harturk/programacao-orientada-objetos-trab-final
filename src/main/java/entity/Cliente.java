package entity;

public class Cliente {
    private int cod;
    private String nome;
    private String email;

    public Cliente(int cod, String nome, String email) {
        this.cod = cod;
        this.nome = nome;
        this.email = email;
    }

    public int getCod() {
        return this.cod;
    }

    public String getNome() {
        return this.nome;
    }

    public String getEmail() {
        return this.email;
    }
}
