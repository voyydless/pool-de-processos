import processos.Process;
import processos.ProcessPool;
import processos.ComputingProcess;
import processos.WritingProcess;
import processos.ReadingProcess;
import processos.PrintingProcess;
import processos.Expression;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/* Classe principal que gerencia a interação com o usuário através de um menu, permitindo adicionar,
 executar, salvar e carregar processos do ProcessPool. */
public class Sistema {
    // Nome do arquivo onde a fila de processos será salva/carregada
    private static final String NOME_DE_ARQUIVO_SALVO = "process_pool_state.txt";

    // Inicializa o pool de processos e o loop do menu de interação com o usuário
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Cria o pool de processos com capacidade máxima de 100 processos
        ProcessPool pool = new ProcessPool(100);

        int opcao;
        // Loop principal do menu: continua exibindo opções até o usuário escolher sair
        do {
            displayMenu(); // Exibe as opções do menu para o usuário
            // Solicita e obtém a escolha da opção do usuário, garantindo que seja um número inteiro
            opcao = getIntInput(scanner, "Escolha uma opção: ");

            // Usa um switch para lidar com a opção escolhida pelo usuário
            switch (opcao) {
                case 1: // Opção para adicionar processo
                    addProcessMenu(scanner, pool); // Chama o método para exibir o submenu de adição de processos
                    break;
                case 2: // Opção para executar próximo processo
                    pool.executeNextProcess(); // Chama o método do pool para executar o próximo processo na fila
                    break;
                case 3: // Opção para executar processo por PID;
                    System.out.print("Digite o PID do processo a ser executado: ");
                    int pidToExecute = getIntInput(scanner, ""); // Pede o PID ao usuário
                    pool.executeProcessByPid(pidToExecute); // Chama o método do pool para executar o processo específico
                    break;
                case 4: // Opção para salvar a fila de processos
                    try {
                        pool.saveToFile(NOME_DE_ARQUIVO_SALVO); // Tenta salvar o estado do pool no arquivo definido
                        System.out.println("[SUCESSO] Pool salvo com sucesso em " + NOME_DE_ARQUIVO_SALVO);
                    } catch (IOException e) {
                        // Se houver erro ao salvar, imprime a mensagem de erro;
                        System.err.println("[ERRO] Falha ao salvar pool: " + e.getMessage());
                    }
                    break;
                case 5: // Opção para carregar a fila de processos
                    try {
                        pool.loadFromFile(NOME_DE_ARQUIVO_SALVO); // Tenta carregar o estado do pool do arquivo
                        System.out.println("[SUCESSO] Pool carregado com sucesso de " + NOME_DE_ARQUIVO_SALVO);
                    } catch (IOException e) {
                        // Se houver erro ao carregar, imprime o erro
                        System.err.println("[ERRO] Falha ao carregar pool: " + e.getMessage());
                    }
                    break;
                case 0: // Opção para Sair do programa
                    System.out.println("Saindo da simulação. Até a próxima!");
                    break;
                default: // Caso o usuário digite uma opção que não está no menu
                    System.out.println("Opção inválida. Por favor, tente novamente.");
            }
        } while (opcao != 0); // O loop continua enquanto a opção não for '0'
        scanner.close();
    }

    // Exibe as opções principais disponíveis para o usuário no console
    private static void displayMenu() {
        System.out.println("\n--- Menu do Pool de Processos ---");
        System.out.println("1. Adicionar Processo");
        System.out.println("2. Executar Próximo Processo (FIFO)");
        System.out.println("3. Executar Processo por PID");
        System.out.println("4. Salvar a Fila de Processos");
        System.out.println("5. Carregar a Fila de Processos");
        System.out.println("0. Sair");
        System.out.println("---------------------------------");
    }

    // Exibe um submenu para o usuário escolher qual tipo de processo deseja adicionar
    // Cria a instância do processo escolhido e tenta adicioná-lo ao pool
    private static void addProcessMenu(Scanner scanner, ProcessPool pool) {
        System.out.println("\n--- Adicionar Processo ---");
        System.out.println("1. Processo de Cálculo (ComputingProcess)");
        System.out.println("2. Processo de Gravação (WritingProcess)");
        System.out.println("3. Processo de Leitura (ReadingProcess)");
        System.out.println("4. Processo de Impressão (PrintingProcess)");
        System.out.println("0. Voltar ao Menu Principal");
        System.out.print("Escolha o tipo de processo a adicionar: ");

        int opcaoTipo = getIntInput(scanner, ""); // Obtém a escolha do tipo de processo
        Process newProcess = null; // Variável para armazenar o novo processo a ser criado

        try {
            // Cria um tipo específico de processo baseado na escolha do usuário
            switch (opcaoTipo) {
                case 1: // Cria um processo de cálculo;
                    System.out.print("Digite o primeiro operando: ");
                    double operando1 = getDoubleInput(scanner, ""); // Pede e valida o primeiro operando
                    System.out.print("Digite o segundo operando: ");
                    double operando2 = getDoubleInput(scanner, ""); // Pede e valida o segundo operando
                    System.out.print("Digite o operador (+, -, *, /): ");
                    char operador = scanner.nextLine().trim().charAt(0); // Lê o operador (primeiro caractere da linha)
                    // Formata a expressão como string para passar ao construtor do ComputingProcess
                    String expressionString = String.format("%.2f%c%.2f", operando1, operador, operando2);
                    newProcess = new ComputingProcess(expressionString);
                    break;
                case 2: // Cria um processo de gravação
                    System.out.print("Digite o primeiro operando para gravar: ");
                    double w_operando1 = getDoubleInput(scanner, "");
                    System.out.print("Digite o segundo operando para gravar: ");
                    double w_operando2 = getDoubleInput(scanner, "");
                    System.out.print("Digite o operador para gravar (+, -, *, /): ");
                    char w_operador = scanner.nextLine().trim().charAt(0);
                    // Cria um objeto Expression com os dados fornecidos
                    Expression expressaoParaGravar = new Expression(w_operando1, w_operando2, w_operador);
                    newProcess = new WritingProcess(expressaoParaGravar);
                    break;
                case 3: // Cria um processo de leitura
                    // Passa a referência do pool para o processo de leitura, pois ele interage com o pool
                    newProcess = new ReadingProcess(pool);
                    break;
                case 4: // Cria um processo de impressão
                    // Passa a referência do pool para o processo de impressão, pois ele exibe o estado do pool
                    newProcess = new PrintingProcess(pool);
                    break;
                case 0: // Opção para voltar ao menu principal
                    System.out.println("Voltando ao menu principal.");
                    break;
                default:
                    System.out.println("Tipo de processo inválido.");
            }
        } catch (InputMismatchException e) {
            // Captura erros se o usuário digitar um tipo de entrada incorreto
            System.err.println("Erro de entrada: Entrada inválida. Por favor, digite o formato correto.");
            scanner.nextLine(); // Limpa o buffer do scanner para evitar loop infinito
            newProcess = null; // Garante que nenhum processo inválido seja criado após o erro
        } catch (IllegalArgumentException e) {
            // Captura erros relacionados à criação do processo
            System.err.println("Erro ao criar processo: " + e.getMessage());
            newProcess = null;
        }

        // Se um novo processo válido foi criado, tenta adicioná-lo ao pool
        if (newProcess != null) {
            pool.addProcess(newProcess);
        }
    }

    // Garante que a entrada do usuário seja um número inteiro válido e
    // continua pedindo a entrada até que um inteiro válido seja fornecido
    private static int getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        // Loop continua enquanto a próxima entrada não for um inteiro
        while (!scanner.hasNextInt()) {
            System.out.println("Entrada inválida. Por favor, digite um número inteiro.");
            scanner.next(); // Consome a entrada inválida para evitar repetição
            System.out.print(prompt); // Repete a mensagem de prompt
        }
        int value = scanner.nextInt(); // Lê o número inteiro
        scanner.nextLine(); // Consome a quebra de linha
        return value;
    }

    // Garante que a entrada do usuário seja um número decimal válido
    // Aceita tanto o ponto quanto a vírgula como separador decimal
    private static double getDoubleInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String inputLine;
        // Loop infinito até que uma entrada double válida seja fornecida
        while (true) {
            inputLine = scanner.nextLine(); // Lê a linha completa como string
            try {
                // Tenta converter a string para double, substituindo vírgulas por pontos antes do parse
                return Double.parseDouble(inputLine.replace(',', '.'));
            } catch (NumberFormatException e) {
                // Se a conversão falhar, imprime uma mensagem de erro e pede novamente
                System.out.println("Entrada inválida. Se for digitar um decimal, utilize '.' ou ','.");
                System.out.print(prompt);
            }
        }
    }
}