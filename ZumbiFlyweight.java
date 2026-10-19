import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZumbiFlyweight {

    /** Flyweight: dados pesados, compartilhados e IMUTÁVEIS (estado intrínseco). */
    static final class ZumbiTipo {
        private final String nome;
        private final String malha;      // imagine vários MB
        private final String textura;    // imagine vários MB
        private final int vidaMax;
        private final int dano;
        private final double velocidade;

        ZumbiTipo(String nome, String malha, String textura,
                  int vidaMax, int dano, double velocidade) {
            this.nome = nome;
            this.malha = malha;
            this.textura = textura;
            this.vidaMax = vidaMax;
            this.dano = dano;
            this.velocidade = velocidade;
        }

        int getVidaMax() { return vidaMax; }
        int getDano() { return dano; }
        double getVelocidade() { return velocidade; }

        // O estado extrínseco (posição) chega por parâmetro.
        void desenhar(double x, double y) {
            System.out.printf("Desenhando %s em (%.0f, %.0f) com %s e %s%n",
                    nome, x, y, malha, textura);
        }
    }

    /** Factory: garante uma única instância de cada tipo. */
    static final class FabricaDeTipos {
        private static final Map<String, ZumbiTipo> TIPOS = new HashMap<>();

        static ZumbiTipo obter(String nome) {
            return TIPOS.computeIfAbsent(nome, n -> {
                System.out.println("[Fábrica] Carregando tipo '" + n + "' (só 1 vez)");
                switch (n) {
                    case "comum":
                        return new ZumbiTipo("comum", "malha_comum.obj", "pele_podre.png", 100, 10, 1.0);
                    case "corredor":
                        return new ZumbiTipo("corredor", "malha_corredor.obj", "pele_rapida.png", 60, 15, 3.0);
                    case "tanque":
                        return new ZumbiTipo("tanque", "malha_tanque.obj", "pele_grossa.png", 500, 40, 0.5);
                    default:
                        throw new IllegalArgumentException("Tipo desconhecido: " + n);
                }
            });
        }

        static int quantidadeDeTipos() { return TIPOS.size(); }
    }

    /** Contexto: objeto leve, só com estado extrínseco + referência ao flyweight. */
    static class Zumbi {
        private final ZumbiTipo tipo;   // referência compartilhada
        private double x, y;
        private int vidaAtual;

        Zumbi(String nomeTipo, double x, double y) {
            this.tipo = FabricaDeTipos.obter(nomeTipo);
            this.x = x;
            this.y = y;
            this.vidaAtual = tipo.getVidaMax();
        }

        void desenhar() { tipo.desenhar(x, y); }

        void levarDano(int valor) { vidaAtual -= valor; }

        boolean estaVivo() { return vidaAtual > 0; }

        ZumbiTipo getTipo() { return tipo; }
    }

    public static void main(String[] args) {
        List<Zumbi> horda = new ArrayList<>();

        for (int i = 0; i < 9000; i++) horda.add(new Zumbi("comum", i, 0));
        for (int i = 0; i < 800; i++)  horda.add(new Zumbi("corredor", i, 10));
        for (int i = 0; i < 200; i++)  horda.add(new Zumbi("tanque", i, 20));

        System.out.println("Zumbis na horda: " + horda.size());
        System.out.println("Tipos na memória: " + FabricaDeTipos.quantidadeDeTipos());

        // Mesma referência: prova de que o flyweight é compartilhado.
        System.out.println("zumbi 0 e zumbi 1 compartilham o tipo? "
                + (horda.get(0).getTipo() == horda.get(1).getTipo()));

        // Estado extrínseco é individual: dano em um não afeta os outros.
        horda.get(0).levarDano(100);
        System.out.println("zumbi 0 vivo? " + horda.get(0).estaVivo());
        System.out.println("zumbi 1 vivo? " + horda.get(1).estaVivo());

        horda.get(0).desenhar();
        horda.get(9500).desenhar();
    }
}
