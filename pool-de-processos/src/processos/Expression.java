package processos;

/* Classe é responsável por armazenar a expressão e realizar o seu cálculo,
  simplificando a classe ComputingProcess, que usa ele para executar operações. */
public class Expression {
    private double operando1;
    private double operando2;
    private char operador;
    private String entradaOriginal; // Guarda a string de entrada original para consistência ao salvar!

    // Construtor para criar uma nova expressão
    public Expression(double operando1, double operando2, char operador) {
        this.operando1 = operando1;
        this.operando2 = operando2;
        this.operador = operador;
        this.entradaOriginal = String.format("%.2f%c%.2f", operando1, operador, operando2); // Formato para arquivo
        // Validação básica do operador
        if (operador != '+' && operador != '-' && operador != '*' && operador != '/') {
            throw new IllegalArgumentException("Operador inválido: " + operador + ". Use +, -, * ou /.");
        }
    }

    // Realiza o cálculo da expressão inserida
    public double calculate() {
        return switch (operador) {
            case '+' -> operando1 + operando2;
            case '-' -> operando1 - operando2;
            case '*' -> operando1 * operando2;
            case '/' -> {
                if (operando2 == 0) {
                    throw new ArithmeticException("Divisão por zero não permitida.");
                }
                yield operando1 / operando2;
            }
            default -> throw new IllegalStateException("Operador desconhecido: " + operador); // Não deveria acontecer devido à validação no construtor
        };
    }

    // Retorna uma representação em String da expressão, formatada para exibição melhor ao usuário
    @Override
    public String toString() {
        return String.format("%.2f %c %.2f", operando1, operador, operando2);
    }

    // Retorna a string original da expressão, formatada de modo compacto para gravação em arquivo
    public String toFileString() {
        return this.entradaOriginal;
    }

     // Tenta interpretar uma string para criar um novo objeto Expression.
     // Sendo um método estático, ele é útil para reconstruir expressões a partir de dados lidos de um arquivo.
    public static Expression parseFileString(String fileString) {
        char operator = ' ';
        int operatorIndex = -1;

        // Procura o operador (+, -, *, /) na string;
        // Se encontrar, guarda o operador e seu índice;
        // Prioriza o primeiro operador encontrado caso tenha múltiplos
        for (char op : new char[]{'+', '-', '*', '/'}) {
            int idx = fileString.indexOf(op);
            if (idx != -1) {
                if (operatorIndex == -1 || idx < operatorIndex) {
                    operator = op;
                    operatorIndex = idx;
                }
            }
        }
        // Se nenhum operador for encontrado, a expressão é inválida
        if (operatorIndex == -1) {
            throw new IllegalArgumentException("Expressão inválida: Operador não encontrado em '" + fileString + "'");
        }

        try {
            // Extrai a parte antes do operador como primeiro operando e a parte depois do operador como segundo operando;
            // Substitui vírgulas por pontos para garantir o parse correto para double
            String op1Str = fileString.substring(0, operatorIndex).replace(',', '.');
            String op2Str = fileString.substring(operatorIndex + 1).replace(',', '.');

            // Converte as strings dos operandos para double;
            // Cria e retorna uma nova Expression com os valores e o operador encontrados
            double op1 = Double.parseDouble(op1Str);
            double op2 = Double.parseDouble(op2Str);
            return new Expression(op1, op2, operator);
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Expressão inválida: Erro ao parsear operandos em '" + fileString + "'", e);
        }
    }

    // Getters
    public double getOperando1() { return operando1; }
    public double getOperando2() { return operando2; }
    public char getOperador() { return operador; }
}