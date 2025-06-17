package processos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* Representa um processo que lê expressões matemáticas de um arquivo e as transforma em novos processos de cálculo,
 adicionando-os ao pool principal. Após a leitura, o arquivo de origem é limpo. */
public class ReadingProcess extends Process {
    private ProcessPool processPool; // Referência ao pool principal para adicionar processos lidos
    private static final String NOME_ARQUIVO = "expressions_queue.txt"; // Arquivo de onde as expressões serão lidas

    // Construtor
    public ReadingProcess(ProcessPool processPool) {
        super(); // Chama o construtor da superclasse Process para gerar um PID
        this.processPool = processPool;
    }

    // Construtor para recriar um processo de leitura a partir de um arquivo
    public ReadingProcess(ProcessPool processPool, int pid) {
        super(pid); // Restaura o PID
        this.processPool = processPool;
    }

    // Executa a lógica do processo de leitura.
    // Lê expressões do arquivo e cria ComputingProcess para cada uma
    // adicionando ele no ProcessPool. Finalmente, limpa o arquivo
    @Override
    public void execute() {
        System.out.println("Executando Processo de Leitura (PID: " + pid + "): Lendo expressões do arquivo '" + NOME_ARQUIVO + "' e adicionando-as ao pool...");
        int processesAdded = 0;
        // Abre o arquivo para leitura, usando BufferedReader para ler linha por linha;
        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String line;
            // Lê cada linha do arquivo até o fim
            while ((line = reader.readLine()) != null) {
                // Se a linha estiver vazia, ignora e vai para a próxima
                if (line.trim().isEmpty()) continue;

                try {
                    // Cria um novo processo de cálculo com a expressão lida da linha
                    ComputingProcess newComputingProcess = new ComputingProcess(line);
                    // Adiciona o novo processo de cálculo à fila do pool
                    processPool.addProcess(newComputingProcess);
                    processesAdded++; // Incrementa o contador de processos adicionados
                } catch (IllegalArgumentException e) {
                    // Captura e imprime erros se a linha lida não for uma expressão válida
                    System.err.println("Erro ao criar processo de cálculo da linha '" + line + "': " + e.getMessage());
                }
            }
            System.out.println(processesAdded + " processos de cálculo adicionados ao pool.");
        } catch (IOException e) {
            // Lida com erros de leitura do arquivo (ex: arquivo não encontrado, permissão);
            System.err.println("Erro ao ler o arquivo '" + NOME_ARQUIVO + "': " + e.getMessage());
            // O bloco finally sempre será executado, independentemente de erros ou não
        } finally {
            // Limpa o arquivo de expressões após a leitura
            System.out.println("Limpando o arquivo '" + NOME_ARQUIVO + "'...");
            // Abre o arquivo para escrita, sobrescrevendo seu conteúdo com nada
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO, false))) {
                System.out.println("Arquivo '" + NOME_ARQUIVO + "' limpo.");
            } catch (IOException e) {
                // Lida com erros se não for possível limpar o arquivo
                System.err.println("Erro ao limpar o arquivo '" + NOME_ARQUIVO + "': " + e.getMessage());
            }
        }
        System.out.println("Processo de Leitura (PID: " + pid + ") concluído.");
    }

    // Retorna uma representação em string deste processo, incluindo seu PID
    @Override
    public String toString() {
        return "Processo de Leitura (PID: " + pid + ")";
    }

    // Retorna uma string formatada contendo apenas o tipo do processo e o PID
    @Override
    public String toFileString() {
        return getType() + "|" + pid;
    }

    // Getter
    // Retorna o tipo deste processo
    @Override
    public String getType() {
        return "ReadingProcess";
    }
}