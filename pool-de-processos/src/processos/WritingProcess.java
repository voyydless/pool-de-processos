package processos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/* Representa um processo que grava uma expressão matemática em um arquivo de texto.
 Este processo adiciona novas expressões ao final do arquivo. */
public class WritingProcess extends Process {
    private Expression expression;
    private static final String NOME_ARQUIVO = "expressions_queue.txt"; // Arquivo para gravar expressões

    // Construtor
    public WritingProcess(Expression expression) {
        super(); // Chama o construtor da superclasse Process para gerar um PID
        this.expression = expression;
    }

    // Construtor para recriar um processo de gravação a partir de um arquivo
    public WritingProcess(Expression expression, int pid) {
        super(pid); // Restaura o PID
        this.expression = expression;
    }

    // Executa a lógica do processo de gravação
    // Grava a expressão configurada no arquivo 'expressions_queue.txt', adicionando ela ao final
    @Override
    public void execute() {
        System.out.println("Executando Processo de Gravação (PID: " + pid + "): Gravando expressão '" + expression + "' no arquivo '" + NOME_ARQUIVO + "'...");
        // Abre o arquivo para escrita
        // O 'true' indica que os novos dados serão adicionados ao final do arquivo, sem apagar o conteúdo existente
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO, true))) {
            writer.write(expression.toFileString()); // Escreve a expressão formatada no arquivo
            writer.newLine(); // Adiciona uma nova linha após a expressão gravada
            System.out.println("Expressão gravada com sucesso.");
            Thread.sleep(800);
        } catch (IOException e) {
            // Lida com erros de gravação no arquivo (ex: permissão, disco cheio);
            System.err.println("Erro ao gravar no arquivo '" + NOME_ARQUIVO + "': " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Processo de Gravação (PID: " + pid + ") concluído.");
    }

    // Retorna uma representação em string deste processo
    @Override
    public String toString() {
        return "Processo de Gravação (PID: " + pid + ") - Expressão: " + expression;
    }

    // Retorna uma string formatada contendo o tipo do processo, PID e a expressão a ser gravada
    @Override
    public String toFileString() {
        // Salva o tipo, PID e a expressão formatada para que possa ser recriada;
        return getType() + "|" + pid + "|" + expression.toFileString();
    }

    // Getter
    // Retorna o objeto expression que este processo vai gravar
    public Expression getExpression() {
        return expression;
    }
    // Retorna o tipo deste processo
    @Override
    public String getType() {
        return "WritingProcess";
    }
}