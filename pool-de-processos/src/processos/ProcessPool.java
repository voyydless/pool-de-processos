package processos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/* Gerencia uma fila de processos, permitindo adicionar, executar e persistir o estado.
 Implementa a fila usando um array, com capacidade máxima definida. */
public class ProcessPool {
    private Process[] fila; // O array que armazena os processos na fila
    private int tamanho; // Número atual de processos na fila
    private final int CAPACIDADE_MAXIMA; // Capacidade máxima da fila

    // Construtor
    public ProcessPool(int capacidadeMaxima) {
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("A capacidade máxima deve ser maior que zero!");
        }
        this.CAPACIDADE_MAXIMA = capacidadeMaxima;
        this.fila = new Process[CAPACIDADE_MAXIMA];
        this.tamanho = 0; // Inicia a fila vazia
    }

    // Adiciona um novo processo ao final da fila;
    // Retorna true se o processo foi adicionado com sucesso, false se a fila estiver cheia
    public boolean addProcess(Process process) {
        // Verifica se há espaço na fila;
        if (tamanho < CAPACIDADE_MAXIMA) {
            fila[tamanho] = process; // Adiciona o processo na próxima posição livre;
            tamanho++; // Incrementa o contador de processos na fila
            System.out.println(">>> " + process.toString() + " adicionado à fila.");
            return true;
        } else {
            System.out.println("Fila de processos cheia. Não é possível adicionar mais processos.");
            return false;
        }
    }

    // Executa o próximo processo disponível na fila (seguindo a ordem first in first out);
    // Retorna o processo que foi executado, ou null se a fila estiver vazia.
    public Process executeNextProcess() {
        // Se a fila estiver vazia, não há o que executar.
        if (tamanho == 0) {
            System.out.println("Fila de processos vazia. Nada para executar.");
            return null;
        }

        System.out.println("\n--- Executando próximo processo na fila (FIFO) ---");
        Process processToExecute = fila[0]; // Pega o primeiro processo da fila;
        processToExecute.execute(); // Manda o processo se executar
        System.out.println("-------------------------------------------------\n");

        shiftLeft(0); // Remove o processo executado, movendo os outros para a esquerda
        return processToExecute;
    }

    // Executa um processo específico da fila, identificado pelo seu PID. Ele é executado e removido da fila;
    // Retorna o processo que foi executado, ou null se o PID não for encontrado ou a fila estiver vazia
    public Process executeProcessByPid(int pid) {
        // Se a fila estiver vazia, não há o que executar.
        if (tamanho == 0) {
            System.out.println("Fila de processos vazia. Não há PID " + pid + " para executar.");
            return null;
        }

        int indiceEncontrado = -1;
        // Percorre a fila para encontrar o processo com o PID desejado;
        for (int i = 0; i < tamanho; i++) {
            if (fila[i].getPid() == pid) {
                indiceEncontrado = i; // Guarda o índice do processo encontrado;
                break; // Sai do loop assim que encontrar
            }
        }

        // Se o processo foi encontrado:
        if (indiceEncontrado != -1) {
            System.out.println("\n--- Executando processo (PID: " + pid + ") solicitado ---");
            Process processToExecute = fila[indiceEncontrado]; // Pega o processo encontrado;
            processToExecute.execute(); // Manda o processo se executar
            System.out.println("--------------------------------------------------\n");

            shiftLeft(indiceEncontrado); // Remove o processo executado, movendo os outros para a esquerda;
            return processToExecute;
        } else {
            System.out.println("Processo com PID " + pid + " não encontrado na fila.");
            return null;
        }
    }

    // Desloca os elementos do array para a esquerda, preenchendo o espaço deixado por um processo removido
    private void shiftLeft(int indiceInicial) {
        // Move cada processo uma posição para a esquerda, a partir do índice do processo removido
        for (int i = indiceInicial; i < tamanho - 1; i++) {
            fila[i] = fila[i + 1];
        }
        fila[tamanho - 1] = null; // Limpa a última posição (que agora está duplicada ou vazia);
        tamanho--; // Decrementa o contador de processos
    }

    // Exibe o estado atual da fila de processos, mostrando cada processo e seu índice
    public void mostrarFila() {
        // Se a fila estiver vazia, imprime uma mensagem
        if (tamanho == 0) {
            System.out.println("A fila de processos está vazia.");
            return;
        }
        System.out.println("\n--- Fila de Processos (" + tamanho + "/" + CAPACIDADE_MAXIMA + ") ---");
        // Itera sobre a fila e imprime cada processo
        for (int i = 0; i < tamanho; i++) {
            System.out.println("  [" + i + "] " + fila[i]); // Usa o toString de cada processo
        }
        System.out.println("------------------------------------\n");
    }

    // Verifica se a fila de processos está vazia.
    public boolean isEmpty() {
        return tamanho == 0;
    }

    // Salva o estado atual da fila de processos em um arquivo.
    // A primeira linha do arquivo armazena a quantidade de processos,
    // e as linhas seguintes armazenam cada processo em um formato específico
    public void saveToFile(String filename) throws IOException {
        System.out.println("Salvando a fila de processos para '" + filename + "'...");
        // Abre o arquivo para escrita;
        // O 'false' indica que o arquivo será sobrescrito/apagado se já existir, iniciando a escrita do zero
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, false))) {
            writer.write(String.valueOf(tamanho)); // Escreve o número atual de processos na primeira linha
            writer.newLine(); // Adiciona uma quebra de linha

            // Itera sobre cada processo na fila
            for (int i = 0; i < tamanho; i++) {
                writer.write(fila[i].toFileString()); // Pede para o próprio processo gerar sua string para arquivo
                writer.newLine(); // Adiciona uma quebra de linha
            }
            System.out.println("Fila de processos salva com sucesso (" + tamanho + " processos).");
        }
    }

    // Carrega processos de um arquivo para a fila, substituindo a fila atual.
    public void loadFromFile(String filename) throws IOException {
        System.out.println("Carregando fila de processos de '" + filename + "'...");
        File file = new File(filename);
        // Verifica se o arquivo existe antes de tentar ler;
        if (!file.exists()) {
            throw new IOException("Arquivo " + filename + " não encontrado.");
        }

        limparFila(); // Limpa a fila atual antes de carregar novos processos.

        // Abre o arquivo para leitura;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line = reader.readLine(); // Lê a primeira linha que tem quantidade esperada de processos;
            // Se o arquivo estiver vazio, nada para carregar.
            if (line == null) {
                System.out.println("Arquivo vazio, nenhum processo para carregar.");
                return;
            }

            int contagemEsperada = Integer.parseInt(line.trim()); // Converte a string da primeira linha para int
            int contagemCarregada = 0; // Contador de processos realmente carregados
            int maiorPidCarregado = 0; // Usado para ajustar o próximo PID a ser gerado após o carregamento.

            // Itera para ler cada linha de processo esperada;
            for (int i = 0; i < contagemEsperada; i++) {
                line = reader.readLine();
                // Se o arquivo terminar antes do esperado, imprime um aviso.
                if (line == null) {
                    System.out.println("Aviso: Arquivo corrompido - esperados " + contagemEsperada + " processos, mas encontrou apenas " + contagemCarregada);
                    break;
                }

                Process loadedProcess = parseProcessFromFileString(line); // Cria um objeto Process a partir da linha
                // Se o processo foi criado com sucesso:
                if (loadedProcess != null) {
                    // Tenta adicionar o processo ao pool
                    if (addProcess(loadedProcess)) {
                        contagemCarregada++; // Incrementa o contador de processos carregados
                        // Atualiza o PID máximo encontrado
                        if (loadedProcess.getPid() > maiorPidCarregado) {
                            maiorPidCarregado = loadedProcess.getPid();
                        }
                    } else {
                        System.err.println("Aviso: Não foi possível adicionar o processo carregado (fila cheia): " + loadedProcess.getClass().getSimpleName() + " - " + line);
                        break; // Se a fila do pool ficar cheia durante o carregamento, para o processo.
                    }
                } else {
                    System.err.println("Aviso: Falha ao parsear processo da linha (ignorado): " + line);
                }
            }
            Process.setNextPid(maiorPidCarregado + 1); // Ajusta o contador global de PID para continuar do maior PID carregado + 1

            System.out.println("Fila de processos carregada com sucesso (" + contagemCarregada + " processos adicionados).");
        }
    }

    // Método para criar um objeto Process a partir de uma linha de texto lida de arquivo;
    // Interpreta o tipo e os dados do processo na linha para instanciar a subclasse do objeto correto
    private Process parseProcessFromFileString(String line) {
        // Divide a linha usando '|' como delimitador, esperando no máximo 3 partes (Tipo|PID|DadosAdicionais)
        String[] parts = line.split("\\|", 3);
        // Se não houver pelo menos Tipo e PID, a linha é inválida
        if (parts.length < 2) {
            System.err.println("Linha inválida no arquivo: " + line);
            return null;
        }

        String tipo = parts[0]; // A primeira parte é o tipo do processo
        int pidFromFile = Integer.parseInt(parts[1]); // A segunda parte é o PID do processo

        Process process = null;

        try {
            // Usa um switch para criar a subclasse correta com base no tipo lido;
            switch (tipo) {
                case "ComputingProcess":
                    // Para ComputingProcess, espera-se uma terceira parte com a expressão
                    if (parts.length >= 3) {
                        process = new ComputingProcess(parts[2], pidFromFile);
                    } else {
                        System.err.println("Erro: Linha de ComputingProcess sem expressão: " + line);
                    }
                    break;
                case "WritingProcess":
                    // Para WritingProcess, espera-se uma terceira parte com a expressão
                    if (parts.length >= 3) {
                        Expression expr = Expression.parseFileString(parts[2]);
                        process = new WritingProcess(expr, pidFromFile);
                    } else {
                        System.err.println("Erro: Linha de WritingProcess sem expressão: " + line);
                    }
                    break;
                case "ReadingProcess":
                    // ReadingProcess e PrintingProcess precisam da referência do próprio pool no construtor
                    process = new ReadingProcess(this, pidFromFile);
                    break;
                case "PrintingProcess":
                    // ReadingProcess e PrintingProcess precisam da referência do próprio pool no construtor
                    process = new PrintingProcess(this, pidFromFile);
                    break;
                default:
                    System.err.println("Tipo de processo desconhecido ao carregar: " + tipo + " na linha: " + line);
            }
        } catch (IllegalArgumentException e) {
            // Captura erros que podem ocorrer ao parsear os dados específicos do processo (ex: expressão inválida)
            System.err.println("Erro ao criar processo do tipo " + tipo + " a partir do arquivo: " + e.getMessage() + " na linha: " + line);
        }
        return process;
    }

    // Limpa completamente a fila de processos, resetando o pool para um estado vazio
    public void limparFila() {
        // Define todas as posições do array como null;
        for (int i = 0; i < tamanho; i++) {
            fila[i] = null;
        }
        tamanho = 0; // Zera o contador de processos
        System.out.println("Fila de processos limpa.");
    }

    // Getter
    // Retorna o número atual de processos na fila.
    public int getTamanho() { return tamanho; }
}