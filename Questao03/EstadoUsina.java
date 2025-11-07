package Questao03;

public interface EstadoUsina {EstadoId avaliar(UsinaNuclear ctx, Medidas medidas);
    
    default String nome() { return this.getClass().getSimpleName(); }

}