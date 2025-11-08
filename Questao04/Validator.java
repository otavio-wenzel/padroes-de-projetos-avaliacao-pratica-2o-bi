package Questao04;

public interface Validator {
    String id();
    boolean requiresAllPreviousPass(); // p/ validadores 3 e 5 (rodar só se ninguém falhou antes)
    ValidationResult validate(NFEDocument nfe, ValidationContext ctx);
}