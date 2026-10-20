import java.util.*;

public class ZumbiFlyweight {

    // Flyweight: um record já é imutável (campos finais automáticos)
    record ZumbiTipo(String nome, String malha, int vidaMax) {
        void desenhar(double x, double y) {
            System.out.printf("%s em (%.0f, %.0f) usando %s%n", nome, x, y, malha);
        }
    }

    // Fábrica: um mapa + computeIfAbsent garante 1 instância por tipo
    static final Map<String, ZumbiTipo> TIPOS = new HashMap<>();

    static ZumbiTipo obterTipo(String nome) {
        return TIPOS.computeIfAbsent(nome, n -> new ZumbiTipo(n, "malha_" + n + ".obj",
                switch (n) {
                    case "comum" -> 100;
                    case "corredor" -> 60;
                    case "tanque" -> 500;
                    default -> throw new IllegalArgumentException(n);
                }));
    }

    // Contexto: só estado individual + referência ao tipo compartilhado
    static class Zumbi {
        final ZumbiTipo tipo;
        double x, y;
        int vida;

        Zumbi(String nomeTipo, double x, double y) {
            this.tipo = obterTipo(nomeTipo);
            this.x = x;
            this.y = y;
            this.vida = tipo.vidaMax();
        }
    }

    public static void main(String[] args) {
        List<Zumbi> horda = new ArrayList<>();
        for (int i = 0; i < 9000; i++) horda.add(new Zumbi("comum", i, 0));
        for (int i = 0; i < 800; i++)  horda.add(new Zumbi("corredor", i, 10));
        for (int i = 0; i < 200; i++)  horda.add(new Zumbi("tanque", i, 20));

        System.out.println(horda.size() + " zumbis, " + TIPOS.size() + " tipos");
        System.out.println("Compartilham o tipo? " + (horda.get(0).tipo == horda.get(1).tipo));

        horda.get(0).vida -= 100;  // dano só no zumbi 0
        System.out.println("Vida zumbi 0: " + horda.get(0).vida + " | zumbi 1: " + horda.get(1).vida);

        horda.get(0).tipo.desenhar(horda.get(0).x, horda.get(0).y);
    }
}
