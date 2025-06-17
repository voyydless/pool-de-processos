package processos;

// Representa um processo que realiza cálculos de expressões matemáticas
public class ComputingProcess extends Process {
    private Expression expression;
    private String entradaOriginal; // A expressão original para salvar no arquivo

    // Construtor
    public ComputingProcess(String entradaOriginal) {
        super(); // Chama o construtor da superclasse Process para gerar um PID
        this.entradaOriginal = entradaOriginal;
        // Cria um objeto Expression a partir da string de entrada
        this.expression = Expression.parseFileString(entradaOriginal);
    }

    // Construtor para recriar um processo de cálculo a partir de um arquivo
    // Restaura o PID existente e a expressão original
    public ComputingProcess(String entradaOriginal, int pid) {
        super(pid); // Chama o construtor da superclasse Process para restaurar o PID
        this.entradaOriginal = entradaOriginal;
        // Cria um objeto Expression a partir da string de entrada restaurada.
        this.expression = Expression.parseFileString(entradaOriginal);
    }

     // Executa a lógica do processo de cálculo, realizando a operação e imprimindo o resultado
    @Override
    public void execute() {
        System.out.println("Executando Processo de Cálculo (PID: " + pid + "): Calculando '" + expression + "'...");
        try {
            // Chama o método calculate() do objeto Expression para obter o resultado;
            double resultado = expression.calculate();
            // Imprime o resultado formatado
            System.out.printf("Resultado (PID: %d): %.2f %c %.2f = %.2f%n", pid, expression.getOperando1(), expression.getOperador(), expression.getOperando2(), resultado);
        } catch (ArithmeticException e) {
            // Captura e imprime erros de cálculo, como divisão por zero.
            System.err.println("Erro no Processo de Cálculo (PID: " + pid + "): " + e.getMessage());
        }
        System.out.println("Processo de Cálculo (PID: " + pid + ") concluído.");
    }

    // Retorna uma representação em string deste processo, incluindo seu PID e a expressão
    @Override
    public String toString() {
        return "Processo de Cálculo (PID: " + pid + ") - Expressão: " + expression;
    }

    // Retorna uma string formatada contendo o tipo do processo, PID e a expressão original
    @Override
    public String toFileString() {
        return getType() + "|" + pid + "|" + entradaOriginal;
    }

    // Getters
    public Expression getExpression() { return expression; }
    // Retorna o tipo deste processo
    @Override
    public String getType() { return "ComputingProcess"; }
}